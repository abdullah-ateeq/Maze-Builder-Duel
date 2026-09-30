package com.example.mazebuilderduel.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Game : Screen("game")
    data object Result : Screen("result")
    data object HowToPlay : Screen("how_to_play")
    data object Settings : Screen("settings")
    data object About : Screen("about")
}
