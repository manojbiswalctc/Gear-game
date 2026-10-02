package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.ChallengesScreen
import com.example.ui.screens.GameScreen
import com.example.ui.screens.GearLabScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LevelsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.GearForgeTheme
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GearForgeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    val gameViewModel: GameViewModel = viewModel(
                        factory = GameViewModel.Factory(applicationContext)
                    )
                    GearForgeApp(viewModel = gameViewModel)
                }
            }
        }
    }
}

@Composable
fun GearForgeApp(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle physical back button navigation
    if (uiState.currentScreen != Screen.HOME) {
        BackHandler {
            viewModel.navigateTo(Screen.HOME)
        }
    }

    when (uiState.currentScreen) {
        Screen.HOME -> HomeScreen(viewModel = viewModel)
        Screen.GAME -> GameScreen(viewModel = viewModel)
        Screen.LEVEL_SELECT -> LevelsScreen(viewModel = viewModel)
        Screen.GEAR_LAB -> GearLabScreen(viewModel = viewModel)
        Screen.CHALLENGES -> ChallengesScreen(viewModel = viewModel)
        Screen.PROFILE -> ProfileScreen(viewModel = viewModel)
        Screen.SETTINGS -> SettingsScreen(viewModel = viewModel)
    }
}
