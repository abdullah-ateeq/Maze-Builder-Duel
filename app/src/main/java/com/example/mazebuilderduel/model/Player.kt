package com.example.mazebuilderduel.model

enum class Player {
    PLAYER_ONE,
    PLAYER_TWO;

    fun opponent(): Player = when (this) {
        PLAYER_ONE -> PLAYER_TWO
        PLAYER_TWO -> PLAYER_ONE
    }

    fun toCellOwner(): CellOwner = when (this) {
        PLAYER_ONE -> CellOwner.PLAYER_ONE
        PLAYER_TWO -> CellOwner.PLAYER_TWO
    }

    val displayName: String
        get() = when (this) {
            PLAYER_ONE -> "Player 1"
            PLAYER_TWO -> "Player 2"
        }
}
