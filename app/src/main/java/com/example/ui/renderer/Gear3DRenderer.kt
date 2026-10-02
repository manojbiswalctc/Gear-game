package com.example.ui.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.model.Gear
import com.example.model.GearMaterial
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object Gear3DRenderer {

    /**
     * Draw a complete photorealistic 3D gear at projected coordinates.
     */
    fun drawGear(
        scope: DrawScope,
        gear: Gear,
        center: Offset,
        camera: Camera3DState,
        scale: Float = 1.0f
    ) {
        val effectiveRadius = gear.radius * scale * camera.zoom
        val teeth = gear.teethCount
        val rotAngle = gear.rotationAngle
        val foreshorten = camera.foreshorteningY
        val thickness = (gear.thickness * camera.zoom * 0.7f).coerceAtLeast(4f)
        val shadowOffsetY = thickness * 2.2f + (2 - gear.layer) * 8f

        // 1. Drop Shadow onto machine bedplate
        drawGearShadow(scope, center.copy(y = center.y + shadowOffsetY), effectiveRadius, foreshorten)

        // 2. 3D Gear Thickness / Extruded Side Walls
        drawGearExtrusionSide(
            scope = scope,
            center = center,
            radius = effectiveRadius,
            teeth = teeth,
            rotationAngle = rotAngle,
            thickness = thickness,
            foreshorten = foreshorten,
            material = gear.material
        )

        // 3. Top Gear Face (Face Teeth, Rim, Spoke Cutouts, Center Hub)
        scope.rotate(rotAngle, pivot = center) {
            drawGearTopFace(
                scope = this,
                center = center,
                radius = effectiveRadius,
                teeth = teeth,
                gear = gear,
                material = gear.material
            )
        }

        // 4. Overload Jam Warning or Target Satisfied Aura
        if (gear.isOverloaded) {
            drawOverloadSparks(scope, center, effectiveRadius)
        } else if (gear.isTarget && gear.isTargetSatisfied()) {
            drawTargetEnergyGlow(scope, center, effectiveRadius)
        } else if (gear.isPowered && gear.currentRpm > 2f) {
            drawKineticEnergyPulse(scope, center, effectiveRadius)
        }
    }

    private fun drawGearShadow(
        scope: DrawScope,
        center: Offset,
        radius: Float,
        foreshorten: Float
    ) {
        val shadowBrush = Brush.radialGradient(
            colors = listOf(Color(0x75000000), Color(0x30000000), Color(0x00000000)),
            center = center,
            radius = radius * 1.25f
        )
        scope.drawOval(
            brush = shadowBrush,
            topLeft = Offset(center.x - radius * 1.15f, center.y - radius * 1.15f * foreshorten),
            size = Size(radius * 2.3f, radius * 2.3f * foreshorten)
        )
    }

    private fun drawGearExtrusionSide(
        scope: DrawScope,
        center: Offset,
        radius: Float,
        teeth: Int,
        rotationAngle: Float,
        thickness: Float,
        foreshorten: Float,
        material: GearMaterial
    ) {
        // Draw bottom rim rim/shelf to give realistic 3D cylinder thickness
        val sideBrush = Brush.verticalGradient(
            colors = listOf(
                material.shadowColor.copy(alpha = 0.95f),
                material.primaryColor.copy(alpha = 0.7f),
                material.shadowColor.copy(alpha = 0.95f)
            ),
            startY = center.y,
            endY = center.y + thickness
        )

        scope.drawOval(
            brush = sideBrush,
            topLeft = Offset(center.x - radius, center.y - radius * foreshorten + thickness),
            size = Size(radius * 2f, radius * 2f * foreshorten)
        )
    }

    private fun drawGearTopFace(
        scope: DrawScope,
        center: Offset,
        radius: Float,
        teeth: Int,
        gear: Gear,
        material: GearMaterial
    ) {
        val toothDepth = radius * 0.18f
        val rootRadius = radius - toothDepth
        val outerRadius = radius + toothDepth * 0.6f

        // Generate accurate involute gear tooth profile path
        val gearPath = Path()
        val totalSteps = teeth * 2
        for (i in 0 until totalSteps) {
            val angleStep = (2 * PI / totalSteps).toFloat()
            val angle1 = i * angleStep
            val angle2 = (i + 1) * angleStep
            val isTooth = i % 2 == 0

            val currentR = if (isTooth) outerRadius else rootRadius
            val x1 = center.x + currentR * cos(angle1)
            val y1 = center.y + currentR * sin(angle1)

            if (i == 0) {
                gearPath.moveTo(x1, y1)
            } else {
                gearPath.lineTo(x1, y1)
            }
        }
        gearPath.close()

        // Metallic Top Face Gradient
        val metallicBrush = Brush.radialGradient(
            colors = listOf(
                material.highlightColor,
                material.primaryColor,
                material.shadowColor
            ),
            center = center.copy(x = center.x - radius * 0.25f, y = center.y - radius * 0.25f),
            radius = radius * 1.3f
        )
        scope.drawPath(path = gearPath, brush = metallicBrush, style = Fill)

        // Specular Rim Chamfer Bevel
        val chamferBrush = Brush.linearGradient(
            colors = listOf(
                material.highlightColor.copy(alpha = 0.9f),
                material.shadowColor.copy(alpha = 0.5f),
                material.highlightColor.copy(alpha = 0.8f)
            ),
            start = Offset(center.x - radius, center.y - radius),
            end = Offset(center.x + radius, center.y + radius)
        )
        scope.drawPath(path = gearPath, brush = chamferBrush, style = Stroke(width = 2.5f))

        // Recessed inner web ring
        val webRadius = rootRadius * 0.72f
        val recessedBrush = Brush.radialGradient(
            colors = listOf(
                material.shadowColor.copy(alpha = 0.85f),
                material.primaryColor.copy(alpha = 0.95f),
                material.shadowColor.copy(alpha = 0.95f)
            ),
            center = center,
            radius = webRadius
        )
        scope.drawCircle(
            brush = recessedBrush,
            radius = webRadius,
            center = center
        )

        // Spoke Cutouts (Swiss watch style 4 or 6 window cutouts)
        val spokeCount = if (teeth >= 20) 6 else 4
        val spokeCutoutPath = Path()
        val spokeInnerR = webRadius * 0.42f
        val spokeOuterR = webRadius * 0.85f

        for (s in 0 until spokeCount) {
            val startAng = (s.toFloat() / spokeCount) * (2 * PI).toFloat() + 0.12f
            val endAng = startAng + ((2 * PI / spokeCount) * 0.72f).toFloat()

            spokeCutoutPath.moveTo(center.x + spokeInnerR * cos(startAng), center.y + spokeInnerR * sin(startAng))
            spokeCutoutPath.lineTo(center.x + spokeOuterR * cos(startAng), center.y + spokeOuterR * sin(startAng))
            spokeCutoutPath.lineTo(center.x + spokeOuterR * cos(endAng), center.y + spokeOuterR * sin(endAng))
            spokeCutoutPath.lineTo(center.x + spokeInnerR * cos(endAng), center.y + spokeInnerR * sin(endAng))
            spokeCutoutPath.close()
        }
        // Draw deep machine-chassis backing inside spoke windows
        scope.drawPath(spokeCutoutPath, color = Color(0xFF070B14))
        scope.drawPath(spokeCutoutPath, color = material.highlightColor.copy(alpha = 0.35f), style = Stroke(1.5f))

        // Center Brass Hub & Shaft Keyway
        val hubRadius = webRadius * 0.4f
        val hubBrush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFDE68A),
                Color(0xFFD97706),
                Color(0xFF78350F)
            ),
            center = center.copy(x = center.x - hubRadius * 0.2f, y = center.y - hubRadius * 0.2f),
            radius = hubRadius
        )
        scope.drawCircle(brush = hubBrush, radius = hubRadius, center = center)
        scope.drawCircle(color = Color(0xFF451A03), radius = hubRadius, center = center, style = Stroke(1.5f))

        // Central Shaft Pin & Slotted Screw Head
        val pinRadius = hubRadius * 0.55f
        scope.drawCircle(color = Color(0xFF0F172A), radius = pinRadius, center = center)
        // Slotted screw line
        scope.drawLine(
            color = Color(0xFFCBD5E1),
            start = Offset(center.x - pinRadius * 0.7f, center.y),
            end = Offset(center.x + pinRadius * 0.7f, center.y),
            strokeWidth = 2.2f
        )

        // For Compound / Double Gear: draw second elevated concentric ring!
        if (gear.type.isCompound) {
            val secondR = gear.secondRingRadius * (radius / gear.radius)
            val secondBrush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFF8FAFC),
                    Color(0xFF94A3B8),
                    Color(0xFF334155)
                ),
                center = center,
                radius = secondR
            )
            scope.drawCircle(brush = secondBrush, radius = secondR, center = center)
            scope.drawCircle(color = Color(0xFF38BDF8), radius = secondR, center = center, style = Stroke(2f))
        }

        // Special material glow (e.g. Neon Energy)
        if (material.glowColor != null) {
            scope.drawCircle(
                color = material.glowColor.copy(alpha = 0.35f),
                radius = outerRadius * 1.05f,
                center = center,
                style = Stroke(3f)
            )
        }
    }

    private fun drawOverloadSparks(scope: DrawScope, center: Offset, radius: Float) {
        scope.drawCircle(
            color = Color(0x66EF4444),
            radius = radius * 1.25f,
            center = center,
            style = Stroke(4f)
        )
        // Red crackle warning lines
        for (i in 0 until 6) {
            val angle = (i * 60f + 15f) * (PI.toFloat() / 180f)
            val p1 = Offset(center.x + radius * cos(angle), center.y + radius * sin(angle))
            val p2 = Offset(center.x + (radius + 20f) * cos(angle), center.y + (radius + 20f) * sin(angle))
            scope.drawLine(color = Color(0xFFF87171), start = p1, end = p2, strokeWidth = 3f)
        }
    }

    private fun drawTargetEnergyGlow(scope: DrawScope, center: Offset, radius: Float) {
        val glowBrush = Brush.radialGradient(
            colors = listOf(
                Color(0x9910B981),
                Color(0x4410B981),
                Color(0x0010B981)
            ),
            center = center,
            radius = radius * 1.5f
        )
        scope.drawCircle(brush = glowBrush, radius = radius * 1.5f, center = center)
        scope.drawCircle(color = Color(0xFF34D399), radius = radius * 1.15f, center = center, style = Stroke(3f))
    }

    private fun drawKineticEnergyPulse(scope: DrawScope, center: Offset, radius: Float) {
        scope.drawCircle(
            color = Color(0x3300F0FF),
            radius = radius * 1.1f,
            center = center,
            style = Stroke(2f)
        )
    }

    /**
     * Draw luminous electrical conduit between two meshing gears.
     */
    fun drawMeshingEnergyConduit(
        scope: DrawScope,
        posA: Offset,
        posB: Offset,
        radiusA: Float,
        radiusB: Float
    ) {
        val midX = (posA.x * radiusB + posB.x * radiusA) / (radiusA + radiusB)
        val midY = (posA.y * radiusB + posB.y * radiusA) / (radiusA + radiusB)
        val contactPoint = Offset(midX, midY)

        // Pulsing green/cyan energy node at the tooth mesh contact
        val energyBrush = Brush.radialGradient(
            colors = listOf(Color(0xFF34D399), Color(0x8000F0FF), Color(0x0000F0FF)),
            center = contactPoint,
            radius = 24f
        )
        scope.drawCircle(brush = energyBrush, radius = 22f, center = contactPoint)
        scope.drawCircle(color = Color(0xFFFFFFFF), radius = 4f, center = contactPoint)
    }
}
