package com.example.ui.renderer

import androidx.compose.ui.geometry.Offset
import kotlin.math.cos
import kotlin.math.sin

data class Camera3DState(
    val pitchDeg: Float = 42f, // Tilt from top: 20 deg to 70 deg
    val yawDeg: Float = 0f,    // Orbit around Z axis: -60 deg to 60 deg
    val panX: Float = 0f,
    val panY: Float = 0f,
    val zoom: Float = 1.0f
) {
    /**
     * Projects a 3D board point (x, y, layer) to 2D screen coordinates with perspective tilt.
     */
    fun project(
        bx: Float,
        by: Float,
        layer: Int, // 1 = Foreground, 2 = Middle, 3 = Background
        viewportCenter: Offset
    ): Offset {
        val radPitch = Math.toRadians(pitchDeg.toDouble()).toFloat()
        val radYaw = Math.toRadians(yawDeg.toDouble()).toFloat()

        // Layer depth offset along the vertical Z axis
        val layerZ = (2 - layer) * 28f // Layer 1 is higher (+28), Layer 3 is lower (-28)

        // Rotate by yaw (around Z)
        val cosY = cos(radYaw)
        val sinY = sin(radYaw)
        val rotX = bx * cosY - by * sinY
        val rotY = bx * sinY + by * cosY

        // Tilt by pitch (foreshortening Y by cos(pitch) and projecting Z by sin(pitch))
        val cosP = cos(radPitch)
        val sinP = sin(radPitch)
        val projY = rotY * cosP - layerZ * sinP

        // Apply camera pan and zoom
        val screenX = viewportCenter.x + (rotX + panX) * zoom
        val screenY = viewportCenter.y + (projY + panY) * zoom

        return Offset(screenX, screenY)
    }

    /**
     * Compute scale factor and perspective oval ratio for drawing 3D gear thickness.
     */
    val foreshorteningY: Float
        get() {
            val radPitch = Math.toRadians(pitchDeg.toDouble()).toFloat()
            return cos(radPitch).coerceIn(0.5f, 1.0f)
        }

    fun reset(): Camera3DState = Camera3DState()
}
