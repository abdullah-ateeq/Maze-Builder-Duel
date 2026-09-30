package com.example.mazebuilderduel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.mazebuilderduel.ui.navigation.MazeBuilderNavGraph
import com.example.mazebuilderduel.ui.theme.MazeBuilderDuelTheme
import com.example.mazebuilderduel.ui.viewmodel.GameViewModel
import com.example.mazebuilderduel.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by settingsViewModel.settings.collectAsState()

            MazeBuilderDuelTheme(
                themeMode = settings.themeMode,
                boardTheme = settings.boardTheme
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MazeBuilderNavGraph(
                        gameViewModel = gameViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}