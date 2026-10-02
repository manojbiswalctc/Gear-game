package com.example.model

object LevelRepository {

    val worlds: List<World> = listOf(
        World(
            id = 1,
            name = "Mechanical Workshop",
            subtitle = "Foundations of Kinetic Force",
            levelRange = 1..50,
            primaryColorHex = 0xFF0284C7,
            backgroundThemeName = "Workshop",
            ambientDescription = "An industrial lathe bench with steel gears and mounting chucks."
        ),
        World(
            id = 2,
            name = "Vintage Clock Factory",
            subtitle = "Haute Horlogerie & Escapements",
            levelRange = 51..100,
            primaryColorHex = 0xFFD97706,
            backgroundThemeName = "Clockwork",
            ambientDescription = "Intricate Swiss brass gear trains and oscillating balance wheels."
        ),
        World(
            id = 3,
            name = "Automobile Engine Lab",
            subtitle = "Camshafts, Torque & High RPM",
            levelRange = 101..150,
            primaryColorHex = 0xFFEA580C,
            backgroundThemeName = "Engine",
            ambientDescription = "Dual overhead camshafts, timing chains, and high-torque compound axles."
        ),
        World(
            id = 4,
            name = "Industrial Factory",
            subtitle = "Heavy Conveyors & Planetary Drives",
            levelRange = 151..200,
            primaryColorHex = 0xFF475569,
            backgroundThemeName = "Factory",
            ambientDescription = "Cast iron gearboxes driving heavy assembly line rollers."
        ),
        World(
            id = 5,
            name = "Steam Machine & Foundry",
            subtitle = "Hydraulic Pressure & Thermal Dissipation",
            levelRange = 201..250,
            primaryColorHex = 0xFFDC2626,
            backgroundThemeName = "Foundry",
            ambientDescription = "Pressurized steam cylinders and copper heat-dissipation wheels."
        ),
        World(
            id = 6,
            name = "Futuristic Robotics Lab",
            subtitle = "Servo Kinematics & Multi-Axis Joints",
            levelRange = 251..300,
            primaryColorHex = 0xFF10B981,
            backgroundThemeName = "Robotics",
            ambientDescription = "Precision titanium gears powering 6-axis articulated robot arms."
        ),
        World(
            id = 7,
            name = "Cybernetic Factory",
            subtitle = "Photonic Gears & Magnetic Couplings",
            levelRange = 301..350,
            primaryColorHex = 0xFF8B5CF6,
            backgroundThemeName = "Cybernetic",
            ambientDescription = "Air-gap magnetic flux linkages and pulsing optical power meshes."
        ),
        World(
            id = 8,
            name = "Space Station Machinery",
            subtitle = "Zero-G Low Inertia Transmission",
            levelRange = 351..400,
            primaryColorHex = 0xFF06B6D4,
            backgroundThemeName = "Station",
            ambientDescription = "Cryogenic vacuum bearings and lightweight carbon fiber satellite gimbals."
        ),
        World(
            id = 9,
            name = "Underwater Mechanical Facility",
            subtitle = "Hydrostatic Seals & Submersible Turbines",
            levelRange = 401..450,
            primaryColorHex = 0xFF0284C7,
            backgroundThemeName = "Abyss",
            ambientDescription = "Deep-sea pressure hulls and corrosive-resistant bronze planetary gearboxes."
        ),
        World(
            id = 10,
            name = "AI Quantum Machine",
            subtitle = "5D Dimensional Core Transmissions",
            levelRange = 451..500,
            primaryColorHex = 0xFFEC4899,
            backgroundThemeName = "Quantum",
            ambientDescription = "Self-assembling hyper-dimensional gear matrices operating in quantum coherence."
        )
    )

    fun getWorldForLevel(levelId: Int): World {
        return worlds.firstOrNull { levelId in it.levelRange } ?: worlds[0]
    }

