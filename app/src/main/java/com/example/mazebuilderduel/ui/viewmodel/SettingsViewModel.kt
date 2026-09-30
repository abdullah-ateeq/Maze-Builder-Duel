package com.example.mazebuilderduel.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mazebuilderduel.data.SettingsRepository
import com.example.mazebuilderduel.data.StatisticsRepository
import com.example.mazebuilderduel.model.BoardTheme
import com.example.mazebuilderduel.model.GameStatistics
import com.example.mazebuilderduel.model.SettingsState
import com.example.mazebuilderduel.model.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepo = SettingsRepository(application)
    private val statsRepo = StatisticsRepository(application)

    val settings: StateFlow<SettingsState> = settingsRepo.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, SettingsState())

    val statistics: StateFlow<GameStatistics> = statsRepo.statisticsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, GameStatistics())

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepo.setThemeMode(mode)
        }
    }

    fun setBoardTheme(theme: BoardTheme) {
        viewModelScope.launch {
            settingsRepo.setBoardTheme(theme)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.setSoundEnabled(enabled)
        }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.setHapticsEnabled(enabled)
        }
    }

    fun setShowLegalMoves(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.setShowLegalMoves(enabled)
        }
    }

    fun setShowLastAction(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.setShowLastAction(enabled)
        }
    }

    fun resetStatistics() {
        viewModelScope.launch {
            statsRepo.resetStatistics()
        }
    }
}
