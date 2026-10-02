package com.example.model

import androidx.compose.ui.graphics.Color

enum class GearMaterial(
    val displayName: String,
    val description: String,
    val primaryColor: Color,
    val highlightColor: Color,
    val shadowColor: Color,
    val specularGloss: Float,
    val massMultiplier: Float, // Higher = slower acceleration, heavy mechanical feel
    val frictionMultiplier: Float,
    val glowColor: Color? = null,
    val soundProfile: SoundProfile = SoundProfile.STANDARD
) {
    STEEL(
        displayName = "Brushed Steel",
        description = "Industrial cold-rolled alloy with balanced inertia and durability.",
        primaryColor = Color(0xFF8C9BAE),
        highlightColor = Color(0xFFD6E0EC),
        shadowColor = Color(0xFF475569),
        specularGloss = 0.8f,
        massMultiplier = 1.0f,
        frictionMultiplier = 1.0f
    ),
    CHROME(
        displayName = "Chrome Mirror",
        description = "Ultra-polished mirror finish with intense specular reflections.",
        primaryColor = Color(0xFFCBD5E1),
        highlightColor = Color(0xFFFFFFFF),
        shadowColor = Color(0xFF64748B),
        specularGloss = 1.0f,
        massMultiplier = 0.9f,
        frictionMultiplier = 0.6f
    ),
    COPPER(
        displayName = "Pure Copper",
        description = "Warm reddish metallic alloy with excellent thermal conductivity.",
        primaryColor = Color(0xFFEA580C),
        highlightColor = Color(0xFFFDBA74),
        shadowColor = Color(0xFF9A3412),
        specularGloss = 0.75f,
        massMultiplier = 1.15f,
        frictionMultiplier = 0.9f
    ),
    BRASS(
        displayName = "Horology Brass",
        description = "Classic watchmaking brass alloy with high acoustic resonance.",
        primaryColor = Color(0xFFD97706),
        highlightColor = Color(0xFFFDE68A),
        shadowColor = Color(0xFF78350F),
        specularGloss = 0.85f,
        massMultiplier = 1.05f,
        frictionMultiplier = 0.75f,
        soundProfile = SoundProfile.BRASS_RESONANT
    ),
    TITANIUM(
        displayName = "Titanium Carbon",
        description = "Aerospace grade material, featherlight yet exceptionally rigid.",
        primaryColor = Color(0xFF475569),
        highlightColor = Color(0xFF94A3B8),
        shadowColor = Color(0xFF1E293B),
        specularGloss = 0.9f,
        massMultiplier = 0.65f,
        frictionMultiplier = 0.5f
    ),
    GOLD(
        displayName = "Gilded Gold",
        description = "Luxurious royal timepiece finish with soft warm reflections.",
        primaryColor = Color(0xFFEAB308),
        highlightColor = Color(0xFFFEF08A),
        shadowColor = Color(0xFF854D0E),
        specularGloss = 0.95f,
        massMultiplier = 1.3f,
        frictionMultiplier = 0.7f
    ),
    CARBON_FIBER(
        displayName = "Carbon Weave",
        description = "Ultralight composite with high rotational acceleration.",
        primaryColor = Color(0xFF1E293B),
        highlightColor = Color(0xFF334155),
        shadowColor = Color(0xFF0F172A),
        specularGloss = 0.6f,
        massMultiplier = 0.45f,
        frictionMultiplier = 0.4f
    ),
    NEON_ENERGY(
        displayName = "Quantum Flux",
        description = "Translucent energized core pulsing with electromagnetic force.",
        primaryColor = Color(0xFF0284C7),
        highlightColor = Color(0xFF38BDF8),
        shadowColor = Color(0xFF0369A1),
        specularGloss = 1.0f,
        massMultiplier = 0.3f,
        frictionMultiplier = 0.2f,
        glowColor = Color(0xFF00F0FF),
        soundProfile = SoundProfile.ENERGY_PULSE
    ),
    CAST_IRON(
        displayName = "Heavy Cast Iron",
        description = "Massive structural gear with immense rotational inertia.",
        primaryColor = Color(0xFF334155),
        highlightColor = Color(0xFF64748B),
        shadowColor = Color(0xFF1E293B),
        specularGloss = 0.4f,
        massMultiplier = 1.8f,
        frictionMultiplier = 1.4f,
        soundProfile = SoundProfile.HEAVY_LOW
    ),
    ALUMINUM(
        displayName = "Aero Aluminum",
        description = "High-precision lightweight alloy for rapid speed changes.",
        primaryColor = Color(0xFFA1A1AA),
        highlightColor = Color(0xFFE4E4E7),
        shadowColor = Color(0xFF52525B),
        specularGloss = 0.8f,
        massMultiplier = 0.5f,
        frictionMultiplier = 0.8f
    );

    enum class SoundProfile {
        STANDARD,
        BRASS_RESONANT,
        HEAVY_LOW,
        ENERGY_PULSE
    }
}