    fun getLevel(levelId: Int): Level {
        val clampedId = levelId.coerceIn(1, 500)
        val world = getWorldForLevel(clampedId)

        // Handcrafted custom tutorials and landmark levels
        return when (clampedId) {
            1 -> Level(
                id = 1,
                worldId = 1,
                name = "First Ignition",
                description = "Learn basic gear placement: drag the Standard Spur gear to bridge the Start Motor and Target Gear.",
                objectiveType = LevelObjectiveType.CONNECT_TARGET,
                minGearsFor3Stars = 1,
                fixedGears = listOf(
                    Gear(
                        id = "source_1",
                        type = GearType.STANDARD_SPUR,
                        material = GearMaterial.STEEL,
                        teethCount = 16,
                        radius = 48f,
                        x = -96f,
                        y = 0f,
                        layer = 1,
                        isSource = true,
                        isFixed = true,
                        direction = 1,
                        currentRpm = 60f,
                        isPowered = true
                    ),
                    Gear(
                        id = "target_1",
                        type = GearType.STANDARD_SPUR,
                        material = GearMaterial.GOLD,
                        teethCount = 16,
                        radius = 48f,
                        x = 96f,
                        y = 0f,
                        layer = 1,
                        isTarget = true,
                        isFixed = true,
                        targetRequiredDirection = 1 // Source is CW(1), intermediate CCW(-1), target CW(1)
                    )
                ),
                inventoryGears = listOf(
                    Gear(
                        id = "inv_1_1",
                        type = GearType.STANDARD_SPUR,
                        material = GearMaterial.BRASS,
                        teethCount = 16,
                        radius = 48f,
                        x = 0f,
                        y = 0f,
                        layer = 1
                    )
                ),
                pegSlots = listOf(
                    PegSlot(id = "slot_1", x = 0f, y = 0f, layer = 1)
                ),
                hints = HintInfo(
                    level1Text = "Drag the Brass Spur Gear from the dock.",
                    level2TargetSlot = PegSlot("slot_1", 0f, 0f, 1),
                    level3DirectionText = "Intermediate gear will rotate Counter-Clockwise (↺) to drive target Clockwise (↻).",
                    level4SolutionSlots = listOf("inv_1_1" to (0f to 0f))
                )
            )

            2 -> Level(
                id = 2,
                worldId = 1,
                name = "Kinetic Direction",
                description = "Clockwise vs Counter-Clockwise: deliver COUNTER-CLOCKWISE power to the output shaft.",
                objectiveType = LevelObjectiveType.TARGET_COUNTER_CLOCKWISE,
                minGearsFor3Stars = 2,
                fixedGears = listOf(
                    Gear(
                        id = "source_2",
                        type = GearType.STANDARD_SPUR,
                        material = GearMaterial.STEEL,
                        teethCount = 16,
                        radius = 48f,
                        x = -135f,
                        y = -40f,
                        layer = 1,
                        isSource = true,
                        isFixed = true,
                        direction = 1,
                        currentRpm = 60f,
                        isPowered = true
                    ),
                    Gear(
                        id = "target_2",
                        type = GearType.STANDARD_SPUR,
                        material = GearMaterial.GOLD,
                        teethCount = 16,
                        radius = 48f,
                        x = 135f,
                        y = 40f,
                        layer = 1,
                        isTarget = true,
                        isFixed = true,
                        targetRequiredDirection = -1 // Requires CCW!
                    )
                ),
                inventoryGears = listOf(
                    Gear(
                        id = "inv_2_1",
                        type = GearType.STANDARD_SPUR,
                        material = GearMaterial.STEEL,
                        teethCount = 16,
                        radius = 48f
                    ),
                    Gear(
                        id = "inv_2_2",
                        type = GearType.STANDARD_SPUR,
                        material = GearMaterial.BRASS,
                        teethCount = 16,
                        radius = 48f
                    )
                ),
                pegSlots = listOf(
                    PegSlot(id = "slot_2_1", x = -45f, y = 0f, layer = 1),
                    PegSlot(id = "slot_2_2", x = 45f, y = 0f, layer = 1)
                ),
                hints = HintInfo(
                    level1Text = "Place two gears in series to invert direction twice.",
                    level2TargetSlot = PegSlot("slot_2_1", -45f, 0f, 1),
                    level3DirectionText = "Source (CW) -> Gear 1 (CCW) -> Gear 2 (CW) -> Target (CCW).",
                    level4SolutionSlots = listOf("inv_2_1" to (-45f to 0f), "inv_2_2" to (45f to 0f))
                )
            )

            3 -> Level(
                id = 3,
                worldId = 1,
                name = "Velocity Multiplier",
                description = "Gear ratios: connect a large gear to a small precision pinion to multiply output RPM to 120 RPM.",
                objectiveType = LevelObjectiveType.REACH_EXACT_RPM,
                targetRpm = 120f,
                minGearsFor3Stars = 1,
                fixedGears = listOf(
                    Gear(
                        id = "source_3",
                        type = GearType.LARGE_TORQUE,
                        material = GearMaterial.CAST_IRON,
                        teethCount = 24,
                        radius = 64f,
                        x = -80f,
                        y = 0f,
                        layer = 1,
                        isSource = true,
                        isFixed = true,
                        direction = 1,
                        currentRpm = 60f,
                        isPowered = true
                    ),
                    Gear(
                        id = "target_3",
                        type = GearType.SMALL_PRECISION,
                        material = GearMaterial.GOLD,
                        teethCount = 12,
                        radius = 32f,
                        x = 80f,
                        y = 0f,
                        layer = 1,
                        isTarget = true,
                        isFixed = true,
                        targetRequiredRpm = 120f
                    )
                ),
                inventoryGears = listOf(
                    Gear(
                        id = "inv_3_1",
                        type = GearType.STANDARD_SPUR,
                        material = GearMaterial.CHROME,
                        teethCount = 16,
                        radius = 48f
                    ),
                    Gear(
                        id = "inv_3_2",
                        type = GearType.LARGE_TORQUE,
                        material = GearMaterial.STEEL,
                        teethCount = 24,
                        radius = 64f
                    )
                ),
                pegSlots = listOf(
                    PegSlot(id = "slot_3_1", x = 16f, y = 0f, layer = 1)
                ),
                hints = HintInfo(
                    level1Text = "A smaller output gear rotates faster: 60 RPM * (24 / 12) = 120 RPM.",
                    level2TargetSlot = PegSlot("slot_3_1", 16f, 0f, 1),
                    level3DirectionText = "Connect the drive gear to mesh with both source and target.",
                    level4SolutionSlots = listOf("inv_3_1" to (16f to 0f))
                )
            )

            4 -> Level(
                id = 4,
                worldId = 1,
                name = "Multi-Layer Shaft",
                description = "Layer 1 to Layer 2 transmission: use the Double Step Gear to bridge foreground and middle mechanical depth.",
                objectiveType = LevelObjectiveType.CONNECT_TARGET,
                minGearsFor3Stars = 2,
                fixedGears = listOf(
                    Gear(
                        id = "source_4",
                        type = GearType.STANDARD_SPUR,
                        material = GearMaterial.STEEL,
                        teethCount = 16,
                        radius = 48f,
                        x = -110f,
                        y = 0f,
                        layer = 1,
                        isSource = true,
                        isFixed = true,
                        direction = 1,
                        currentRpm = 60f,
                        isPowered = true
                    ),
                    Gear(
                        id = "target_4",
                        type = GearType.STANDARD_SPUR,
                        material = GearMaterial.GOLD,
                        teethCount = 16,
                        radius = 48f,
                        x = 110f,
                        y = 0f,
                        layer = 2, // Background layer!
                        isTarget = true,
                        isFixed = true
                    )
                ),
                inventoryGears = listOf(
                    Gear(
                        id = "inv_4_double",
                        type = GearType.DOUBLE_GEAR,
                        material = GearMaterial.TITANIUM,
                        teethCount = 20,
                        radius = 54f,
                        layer = 1,
                        secondRingTeeth = 12,
                        secondRingRadius = 34f,
                        secondRingLayer = 2
                    ),
                    Gear(
                        id = "inv_4_spur",
                        type = GearType.STANDARD_SPUR,
                        material = GearMaterial.BRASS,
                        teethCount = 16,
                        radius = 48f,
                        layer = 2
                    )
                ),
                pegSlots = listOf(
                    PegSlot(id = "slot_4_1", x = -10f, y = 0f, layer = 1),
                    PegSlot(id = "slot_4_2", x = 60f, y = 0f, layer = 2)
                ),
                hints = HintInfo(
                    level1Text = "Place the Double Step Gear at the central shaft to bridge Layer 1 and Layer 2.",
                    level2TargetSlot = PegSlot("slot_4_1", -10f, 0f, 1),
                    level3DirectionText = "The secondary ring on Layer 2 drives the background gear train.",
                    level4SolutionSlots = listOf("inv_4_double" to (-10f to 0f), "inv_4_spur" to (60f to 0f))
                )
            )

            5 -> Level(
                id = 5,
                worldId = 1,
                name = "Triple Output Junction",
                description = "Synchronized power: distribute rotational force from 1 motor to 2 target outputs simultaneously.",
                objectiveType = LevelObjectiveType.MULTI_TARGET,
                minGearsFor3Stars = 2,
                fixedGears = listOf(
                    Gear(
                        id = "source_5",
                        type = GearType.LARGE_TORQUE,
                        material = GearMaterial.STEEL,
                        teethCount = 24,
                        radius = 64f,
                        x = -110f,
                        y = 0f,
                        layer = 1,
                        isSource = true,
                        isFixed = true,
                        direction = 1,
                        currentRpm = 60f,
                        isPowered = true
                    ),
                    Gear(
                        id = "target_5_top",
                        type = GearType.STANDARD_SPUR,
                        material = GearMaterial.GOLD,
                        teethCount = 16,
                        radius = 44f,
                        x = 90f,
                        y = -70f,
                        layer = 1,
                        isTarget = true,
                        isFixed = true
                    ),
                    Gear(
                        id = "target_5_bottom",
                        type = GearType.STANDARD_SPUR,
                        material = GearMaterial.GOLD,
                        teethCount = 16,
                        radius = 44f,
                        x = 90f,
                        y = 70f,
                        layer = 1,
                        isTarget = true,
                        isFixed = true
                    )
                ),
                inventoryGears = listOf(
                    Gear(
                        id = "inv_5_1",
                        type = GearType.STANDARD_SPUR,
                        material = GearMaterial.CHROME,
                        teethCount = 16,
                        radius = 48f
                    ),
                    Gear(
                        id = "inv_5_2",
                        type = GearType.STANDARD_SPUR,
                        material = GearMaterial.BRASS,
                        teethCount = 16,
                        radius = 48f
                    )
                ),
                pegSlots = listOf(
                    PegSlot(id = "slot_5_center", x = 0f, y = 0f, layer = 1),
                    PegSlot(id = "slot_5_hub", x = 50f, y = 0f, layer = 1)
                ),
                hints = HintInfo(
                    level1Text = "Place the Chrome gear at the center to branch torque to both targets.",
                    level2TargetSlot = PegSlot("slot_5_center", 0f, 0f, 1),
                    level3DirectionText = "Center hub splits power up and down.",
                    level4SolutionSlots = listOf("inv_5_1" to (0f to 0f), "inv_5_2" to (50f to 0f))
                )
            )

            // Procedurally generated high-craft levels for all 500 levels
            else -> generateProceduralLevel(clampedId, world)
        }
    }

