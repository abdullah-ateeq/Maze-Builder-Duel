package com.example.mazebuilderduel.model

data class GameStatistics(
    val gamesPlayed: Int = 0,
    val player1Wins: Int = 0,
    val player2Wins: Int = 0,
    val draws: Int = 0,
    val fewestTurns: Int? = null
)
