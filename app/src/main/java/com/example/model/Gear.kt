package com.example.model

data class Gear(
    val id: String,
    val type: GearType = GearType.STANDARD_SPUR,
    val material: GearMaterial = type.defaultMaterial,
    val teethCount: Int = type.defaultTeeth,
    val radius: Float = type.defaultRadius,
    val thickness: Float = 14f,
    val x: Float = 0f,
    val y: Float = 0f,
    val layer: Int = 1, // 1 = Foreground, 2 = Middle, 3 = Background
    val isSource: Boolean = false,
    val isTarget: Boolean = false,
    val targetRequiredDirection: Int = 0, // 0 = Any, 1 = CW, -1 = CCW
    val targetRequiredRpm: Float? = null,
    val isFixed: Boolean = false, // Permanent mounting shaft
    val isLocked: Boolean = false, // Locked until unlock condition
    val isUnlocked: Boolean = false,
    // For Double / Compound gears
    val secondRingTeeth: Int = (teethCount / 2).coerceAtLeast(8),
    val secondRingRadius: Float = radius * 0.55f,
    val secondRingLayer: Int = (layer + 1).coerceAtMost(3),
    // Dynamic simulation properties
    val rotationAngle: Float = 0f,
    val currentRpm: Float = 0f,
    val targetRpm: Float = 0f,
    val direction: Int = 0, // 1 = CW, -1 = CCW, 0 = stationary
    val isPowered: Boolean = false,
    val meshedWith: List<String> = emptyList(),
    val isOverloaded: Boolean = false,
    val isSlipping: Boolean = false,
    val isDragging: Boolean = false
) {
    val toothPitch: Float
        get() = (2 * Math.PI.toFloat() * radius) / teethCount

    // Helper to duplicate with position
    fun withPosition(newX: Float, newY: Float, newLayer: Int = layer): Gear {
        return copy(x = newX, y = newY, layer = newLayer)
    }

    // Check if gear is in satisfactory target state
    fun isTargetSatisfied(): Boolean {
        if (!isTarget) return false
        if (!isPowered || currentRpm < 5f) return false
        if (targetRequiredDirection != 0 && direction != targetRequiredDirection) return false
        if (targetRequiredRpm != null && kotlin.math.abs(currentRpm - targetRequiredRpm) > 5f) return false
        return true
    }
}