    private fun generateProceduralLevel(levelId: Int, world: World): Level {
        val tier = when (levelId) {
            in 1..20 -> 1 // Tutorial
            in 21..50 -> 2 // Basic
            in 51..100 -> 3 // Intermediate
            in 101..200 -> 4 // Advanced
            in 201..350 -> 5 // Expert
            else -> 6 // Master Engineer
        }

        val objectiveType = when ((levelId + world.id) % 7) {
            0 -> LevelObjectiveType.CONNECT_TARGET
            1 -> LevelObjectiveType.TARGET_CLOCKWISE
            2 -> LevelObjectiveType.TARGET_COUNTER_CLOCKWISE
            3 -> LevelObjectiveType.REACH_EXACT_RPM
            4 -> LevelObjectiveType.ALL_GEARS_ROTATING
            5 -> LevelObjectiveType.MULTI_TARGET
            else -> LevelObjectiveType.MINIMALIST
        }

        val targetRequiredDir = when (objectiveType) {
            LevelObjectiveType.TARGET_CLOCKWISE -> 1
            LevelObjectiveType.TARGET_COUNTER_CLOCKWISE -> -1
            else -> 0
        }

        // Layout geometry based on level ID
        val gearCount = (3 + (levelId % 8)).coerceAtMost(10)
        val sourceX = -130f
        val sourceY = if (levelId % 2 == 0) -40f else 30f
        val targetX = 130f
        val targetY = if (levelId % 3 == 0) 50f else -30f

        val sourceRadius = if (tier >= 3 && levelId % 4 == 0) 64f else 48f
        val sourceTeeth = if (sourceRadius > 50f) 24 else 16

        val targetRadius = if (tier >= 4 && levelId % 3 == 0) 32f else 48f
        val targetTeeth = if (targetRadius < 40f) 12 else 16

        val fixedGears = mutableListOf<Gear>()
        fixedGears.add(
            Gear(
                id = "src_$levelId",
                type = if (sourceRadius > 50f) GearType.LARGE_TORQUE else GearType.STANDARD_SPUR,
                material = when (world.id) {
                    2 -> GearMaterial.BRASS
                    3 -> GearMaterial.TITANIUM
                    5 -> GearMaterial.COPPER
                    7, 10 -> GearMaterial.NEON_ENERGY
                    else -> GearMaterial.STEEL
                },
                teethCount = sourceTeeth,
                radius = sourceRadius,
                x = sourceX,
                y = sourceY,
                layer = 1,
                isSource = true,
                isFixed = true,
                direction = 1,
                currentRpm = 60f,
                isPowered = true
            )
        )

        fixedGears.add(
            Gear(
                id = "tgt_$levelId",
                type = if (targetRadius < 40f) GearType.SMALL_PRECISION else GearType.STANDARD_SPUR,
                material = GearMaterial.GOLD,
                teethCount = targetTeeth,
                radius = targetRadius,
                x = targetX,
                y = targetY,
                layer = if (tier >= 4 && levelId % 2 == 1) 2 else 1,
                isTarget = true,
                isFixed = true,
                targetRequiredDirection = targetRequiredDir,
                targetRequiredRpm = if (objectiveType == LevelObjectiveType.REACH_EXACT_RPM) 90f else null
            )
        )

        // Peg slots connecting source to target
        val steps = (gearCount - 2).coerceIn(2, 5)
        val pegSlots = mutableListOf<PegSlot>()
        val inventoryGears = mutableListOf<Gear>()
        val solutionSlots = mutableListOf<Pair<String, Pair<Float, Float>>>()

        for (i in 1..steps) {
            val frac = i.toFloat() / (steps + 1)
            val px = sourceX + (targetX - sourceX) * frac
            val py = sourceY + (targetY - sourceY) * frac + (if (i % 2 == 1) 35f else -35f)
            val slotId = "slot_${levelId}_$i"
            val slotLayer = if (tier >= 4 && i % 3 == 0) 2 else 1
            pegSlots.add(PegSlot(id = slotId, x = px, y = py, layer = slotLayer))

            val gearType = when {
                tier >= 4 && i == 2 && slotLayer == 2 -> GearType.DOUBLE_GEAR
                tier >= 3 && i % 3 == 0 -> GearType.LARGE_TORQUE
                tier >= 2 && i % 2 == 1 -> GearType.SMALL_PRECISION
                else -> GearType.STANDARD_SPUR
            }

            val mat = when ((i + world.id) % 6) {
                0 -> GearMaterial.BRASS
                1 -> GearMaterial.CHROME
                2 -> GearMaterial.TITANIUM
                3 -> GearMaterial.COPPER
                4 -> GearMaterial.NEON_ENERGY
                else -> GearMaterial.STEEL
            }

            val invGear = Gear(
                id = "inv_${levelId}_$i",
                type = gearType,
                material = mat,
                teethCount = gearType.defaultTeeth,
                radius = gearType.defaultRadius,
                layer = slotLayer
            )
            inventoryGears.add(invGear)
            solutionSlots.add(invGear.id to (px to py))
        }

        // Add 1 or 2 decoy/alternative gears for higher tiers to provide realistic engineering choice
        if (tier >= 2) {
            inventoryGears.add(
                Gear(
                    id = "inv_${levelId}_decoy1",
                    type = GearType.IDLER_GEAR,
                    material = GearMaterial.ALUMINUM,
                    teethCount = 14,
                    radius = 42f
                )
            )
        }
        if (tier >= 4) {
            inventoryGears.add(
                Gear(
                    id = "inv_${levelId}_decoy2",
                    type = GearType.HEAVY_STEEL,
                    material = GearMaterial.CAST_IRON,
                    teethCount = 22,
                    radius = 66f
                )
            )
        }

        return Level(
            id = levelId,
            worldId = world.id,
            name = "Assembly Stage $levelId",
            description = "${world.name}: Align gears to satisfy ${objectiveType.title}.",
            objectiveType = objectiveType,
            targetRpm = if (objectiveType == LevelObjectiveType.REACH_EXACT_RPM) 90f else null,
            timeLimitSeconds = if (objectiveType == LevelObjectiveType.TIME_ATTACK) 90 else null,
            minGearsFor3Stars = steps,
            fixedGears = fixedGears,
            inventoryGears = inventoryGears,
            pegSlots = pegSlots,
            hints = HintInfo(
                level1Text = "Select the ${inventoryGears.firstOrNull()?.type?.displayName ?: "gear"} from inventory.",
                level2TargetSlot = pegSlots.firstOrNull(),
                level3DirectionText = if (targetRequiredDir == 1) "Target needs Clockwise (↻) drive." else "Balance gear counts for proper direction.",
                level4SolutionSlots = solutionSlots
            )
        )
    }
}
