package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MetallicButton
import com.example.ui.components.MetallicCard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EnergyCyan
import com.example.ui.theme.EnergyGreen
import com.example.ui.theme.MetalGold
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.Screen

@Composable
fun ProfileScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.playerProfile.collectAsState()
    val progressList by viewModel.levelProgressList.collectAsState()

    val totalStars = progressList.sumOf { it.stars }
    val completedLevels = progressList.count { it.isCompleted }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(Screen.HOME) },
                modifier = Modifier
                    .testTag("profile_back_button")
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceElevated)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "ENGINEER DOSSIER",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Chief Kinematics Architect",
                    fontSize = 11.sp,
                    color = EnergyCyan
                )
            }
        }

        // Rank Badge Card
        MetallicCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F2537))
                        .border(1.5.dp, EnergyCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⚙", fontSize = 30.sp)
                }

                Column {
                    Text(
                        text = "RANK: MASTER ENGINEER",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Industrial Machine Specialist",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Highest Sector: 0${(profile?.highestLevelUnlocked ?: 1) / 50 + 1}",
                        fontSize = 11.sp,
                        color = EnergyCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Stat Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "STARS", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    Text(text = "$totalStars", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MetalGold, fontFamily = FontFamily.Monospace)
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "COMPLETED", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    Text(text = "$completedLevels", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EnergyGreen, fontFamily = FontFamily.Monospace)
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "GEAR COINS", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    Text(text = "${profile?.gearCoins ?: 150}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EnergyCyan, fontFamily = FontFamily.Monospace)
                }
            }
        }

        // Achievements List
        Text(
            text = "CAREER ACHIEVEMENTS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            letterSpacing = 1.sp
        )

        val achievements = listOf(
            Triple("FIRST IGNITION", "Complete Tutorial Level 1.", completedLevels >= 1),
            Triple("KINETIC MASTER", "Synchronize 10 consecutive machine networks.", completedLevels >= 10),
            Triple("HOROLOGY WIZARD", "Unlock Sector 02 Swiss Clock Factory.", completedLevels >= 50),
            Triple("PERFECT MESH", "Earn 3 stars on any puzzle.", totalStars >= 3),
            Triple("5D ARCHITECT", "Bridge mechanical force across 3 depth layers.", completedLevels >= 4)
        )

        for (ach in achievements) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, if (ach.third) EnergyGreen.copy(0.5f) else DarkSurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = if (ach.third) "🏆" else "🔒", fontSize = 20.sp)
                        Column {
                            Text(text = ach.first, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (ach.third) Color.White else Color(0xFF94A3B8))
                            Text(text = ach.second, fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                    }
                    if (ach.third) {
                        Text(text = "UNLOCKED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EnergyGreen)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    var soundEnabled by remember { mutableStateOf(true) }
    var hapticsEnabled by remember { mutableStateOf(true) }
    var highFpsMode by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(Screen.HOME) },
                modifier = Modifier
                    .testTag("settings_back_button")
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceElevated)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "SETTINGS & KINEMATICS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Hardware & Audio Options",
                    fontSize = 11.sp,
                    color = EnergyCyan
                )
            }
        }

        MetallicCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Mechanical Sound FX", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text(text = "Synthesized metallic clicks, whirs, motor hum", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = {
                            soundEnabled = it
                            viewModel.audioEngine.setMuted(!it)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = EnergyCyan, checkedTrackColor = Color(0xFF0284C7))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Haptic Feedback", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text(text = "Physical vibration upon gear mesh and ignition", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    }
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = { hapticsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = EnergyCyan, checkedTrackColor = Color(0xFF0284C7))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "60 FPS Precision Simulation", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text(text = "High-fidelity sub-millisecond physics clock", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    }
                    Switch(
                        checked = highFpsMode,
                        onCheckedChange = { highFpsMode = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = EnergyCyan, checkedTrackColor = Color(0xFF0284C7))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        MetallicButton(
            text = "RECALIBRATE MACHINE (RESET CURRENT STAGE)",
            onClick = { viewModel.resetLevel() },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
