package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LevelRepository
import com.example.model.World
import com.example.ui.components.StarRatingRow
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
fun LevelsScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val progressList by viewModel.levelProgressList.collectAsState()
    val profile by viewModel.playerProfile.collectAsState()
    val highestUnlocked = profile?.highestLevelUnlocked ?: 1

    val worlds = LevelRepository.worlds
    var selectedWorldId by remember { mutableStateOf(1) }
    val currentWorld = worlds.find { it.id == selectedWorldId } ?: worlds[0]

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.HOME) },
                    modifier = Modifier
                        .testTag("levels_back_button")
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Home",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "MECHANICAL STAGES",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "500 Stages across 10 Industrial Sectors",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Text(
                text = "⭐ ${progressList.sumOf { it.stars }}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MetalGold,
                fontFamily = FontFamily.Monospace
            )
        }

        // Worlds horizontal selector tabs
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(worlds) { world ->
                val isSelected = world.id == selectedWorldId
                val worldLevels = world.levelRange
                val isWorldUnlocked = highestUnlocked >= worldLevels.first

                Box(
                    modifier = Modifier
                        .testTag("world_tab_${world.id}")
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) {
                                Brush.verticalGradient(listOf(Color(0xFF0284C7), Color(0xFF0369A1)))
                            } else {
                                Brush.verticalGradient(listOf(DarkSurfaceElevated, DarkSurface))
                            }
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) EnergyCyan else DarkSurfaceBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedWorldId = world.id }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "SECTOR 0${world.id}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF94A3B8)
                        )
                        if (!isWorldUnlocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }

        // World Info Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "${currentWorld.name.uppercase()} (LEVELS ${currentWorld.levelRange.first}–${currentWorld.levelRange.last})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EnergyCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = currentWorld.subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = currentWorld.ambientDescription,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        // Levels Grid for Selected World
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val levelIds = currentWorld.levelRange.toList()
            items(levelIds) { levelId ->
                val isUnlocked = levelId <= highestUnlocked
                val progress = progressList.find { it.levelId == levelId }
                val isCompleted = progress?.isCompleted == true
                val stars = progress?.stars ?: 0

                LevelGridTile(
                    levelId = levelId,
                    isUnlocked = isUnlocked,
                    isCompleted = isCompleted,
                    stars = stars,
                    onClick = {
                        if (isUnlocked) {
                            viewModel.loadLevel(levelId)
                            viewModel.navigateTo(Screen.GAME)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun LevelGridTile(
    levelId: Int,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    stars: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(62.dp)
            .testTag("level_tile_$levelId")
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isCompleted -> Brush.verticalGradient(listOf(Color(0xFF0F2537), Color(0xFF0B1926)))
                    isUnlocked -> Brush.verticalGradient(listOf(DarkSurfaceElevated, DarkSurface))
                    else -> Brush.verticalGradient(listOf(Color(0xFF0A0F1A), Color(0xFF060910)))
                }
            )
            .border(
                width = 1.dp,
                color = when {
                    isCompleted -> EnergyGreen
                    isUnlocked -> EnergyCyan.copy(alpha = 0.6f)
                    else -> DarkSurfaceBorder
                },
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = isUnlocked, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (!isUnlocked) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = Color(0xFF475569),
                modifier = Modifier.size(16.dp)
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$levelId",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) Color.White else Color(0xFFE2E8F0),
                    fontFamily = FontFamily.Monospace
                )
                if (isCompleted) {
                    StarRatingRow(stars = stars, starSize = 10)
                }
            }
        }
    }
}
