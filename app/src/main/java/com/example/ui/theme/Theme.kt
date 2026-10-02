package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GearForgeColorScheme = darkColorScheme(
    primary = EnergyCyan,
    onPrimary = DarkBackground,
    primaryContainer = EnergyCyanDark,
    onPrimaryContainer = MachineTextPrimary,
    secondary = MetalGold,
    onSecondary = DarkBackground,
    secondaryContainer = MetalBrass,
    onSecondaryContainer = MachineTextPrimary,
    tertiary = EnergyGreen,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = MachineTextPrimary,
    surface = DarkSurface,
    onSurface = MachineTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = MachineTextSecondary,
    outline = DarkSurfaceBorder,
    error = MachineJam,
    onError = MachineTextPrimary
)

@Composable
fun GearForgeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GearForgeColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    GearForgeTheme(content = content)
}
