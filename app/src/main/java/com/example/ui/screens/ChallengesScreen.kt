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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EnergyCyan
import com.example.ui.theme.MetalGold
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.Screen

data class ChallengeItem(
    val title: String,
    val subtitle: String,
    val icon: String,
    val targetLevel: Int,
    val rewardText: String
)

@Composable
fun ChallengesScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val challenges = listOf(
        ChallengeItem(
            title = "DAILY MECHANICAL PUZZLE",
            subtitle = "Today's bespoke clockwork challenge with bonus parts rewards.",
            icon = "📅",
            targetLevel = 7,
            rewardText = "+120 Coins • +15 Parts"
        ),
        ChallengeItem(
            title = "TIME ATTACK SPEEDRUN",
            subtitle = "Assemble and activate the complete 4-gear network under 45 seconds.",
            icon = "⏱",
            targetLevel = 5,
            rewardText = "Speedrunner Trophy"
        ),
        ChallengeItem(
            title = "MINIMALIST ENGINEER",
            subtitle = "Connect the Start Motor to Target using at most 2 inventory gears.",
            icon = "🔩",
            targetLevel = 3,
            rewardText = "+80 Coins"
        ),
        ChallengeItem(
            title = "PRECISION RPM CALIBRATION",
            subtitle = "Select exact gear ratios to hit precisely 120.0 RPM output velocity.",
            icon = "🎯",
            targetLevel = 3,
            rewardText = "Precision Master"
        ),
        ChallengeItem(
            title = "MAXIMUM TORQUE FOUNDRY",
            subtitle = "Drive heavy cast iron flywheel using stepped reduction axles.",
            icon = "⚙",
            targetLevel = 8,
            rewardText = "+200 Coins"
        ),
        ChallengeItem(
            title = "MULTI-AXIS 5D SIMULATION",
            subtitle = "Navigate across 3 distinct mechanical depth layers seamlessly.",
            icon = "🔮",
            targetLevel = 4,
            rewardText = "5D Dimension Badge"
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(Screen.HOME) },
                modifier = Modifier
                    .testTag("challenges_back_button")
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
                    text = "SPECIAL CHALLENGES",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "High-Stakes Mechanical Trials",
                    fontSize = 11.sp,
                    color = MetalGold
                )
            }
        }

        // List of Challenge cards
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(challenges) { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("challenge_${item.title.take(10)}")
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(listOf(DarkSurfaceElevated, DarkSurface))
                        )
                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(14.dp))
                        .clickable {
                            viewModel.loadLevel(item.targetLevel)
                            viewModel.navigateTo(Screen.GAME)
                        }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = item.icon, fontSize = 28.sp)
                            Column {
                                Text(
                                    text = item.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = item.subtitle,
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "REWARD: ${item.rewardText}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EnergyCyan
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Start Challenge",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}
