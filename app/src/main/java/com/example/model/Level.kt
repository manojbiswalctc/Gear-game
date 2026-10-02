package com.example.model

enum class LevelObjectiveType(
    val title: String,
    val icon: String,
    val description: String
) {
    CONNECT_TARGET(
        title = "Power Transmission",
        icon = "⚡",
        description = "Transmit rotational kinetic power from the Start Motor to the Output Target."
    ),
    TARGET_CLOCKWISE(
        title = "Clockwise Vector",
        icon = "↻",
        description = "Drive the Target Gear in a CLOCKWISE (↻) rotation direction."
    ),
    TARGET_COUNTER_CLOCKWISE(
        title = "Counter-Clockwise Vector",
        icon = "↺",
        description = "Drive the Target Gear in a COUNTER-CLOCKWISE (↺) rotation direction."
    ),
    REACH_EXACT_RPM(
        title = "RPM Precision",
        icon = "🎯",
        description = "Utilize precise gear ratios to hit the exact target output RPM."
    ),
    ALL_GEARS_ROTATING(
        title = "Full Synchronization",
        icon = "⚙",
        description = "Engage every single gear in the network into synchronized rotation."
    ),
    TIME_ATTACK(
        title = "Timed Assembly",
        icon = "⏱",
        description = "Assemble and activate the mechanism before time expires."
    ),
    MULTI_TARGET(
        title = "Multi-Axis Drive",
        icon = "⚡⚡",
        description = "Deliver power simultaneously to all required target shafts."
    ),
    MINIMALIST(
        title = "Master Efficiency",
        icon = "🔩",
        description = "Solve the mechanical pathway using the minimum required gears."
    )
}

data class PegSlot(
    val id: String,
    val x: Float,
    val y: Float,
    val layer: Int = 1,
    val allowedMaterials: List<GearMaterial>? = null
)

data class HintInfo(
    val level1Text: String, // Which gear to pick
    val level2TargetSlot: PegSlot? = null, // Highlighted position
    val level3DirectionText: String = "", // Direction arrow
    val level4SolutionSlots: List<Pair<String, Pair<Float, Float>>> = emptyList() // Full solution coordinates
)

data class Level(
    val id: Int,
    val worldId: Int,
    val name: String,
    val description: String,
    val objectiveType: LevelObjectiveType,
    val targetRpm: Float? = null,
    val timeLimitSeconds: Int? = null,
    val minGearsFor3Stars: Int = 3,
    val fixedGears: List<Gear>, // Source motor, targets, pre-installed gears
    val inventoryGears: List<Gear>, // Gears player can drag onto board
    val pegSlots: List<PegSlot> = emptyList(), // Mounting holes/pegs
    val hints: HintInfo
)

data class World(
    val id: Int,
    val name: String,
    val subtitle: String,
    val levelRange: IntRange,
    val primaryColorHex: Long,
    val backgroundThemeName: String,
    val ambientDescription: String
)
