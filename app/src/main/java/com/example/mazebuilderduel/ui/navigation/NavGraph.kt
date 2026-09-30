package com.example.mazebuilderduel.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mazebuilderduel.ui.screens.AboutScreen
import com.example.mazebuilderduel.ui.screens.GameScreen
import com.example.mazebuilderduel.ui.screens.HomeScreen
import com.example.mazebuilderduel.ui.screens.HowToPlayScreen
import com.example.mazebuilderduel.ui.screens.ResultScreen
import com.example.mazebuilderduel.ui.screens.SettingsScreen
import com.example.mazebuilderduel.ui.screens.SplashScreen
import com.example.mazebuilderduel.ui.viewmodel.GameViewModel
import com.example.mazebuilderduel.ui.viewmodel.SettingsViewModel

@Composable
fun MazeBuilderNavGraph(
    navController: NavHostController = rememberNavController(),
    gameViewModel: GameViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val statistics by settingsViewModel.statistics.collectAsState()
    val gameState by gameViewModel.gameState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                statistics = statistics,
                onPlayClick = {
                    gameViewModel.restartGame()
                    navController.navigate(Screen.Game.route)
                },
                onHowToPlayClick = { navController.navigate(Screen.HowToPlay.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onAboutClick = { navController.navigate(Screen.About.route) }
            )
        }

        composable(Screen.Game.route) {
            GameScreen(
                viewModel = gameViewModel,
                onNavigateToResult = {
                    navController.navigate(Screen.Result.route) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                },
                onNavigateHome = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }

        composable(Screen.Result.route) {
            ResultScreen(
                state = gameState,
                onPlayAgainClick = {
                    gameViewModel.restartGame()
                    navController.navigate(Screen.Game.route) {
                        popUpTo(Screen.Result.route) { inclusive = true }
                    }
                },
                onHomeClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.HowToPlay.route) {
            HowToPlayScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
