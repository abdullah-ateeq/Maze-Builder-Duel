package com.example.mazebuilderduel.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mazebuilderduel.model.BoardTheme
import com.example.mazebuilderduel.model.SettingsState
import com.example.mazebuilderduel.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_prefs")

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val BOARD_THEME = stringPreferencesKey("board_theme")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val SHOW_LEGAL_MOVES = booleanPreferencesKey("show_legal_moves")
        val SHOW_LAST_ACTION = booleanPreferencesKey("show_last_action")
    }

    val settingsFlow: Flow<SettingsState> = context.settingsDataStore.data.map { prefs ->
        val themeModeStr = prefs[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
        val boardThemeStr = prefs[PreferencesKeys.BOARD_THEME] ?: BoardTheme.MIDNIGHT.name
        val sound = prefs[PreferencesKeys.SOUND_ENABLED] ?: true
        val haptics = prefs[PreferencesKeys.HAPTICS_ENABLED] ?: true
        val showLegal = prefs[PreferencesKeys.SHOW_LEGAL_MOVES] ?: true
        val showLast = prefs[PreferencesKeys.SHOW_LAST_ACTION] ?: true

        SettingsState(
            themeMode = runCatching { ThemeMode.valueOf(themeModeStr) }.getOrDefault(ThemeMode.SYSTEM),
            boardTheme = runCatching { BoardTheme.valueOf(boardThemeStr) }.getOrDefault(BoardTheme.MIDNIGHT),
            soundEnabled = sound,
            hapticsEnabled = haptics,
            showLegalMoves = showLegal,
            showLastAction = showLast
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { it[PreferencesKeys.THEME_MODE] = mode.name }
    }

    suspend fun setBoardTheme(theme: BoardTheme) {
        context.settingsDataStore.edit { it[PreferencesKeys.BOARD_THEME] = theme.name }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[PreferencesKeys.SOUND_ENABLED] = enabled }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[PreferencesKeys.HAPTICS_ENABLED] = enabled }
    }

    suspend fun setShowLegalMoves(enabled: Boolean) {
        context.settingsDataStore.edit { it[PreferencesKeys.SHOW_LEGAL_MOVES] = enabled }
    }

    suspend fun setShowLastAction(enabled: Boolean) {
        context.settingsDataStore.edit { it[PreferencesKeys.SHOW_LAST_ACTION] = enabled }
    }
}
