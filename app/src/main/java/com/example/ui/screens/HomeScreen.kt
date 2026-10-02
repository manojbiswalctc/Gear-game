package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Gear
import com.example.model.GearMaterial
import com.example.model.GearType
import com.example.ui.components.MetallicButton
import com.example.ui.renderer.Camera3DState
import com.example.ui.renderer.Gear3DRenderer
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.EnergyCyan
import com.example.ui.theme.MetalGold
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.Screen

@Composable
fun HomeScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile by viewModel.playerProfile.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Rotating background machinery animation
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.38f)
            val cam = Camera3DState(pitchDeg = 35f, yawDeg = 15f, zoom = 1.0f)

            // Large central steel gear
            val mainGear = Gear(
                id = "bg_1",
                type = GearType.LARGE_TORQUE,
                material = GearMaterial.STEEL,
                teethCount = 28,
                radius = 110f,
                rotationAngle = (System.currentTimeMillis() / 45f) % 360f,
                currentRpm = 20f,
                isPowered = true
            )
            Gear3DRenderer.drawGear(this, mainGear, center, cam)

            // Interlocking brass gear
            val brassGear = Gear(
                id = "bg_2",
                type = GearType.STANDARD_SPUR,
                material = GearMaterial.BRASS,
                teethCount = 18,
                radius = 70f,
                rotationAngle = -(System.currentTimeMillis() / 29f) % 360f,
                currentRpm = 31f,
                isPowered = true
            )
            Gear3DRenderer.drawGear(
                this,
                brassGear,
                Offset(center.x + 140f, center.y - 70f),
                cam
            )

            // Interlocking small copper pinion
            val copperPinion = Gear(
                id = "bg_3",
                type = GearType.SMALL_PRECISION,
                material = GearMaterial.COPPER,
                teethCount = 12,
                radius = 48f,
                rotationAngle = (System.currentTimeMillis() / 19f) % 360f,
                currentRpm = 46f,
                isPowered = true
            )
            Gear3DRenderer.drawGear(
                this,
                copperPinion,
                Offset(center.x - 120f, center.y + 60f),
                cam
            )
        }

        // Vignette gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DarkBackground.copy(alpha = 0.85f),
                            Color.Transparent,
                            DarkBackground.copy(alpha = 0.95f),
                            DarkBackground
                        )
                    )
                )
        )

        // Main Menu Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Currency and Status Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand tag
                Text(
                    text = "5D KINEMATICS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EnergyCyan,
                    letterSpacing = 2.sp
                )

                // Coin & Part Counter
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "⚙ ${profile?.gearCoins ?: 150}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MetalGold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "🔩 ${profile?.partsCount ?: 25}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFCBD5E1),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // TITLE AND BRANDING
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "GEARFORGE",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "MECHANICAL PUZZLE",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EnergyCyan,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "“MASTER THE MACHINE • EVERY TOOTH MATTERS”",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // MENU BUTTONS
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetallicButton(
                    text = "PLAY CAMPAIGN",
                    onClick = { viewModel.navigateTo(Screen.GAME) },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.PlayArrow,
                    isPrimary = true,
                    testTag = "play_campaign_button"
                )

                MetallicButton(
                    text = "LEVELS (500+)",
                    onClick = { viewModel.navigateTo(Screen.LEVEL_SELECT) },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.GridView,
                    testTag = "levels_button"
                )

                MetallicButton(
                    text = "GEAR LAB 3D",
                    onClick = { viewModel.navigateTo(Screen.GEAR_LAB) },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Build,
                    testTag = "gear_lab_button"
                )

                MetallicButton(
                    text = "SPECIAL CHALLENGES",
                    onClick = { viewModel.navigateTo(Screen.CHALLENGES) },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.EmojiEvents,
                    testTag = "challenges_button"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetallicButton(
                        text = "PROFILE",
                        onClick = { viewModel.navigateTo(Screen.PROFILE) },
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Person,
                        testTag = "profile_button"
                    )
                    MetallicButton(
                        text = "SETTINGS",
                        onClick = { viewModel.navigateTo(Screen.SETTINGS) },
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Settings,
                        testTag = "settings_button"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Footer
            Text(
                text = "REALISTIC GEAR PHYSICS • 60 FPS ENGINE",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF475569),
                letterSpacing = 1.2.sp
            )
        }
    }
}
