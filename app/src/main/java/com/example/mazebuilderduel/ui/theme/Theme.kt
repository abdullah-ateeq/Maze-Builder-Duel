package com.example.mazebuilderduel.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.mazebuilderduel.model.BoardTheme
import com.example.mazebuilderduel.model.ThemeMode

private val MidnightDarkColorScheme = darkColorScheme(
    primary = WallBronze,
    onPrimary = MidnightBg,
    primaryContainer = WallBronze.copy(alpha = 0.2f),
    onPrimaryContainer = WallIvory,
    secondary = Player2Teal,
    onSecondary = Color.White,
    secondaryContainer = Player2TealContainer,
    tertiary = Player1Copper,
    onTertiary = Color.White,
    tertiaryContainer = Player1CopperContainer,
    background = MidnightBg,
    onBackground = TextLight,
    surface = MidnightSurface,
    onSurface = TextLight,
    surfaceVariant = MidnightSurfaceElevated,
    onSurfaceVariant = TextMuted,
    outline = MidnightBorder,
    outlineVariant = MidnightBorder.copy(alpha = 0.5f)
)

private val ClassicDarkColorScheme = darkColorScheme(
    primary = WallBronze,
    onPrimary = ClassicDarkBg,
    primaryContainer = WallBronze.copy(alpha = 0.2f),
    onPrimaryContainer = WallIvory,
    secondary = Player2TealBright,
    onSecondary = Color.White,
    secondaryContainer = Player2TealContainer,
    tertiary = Player1CopperBright,
    onTertiary = Color.White,
    tertiaryContainer = Player1CopperContainer,
    background = ClassicDarkBg,
    onBackground = TextLight,
    surface = ClassicDarkSurface,
    onSurface = TextLight,
    surfaceVariant = ClassicDarkSurfaceElevated,
    onSurfaceVariant = TextMuted,
    outline = ClassicDarkBorder,
    outlineVariant = ClassicDarkBorder.copy(alpha = 0.5f)
)

private val LightArchitecturalColorScheme = lightColorScheme(
    primary = Player1CopperDark,
    onPrimary = Color.White,
    primaryContainer = WallBronze.copy(alpha = 0.25f),
    onPrimaryContainer = TextDark,
    secondary = Player2TealDark,
    onSecondary = Color.White,
    secondaryContainer = Player2Teal.copy(alpha = 0.15f),
    tertiary = Player1Copper,
    onTertiary = Color.White,
    tertiaryContainer = Player1Copper.copy(alpha = 0.15f),
    background = LightBg,
    onBackground = TextDark,
    surface = LightSurface,
    onSurface = TextDark,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = Color(0xFF4B5563),
    outline = LightBorder,
    outlineVariant = LightBorder.copy(alpha = 0.6f)
)

@Composable
fun MazeBuilderDuelTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    boardTheme: BoardTheme = BoardTheme.MIDNIGHT,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = when {
        !isDark -> LightArchitecturalColorScheme
        boardTheme == BoardTheme.CLASSIC -> ClassicDarkColorScheme
        else -> MidnightDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}