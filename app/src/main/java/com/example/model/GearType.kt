package com.example.model

enum class GearType(
    val displayName: String,
    val description: String,
    val defaultTeeth: Int,
    val defaultRadius: Float, // Visual & physics radius in board units
    val defaultMaterial: GearMaterial,
    val isCompound: Boolean = false,
    val isMagnetic: Boolean = false,
    val isPlanetary: Boolean = false,
    val isReversal: Boolean = false
) {
    STANDARD_SPUR(
        displayName = "Standard Spur",
        description = "Balanced straight-tooth gear for versatile power transmission.",
        defaultTeeth = 16,
        defaultRadius = 48f,
        defaultMaterial = GearMaterial.STEEL
    ),
    SMALL_PRECISION(
        displayName = "Precision Pinion",
        description = "Small diameter pinion capable of high rotational speeds.",
        defaultTeeth = 10,
        defaultRadius = 30f,
        defaultMaterial = GearMaterial.BRASS
    ),
    LARGE_TORQUE(
        displayName = "Torque Master",
        description = "Massive heavy-duty gear that multiplies rotational torque.",
        defaultTeeth = 24,
        defaultRadius = 72f,
        defaultMaterial = GearMaterial.CAST_IRON
    ),
    DOUBLE_GEAR(
        displayName = "Double Step Gear",
        description = "Two concentric gears on a single shaft bridging Layer 1 and 2.",
        defaultTeeth = 20,
        defaultRadius = 60f,
        defaultMaterial = GearMaterial.STEEL,
        isCompound = true
    ),
    COMPOUND_GEAR(
        displayName = "Compound Axle",
        description = "Multi-tier axle transferring mechanical energy across planes.",
        defaultTeeth = 18,
        defaultRadius = 54f,
        defaultMaterial = GearMaterial.TITANIUM,
        isCompound = true
    ),
    IDLER_GEAR(
        displayName = "Idler Gear",
        description = "Maintains ratio while reversing output rotation direction.",
        defaultTeeth = 14,
        defaultRadius = 42f,
        defaultMaterial = GearMaterial.ALUMINUM
    ),
    HEAVY_STEEL(
        displayName = "Heavy Steel",
        description = "High flywheel inertia; ramps up slowly with massive kinetic energy.",
        defaultTeeth = 22,
        defaultRadius = 66f,
        defaultMaterial = GearMaterial.STEEL
    ),
    LIGHT_ALUMINUM(
        displayName = "Aero Aluminum",
        description = "Extremely agile gear that accelerates to maximum RPM instantaneously.",
        defaultTeeth = 12,
        defaultRadius = 36f,
        defaultMaterial = GearMaterial.ALUMINUM
    ),
    BRASS_GEAR(
        displayName = "Watchmaker Brass",
        description = "Precision cut skeleton wheel inspired by haute horlogerie.",
        defaultTeeth = 16,
        defaultRadius = 48f,
        defaultMaterial = GearMaterial.BRASS
    ),
    COPPER_GEAR(
        displayName = "Thermal Copper",
        description = "High heat dissipation gear preventing machine overheating.",
        defaultTeeth = 14,
        defaultRadius = 42f,
        defaultMaterial = GearMaterial.COPPER
    ),
    CHROME_GEAR(
        displayName = "Chrome Reflector",
        description = "Frictionless mirror alloy minimizing transmission drag.",
        defaultTeeth = 16,
        defaultRadius = 48f,
        defaultMaterial = GearMaterial.CHROME
    ),
    NEON_ENERGY(
        displayName = "Neon Energy Gear",
        description = "Energized electromagnetic gear creating power conduits.",
        defaultTeeth = 14,
        defaultRadius = 42f,
        defaultMaterial = GearMaterial.NEON_ENERGY
    ),
    MAGNETIC_GEAR(
        displayName = "Magnetic Flux Gear",
        description = "Transfers mechanical force across an air gap using magnetic fields.",
        defaultTeeth = 16,
        defaultRadius = 48f,
        defaultMaterial = GearMaterial.NEON_ENERGY,
        isMagnetic = true
    ),
    LOCKED_GEAR(
        displayName = "Mechanical Lock Gear",
        description = "Equipped with a safety pin latch that releases when key rotates.",
        defaultTeeth = 16,
        defaultRadius = 48f,
        defaultMaterial = GearMaterial.STEEL
    ),
    REVERSAL_GEAR(
        displayName = "Direction Reversal",
        description = "Internal bevel coupling that keeps matching rotation direction.",
        defaultTeeth = 16,
        defaultRadius = 48f,
        defaultMaterial = GearMaterial.TITANIUM,
        isReversal = true
    ),
    TIMER_GEAR(
        displayName = "Escapement Timer",
        description = "Regulates mechanical countdown with a ticking pallet fork.",
        defaultTeeth = 18,
        defaultRadius = 54f,
        defaultMaterial = GearMaterial.BRASS
    ),
    MULTI_AXIS_GEAR(
        displayName = "Multi-Axis Bevel",
        description = "Beveled 45-degree teeth transferring rotation into the 3rd dimension.",
        defaultTeeth = 16,
        defaultRadius = 48f,
        defaultMaterial = GearMaterial.TITANIUM
    ),
    PLANETARY_GEAR(
        displayName = "Planetary Epicyclic",
        description = "Sun gear with three orbiting planet gears inside an outer ring.",
        defaultTeeth = 24,
        defaultRadius = 72f,
        defaultMaterial = GearMaterial.GOLD,
        isPlanetary = true
    )
}
