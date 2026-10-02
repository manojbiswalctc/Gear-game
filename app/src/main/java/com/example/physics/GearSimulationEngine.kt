package com.example.physics

import com.example.model.Gear
import com.example.model.LevelObjectiveType
import kotlin.math.abs
import kotlin.math.hypot

enum class MeshStatus {
    PERFECT_MESH,
    COLLISION_TOO_CLOSE,
    LOOSE_TOO_FAR,
    NO_MESH
}

data class SimulationResult(
    val updatedGears: List<Gear>,
    val isSystemOverloaded: Boolean,
    val overloadMessage: String? = null,
    val targetSatisfied: Boolean = false,
    val activeMeshCount: Int = 0,
    val energyPulsePairs: List<Pair<String, String>> = emptyList()
)

class GearSimulationEngine {

    companion object {
        const val MESH_TOLERANCE = 18f // Tolerance window for tooth interlocking in board units
        const val MIN_CLEARANCE_RATIO = 0.85f // Below this is physical collision / binding
        const val MAGNETIC_GAP_MAX = 45f // For magnetic gear air-gap coupling
    }

    /**
     * Test whether two gears can physically mesh.
     */
    fun checkMeshStatus(gearA: Gear, gearB: Gear): MeshStatus {
        val dist = hypot(gearA.x - gearB.x, gearA.y - gearB.y)

        // Check layer compatibility
        val canMeshLayers = if (gearA.type.isCompound || gearB.type.isCompound) {
            // Compound / double gears can bridge Layer 1 and Layer 2
            abs(gearA.layer - gearB.layer) <= 1
        } else {
            gearA.layer == gearB.layer
        }

        if (!canMeshLayers) return MeshStatus.NO_MESH

        // If one is magnetic, it can mesh across air gap
        if (gearA.type.isMagnetic || gearB.type.isMagnetic) {
            val idealDist = gearA.radius + gearB.radius
            if (dist >= idealDist * 0.9f && dist <= idealDist + MAGNETIC_GAP_MAX) {
                return MeshStatus.PERFECT_MESH
            }
        }

        // Determine effective radii (handling compound step)
        val radiusA = if (gearA.type.isCompound && gearB.layer == gearA.secondRingLayer) {
            gearA.secondRingRadius
        } else {
            gearA.radius
        }

        val radiusB = if (gearB.type.isCompound && gearA.layer == gearB.secondRingLayer) {
            gearB.secondRingRadius
        } else {
            gearB.radius
        }

        val idealDist = radiusA + radiusB

        return when {
            dist < idealDist * MIN_CLEARANCE_RATIO -> MeshStatus.COLLISION_TOO_CLOSE
            abs(dist - idealDist) <= MESH_TOLERANCE -> MeshStatus.PERFECT_MESH
            dist < idealDist + MESH_TOLERANCE * 1.8f -> MeshStatus.LOOSE_TOO_FAR
            else -> MeshStatus.NO_MESH
        }
    }

