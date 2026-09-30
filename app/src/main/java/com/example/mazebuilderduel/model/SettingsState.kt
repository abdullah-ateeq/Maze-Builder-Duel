package com.example.mazebuilderduel.model

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class BoardTheme {
    MIDNIGHT,
    CLASSIC
}

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val boardTheme: BoardTheme = BoardTheme.MIDNIGHT,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val showLegalMoves: Boolean = true,
    val showLastAction: Boolean = true
)
