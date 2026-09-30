package com.example.mazebuilderduel.logic

import com.example.mazebuilderduel.model.GridPosition

object BoardConstants {
    const val BOARD_SIZE = 7
    const val INITIAL_WALLS = 5

    val PLAYER_ONE_START = GridPosition(row = 6, col = 0)
    val PLAYER_TWO_START = GridPosition(row = 0, col = 6)

    const val PLAYER_ONE_GOAL_ROW = 0
    const val PLAYER_TWO_GOAL_ROW = 6
}
