package com.example.mazebuilderduel.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mazebuilderduel.audio.HapticFeedbackType
import com.example.mazebuilderduel.audio.HapticManager
import com.example.mazebuilderduel.audio.SoundManager
import com.example.mazebuilderduel.audio.SoundType
import com.example.mazebuilderduel.data.SettingsRepository
import com.example.mazebuilderduel.data.StatisticsRepository
import com.example.mazebuilderduel.logic.MazeEngine
import com.example.mazebuilderduel.logic.PathClaimResult
import com.example.mazebuilderduel.logic.WallPlacementResult
import com.example.mazebuilderduel.model.ActionMode
import com.example.mazebuilderduel.model.GridPosition
import com.example.mazebuilderduel.model.MazeBuilderState
import com.example.mazebuilderduel.model.SettingsState
import com.example.mazebuilderduel.model.Wall
import com.example.mazebuilderduel.model.WallOrientation
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.ArrayDeque

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepo = SettingsRepository(application)
    private val statsRepo = StatisticsRepository(application)
    private val soundManager = SoundManager()
    private val hapticManager = HapticManager(application)

    val settings: StateFlow<SettingsState> = settingsRepo.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, SettingsState())

    private val _gameState = MutableStateFlow(MazeEngine.createInitialState())
    val gameState: StateFlow<MazeBuilderState> = _gameState.asStateFlow()

    private val _warningMessage = MutableStateFlow<String?>(null)
    val warningMessage: StateFlow<String?> = _warningMessage.asStateFlow()

    private val undoStack = ArrayDeque<MazeBuilderState>()

    private var warningJob: Job? = null

    val canUndo: Boolean
        get() = undoStack.isNotEmpty() && !_gameState.value.isAnimating

    fun setActionMode(mode: ActionMode) {
        val current = _gameState.value
        if (current.isGameOver) return
        if (mode == ActionMode.WALL && current.currentWallsRemaining <= 0) return
        _gameState.value = current.copy(actionMode = mode)
    }

    fun setWallOrientation(orientation: WallOrientation) {
        val current = _gameState.value
        if (current.isGameOver) return
        _gameState.value = current.copy(selectedWallOrientation = orientation)
    }

    fun claimPathCell(position: GridPosition) {
        val current = _gameState.value
        if (current.isGameOver || current.isAnimating) return
        if (current.actionMode != ActionMode.PATH) return

        val prevSnapshot = current

        when (val result = MazeEngine.claimPathCell(current, position)) {
            is PathClaimResult.Success -> {
                undoStack.push(prevSnapshot)
                val soundEnabled = settings.value.soundEnabled
                val hapticsEnabled = settings.value.hapticsEnabled

                soundManager.play(SoundType.PATH_CLAIMED, soundEnabled)
                hapticManager.performHaptic(HapticFeedbackType.PATH_CLAIM, hapticsEnabled)

                val newState = result.newState
                _gameState.value = newState

                if (newState.isGameOver) {
                    soundManager.play(SoundType.WIN, soundEnabled)
                    hapticManager.performHaptic(HapticFeedbackType.WIN, hapticsEnabled)
                    recordGameOver(newState)
                } else {
                    soundManager.play(SoundType.TURN_SWITCH, soundEnabled)
                }
            }
            is PathClaimResult.Invalid -> {
                // Illegal move ignored safely
            }
        }
    }

    fun placeWall(wall: Wall) {
        val current = _gameState.value
        if (current.isGameOver || current.isAnimating) return
        if (current.actionMode != ActionMode.WALL) return
        if (current.currentWallsRemaining <= 0) return

        val prevSnapshot = current

        when (val result = MazeEngine.placeWall(current, wall)) {
            is WallPlacementResult.Success -> {
                undoStack.push(prevSnapshot)
                val soundEnabled = settings.value.soundEnabled
                val hapticsEnabled = settings.value.hapticsEnabled

                soundManager.play(SoundType.WALL_PLACED, soundEnabled)
                hapticManager.performHaptic(HapticFeedbackType.WALL_PLACE, hapticsEnabled)

                val newState = result.newState
                _gameState.value = newState

                if (newState.isGameOver) {
                    soundManager.play(SoundType.WIN, soundEnabled)
                    hapticManager.performHaptic(HapticFeedbackType.WIN, hapticsEnabled)
                    recordGameOver(newState)
                } else {
                    soundManager.play(SoundType.TURN_SWITCH, soundEnabled)
                }
            }
            is WallPlacementResult.Invalid -> {
                showWarning(result.reason)
                soundManager.play(SoundType.INVALID_WALL, settings.value.soundEnabled)
                hapticManager.performHaptic(HapticFeedbackType.INVALID_WARNING, settings.value.hapticsEnabled)
            }
        }
    }

    fun undoMove() {
        if (undoStack.isEmpty() || _gameState.value.isAnimating) return
        val restored = undoStack.pop()
        _gameState.value = restored
        hapticManager.performHaptic(HapticFeedbackType.PATH_CLAIM, settings.value.hapticsEnabled)
    }

    fun restartGame() {
        undoStack.clear()
        _gameState.value = MazeEngine.createInitialState()
        _warningMessage.value = null
    }

    private fun showWarning(message: String) {
        _warningMessage.value = message
        warningJob?.cancel()
        warningJob = viewModelScope.launch {
            delay(2200)
            if (_warningMessage.value == message) {
                _warningMessage.value = null
            }
        }
    }

    fun dismissWarning() {
        _warningMessage.value = null
    }

    private fun recordGameOver(state: MazeBuilderState) {
        viewModelScope.launch {
            statsRepo.recordGameResult(
                winner = state.winner,
                isDraw = state.isDraw,
                turns = state.moveCount
            )
        }
    }
}
