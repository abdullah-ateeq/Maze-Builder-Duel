package com.example.mazebuilderduel.model

enum class CellOwner {
    EMPTY,
    PLAYER_ONE,
    PLAYER_TWO;

    fun toPlayer(): Player? = when (this) {
        PLAYER_ONE -> Player.PLAYER_ONE
        PLAYER_TWO -> Player.PLAYER_TWO
        EMPTY -> null
    }
}
