package com.example.mazebuilderduel.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mazebuilderduel.model.GameStatistics
import com.example.mazebuilderduel.model.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.min

private val Context.statisticsDataStore: DataStore<Preferences> by preferencesDataStore(name = "statistics_prefs")

class StatisticsRepository(private val context: Context) {

    private object PreferencesKeys {
        val GAMES_PLAYED = intPreferencesKey("games_played")
        val PLAYER_1_WINS = intPreferencesKey("player_1_wins")
        val PLAYER_2_WINS = intPreferencesKey("player_2_wins")
        val DRAWS = intPreferencesKey("draws")
        val FEWEST_TURNS = intPreferencesKey("fewest_turns")
    }

    val statisticsFlow: Flow<GameStatistics> = context.statisticsDataStore.data.map { prefs ->
        val games = prefs[PreferencesKeys.GAMES_PLAYED] ?: 0
        val p1Wins = prefs[PreferencesKeys.PLAYER_1_WINS] ?: 0
        val p2Wins = prefs[PreferencesKeys.PLAYER_2_WINS] ?: 0
        val draws = prefs[PreferencesKeys.DRAWS] ?: 0
        val fewestTurns = prefs[PreferencesKeys.FEWEST_TURNS]

        GameStatistics(
            gamesPlayed = games,
            player1Wins = p1Wins,
            player2Wins = p2Wins,
            draws = draws,
            fewestTurns = if (fewestTurns != null && fewestTurns > 0) fewestTurns else null
        )
    }

    suspend fun recordGameResult(winner: Player?, isDraw: Boolean, turns: Int) {
        context.statisticsDataStore.edit { prefs ->
            val games = (prefs[PreferencesKeys.GAMES_PLAYED] ?: 0) + 1
            prefs[PreferencesKeys.GAMES_PLAYED] = games

            if (isDraw) {
                prefs[PreferencesKeys.DRAWS] = (prefs[PreferencesKeys.DRAWS] ?: 0) + 1
            } else when (winner) {
                Player.PLAYER_ONE -> {
                    prefs[PreferencesKeys.PLAYER_1_WINS] = (prefs[PreferencesKeys.PLAYER_1_WINS] ?: 0) + 1
                }
                Player.PLAYER_TWO -> {
                    prefs[PreferencesKeys.PLAYER_2_WINS] = (prefs[PreferencesKeys.PLAYER_2_WINS] ?: 0) + 1
                }
                null -> {}
            }

            if (winner != null && turns > 0) {
                val currentFewest = prefs[PreferencesKeys.FEWEST_TURNS]
                if (currentFewest == null || turns < currentFewest) {
                    prefs[PreferencesKeys.FEWEST_TURNS] = turns
                }
            }
        }
    }

    suspend fun resetStatistics() {
        context.statisticsDataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