    /**
     * Compute kinematics across the network starting from the power source(s).
     */
    fun computeKinematics(
        gears: List<Gear>,
        isPowerOn: Boolean,
        objectiveType: LevelObjectiveType
    ): SimulationResult {
        if (!isPowerOn) {
            // Machine powered off: all target velocities ramp to 0
            val stoppedGears = gears.map { g ->
                g.copy(
                    isPowered = false,
                    targetRpm = 0f,
                    direction = 0,
                    meshedWith = emptyList(),
                    isOverloaded = false,
                    isSlipping = false
                )
            }
            return SimulationResult(
                updatedGears = stoppedGears,
                isSystemOverloaded = false,
                targetSatisfied = false
            )
        }

        // Map gears by ID for quick lookup and mutation
        val gearMap = gears.associateBy { it.id }.toMutableMap()
        val visited = mutableSetOf<String>()
        val meshedPairs = mutableListOf<Pair<String, String>>()
        var hasConflict = false
        var conflictReason: String? = null

        // Power sources drive the network
        val sourceGears = gears.filter { it.isSource }
        val queue = ArrayDeque<Gear>()

        for (source in sourceGears) {
            val driverRpm = 60f
            val driverDir = 1 // Clockwise driver
            gearMap[source.id] = source.copy(
                isPowered = true,
                targetRpm = driverRpm,
                direction = driverDir,
                meshedWith = emptyList()
            )
            visited.add(source.id)
            queue.add(gearMap[source.id]!!)
        }

        // BFS traversal propagating angular velocity and direction
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()

            for (other in gears) {
                if (other.id == current.id) continue

                val meshStatus = checkMeshStatus(current, other)

                if (meshStatus == MeshStatus.COLLISION_TOO_CLOSE) {
                    hasConflict = true
                    conflictReason = "Mechanical collision: gears too close and binding!"
                    gearMap[other.id] = other.copy(isOverloaded = true)
                    gearMap[current.id] = current.copy(isOverloaded = true)
                } else if (meshStatus == MeshStatus.PERFECT_MESH) {
                    meshedPairs.add(current.id to other.id)

                    // Direction inversion: external gears mesh in opposite directions
                    // (Unless reversal gear mechanism handles it)
                    val expectedDir = if (other.type.isReversal || current.type.isReversal) {
                        current.direction
                    } else {
                        -current.direction
                    }

                    // Gear ratio: RPM_out = RPM_in * (Teeth_in / Teeth_out)
                    val teethIn = if (current.type.isCompound && other.layer == current.secondRingLayer) {
                        current.secondRingTeeth
                    } else {
                        current.teethCount
                    }

                    val teethOut = if (other.type.isCompound && current.layer == other.secondRingLayer) {
                        other.secondRingTeeth
                    } else {
                        other.teethCount
                    }

                    val ratio = teethIn.toFloat() / teethOut.toFloat()
                    val calculatedRpm = current.targetRpm * ratio

                    if (visited.contains(other.id)) {
                        // Check for conflicting mechanical drive (binding loop)
                        val existing = gearMap[other.id]!!
                        if (existing.direction != 0 && existing.direction != expectedDir) {
                            hasConflict = true
                            conflictReason = "System Jam: conflicting rotation vectors on same gear!"
                            gearMap[other.id] = existing.copy(isOverloaded = true)
                            gearMap[current.id] = current.copy(isOverloaded = true)
                        }
                    } else {
                        visited.add(other.id)
                        val updatedOther = other.copy(
                            isPowered = true,
                            targetRpm = calculatedRpm,
                            direction = expectedDir,
                            isOverloaded = false,
                            isSlipping = false
                        )
                        gearMap[other.id] = updatedOther
                        queue.add(updatedOther)
                    }
                }
            }
        }

        // Update meshedWith lists
        val finalList = gearMap.values.map { gear ->
            val connectedIds = meshedPairs.filter { it.first == gear.id || it.second == gear.id }
                .map { if (it.first == gear.id) it.second else it.first }
            gear.copy(
                meshedWith = connectedIds,
                isPowered = visited.contains(gear.id) && !hasConflict
            )
        }

        // Check if objective is satisfied
        val targets = finalList.filter { it.isTarget }
        val targetSatisfied = if (targets.isNotEmpty() && !hasConflict) {
            targets.all { target ->
                target.isPowered && (target.targetRequiredDirection == 0 || target.direction == target.targetRequiredDirection)
            }
        } else false

        return SimulationResult(
            updatedGears = finalList,
            isSystemOverloaded = hasConflict,
            overloadMessage = conflictReason,
            targetSatisfied = targetSatisfied,
            activeMeshCount = meshedPairs.size,
            energyPulsePairs = meshedPairs
        )
    }

    /**
     * Advance animation frame with realistic acceleration / deceleration inertia.
     * dt: delta time in seconds (e.g. 0.016f for 60fps)
     */
    fun advanceAnimationFrame(
        gears: List<Gear>,
        dt: Float
    ): List<Gear> {
        return gears.map { gear ->
            val mass = gear.material.massMultiplier
            // Inertia rate: heavy materials ramp slower
            val rampRate = (12f / mass).coerceIn(2f, 25f)

            // Smooth RPM interpolation (acceleration/deceleration)
            val newRpm = if (gear.isPowered && !gear.isOverloaded) {
                gear.currentRpm + (gear.targetRpm - gear.currentRpm) * (rampRate * dt).coerceAtMost(1f)
            } else {
                gear.currentRpm + (0f - gear.currentRpm) * (rampRate * 1.5f * dt).coerceAtMost(1f)
            }

            // Angular step: degrees per second = RPM * 360 / 60 = RPM * 6
            val angularStep = (newRpm * 6f * dt) * gear.direction
            val newAngle = (gear.rotationAngle + angularStep) % 360f

            gear.copy(
                currentRpm = if (newRpm < 0.2f) 0f else newRpm,
                rotationAngle = if (newAngle < 0f) newAngle + 360f else newAngle
            )
        }
    }
}
