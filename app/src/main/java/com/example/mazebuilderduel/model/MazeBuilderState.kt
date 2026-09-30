package com.example.mazebuilderduel.model

data class MazeBuilderState(
    val board: List<List<CellOwner>>,
    val walls: Set<Wall> = emptySet(),
    val currentPlayer: Player = Player.PLAYER_ONE,
    val actionMode: ActionMode = ActionMode.PATH,
    val selectedWallOrientation: WallOrientation = WallOrientation.HORIZONTAL,
    val player1WallsRemaining: Int = 5,
    val player2WallsRemaining: Int = 5,
    val legalPathCells: Set<GridPosition> = emptySet(),
    val lastPathCell: GridPosition? = null,
    val lastWall: Wall? = null,
    val winner: Player? = null,
    val winReason: WinReason? = null,
    val isDraw: Boolean = false,
    val moveCount: Int = 0,
    val isAnimating: Boolean = false,
    val player1DistanceToGoal: Int? = null,
    val player2DistanceToGoal: Int? = null,
    val startTimeMillis: Long = System.currentTimeMillis(),
    val matchDurationSeconds: Long = 0L
) {
    val isGameOver: Boolean
        get() = winner != null || isDraw

    val player1CellCount: Int
        get() = board.sumOf { row -> row.count { it == CellOwner.PLAYER_ONE } }

    val player2CellCount: Int
        get() = board.sumOf { row -> row.count { it == CellOwner.PLAYER_TWO } }

    val currentWallsRemaining: Int
        get() = when (currentPlayer) {
            Player.PLAYER_ONE -> player1WallsRemaining
            Player.PLAYER_TWO -> player2WallsRemaining
        }
}
