package com.example.mazebuilderduel.logic

import com.example.mazebuilderduel.model.ActionMode
import com.example.mazebuilderduel.model.CellOwner
import com.example.mazebuilderduel.model.GridPosition
import com.example.mazebuilderduel.model.MazeBuilderState
import com.example.mazebuilderduel.model.Player
import com.example.mazebuilderduel.model.Wall
import com.example.mazebuilderduel.model.WallOrientation
import com.example.mazebuilderduel.model.WinReason

sealed interface WallPlacementResult {
    data class Success(val newState: MazeBuilderState) : WallPlacementResult
    data class Invalid(val reason: String) : WallPlacementResult
}

sealed interface PathClaimResult {
    data class Success(val newState: MazeBuilderState) : PathClaimResult
    data class Invalid(val reason: String) : PathClaimResult
}

object MazeEngine {

    const val PATH_MUST_REMAIN_OPEN = "PATH MUST REMAIN OPEN"

    /**
     * Initializes a fresh 7x7 game board.
     * Player 1 at (6,0), Player 2 at (0,6).
     */
    fun createInitialState(): MazeBuilderState {
        val board = List(BoardConstants.BOARD_SIZE) { r ->
            List(BoardConstants.BOARD_SIZE) { c ->
                when {
                    r == BoardConstants.PLAYER_ONE_START.row && c == BoardConstants.PLAYER_ONE_START.col ->
                        CellOwner.PLAYER_ONE
                    r == BoardConstants.PLAYER_TWO_START.row && c == BoardConstants.PLAYER_TWO_START.col ->
                        CellOwner.PLAYER_TWO
                    else -> CellOwner.EMPTY
                }
            }
        }

        val walls = emptySet<Wall>()
        val initialPlayer = Player.PLAYER_ONE
        val legalCells = PathFinder.getLegalPathCells(initialPlayer, board, walls)
        val p1Dist = PathFinder.shortestDistanceToGoal(Player.PLAYER_ONE, board, walls)
        val p2Dist = PathFinder.shortestDistanceToGoal(Player.PLAYER_TWO, board, walls)

        return MazeBuilderState(
            board = board,
            walls = walls,
            currentPlayer = initialPlayer,
            actionMode = ActionMode.PATH,
            selectedWallOrientation = WallOrientation.HORIZONTAL,
            player1WallsRemaining = BoardConstants.INITIAL_WALLS,
            player2WallsRemaining = BoardConstants.INITIAL_WALLS,
            legalPathCells = legalCells,
            lastPathCell = null,
            lastWall = null,
            winner = null,
            winReason = null,
            isDraw = false,
            moveCount = 0,
            isAnimating = false,
            player1DistanceToGoal = p1Dist,
            player2DistanceToGoal = p2Dist,
            startTimeMillis = System.currentTimeMillis(),
            matchDurationSeconds = 0L
        )
    }

    /**
     * Validates if a wall can be placed by the player with the given board and walls.
     */
    fun canPlaceWall(
        wall: Wall,
        player: Player,
        walls: Set<Wall>,
        board: List<List<CellOwner>>,
        wallsRemaining: Int
    ): Pair<Boolean, String?> {
        if (wallsRemaining <= 0) {
            return false to "No walls remaining"
        }

        // Validate boundary constraints
        val isValidCoordinates = when (wall.orientation) {
            WallOrientation.HORIZONTAL ->
                wall.row in 0 until (BoardConstants.BOARD_SIZE - 1) &&
                        wall.col in 0 until BoardConstants.BOARD_SIZE
            WallOrientation.VERTICAL ->
                wall.row in 0 until BoardConstants.BOARD_SIZE &&
                        wall.col in 0 until (BoardConstants.BOARD_SIZE - 1)
        }

        if (!isValidCoordinates) {
            return false to "Invalid wall position"
        }

        if (walls.contains(wall)) {
            return false to "Wall already exists here"
        }

        val candidateWalls = walls + wall

        // Check Player 1 theoretical route
        if (!PathFinder.hasRouteToGoal(Player.PLAYER_ONE, board, candidateWalls)) {
            return false to PATH_MUST_REMAIN_OPEN
        }

        // Check Player 2 theoretical route
        if (!PathFinder.hasRouteToGoal(Player.PLAYER_TWO, board, candidateWalls)) {
            return false to PATH_MUST_REMAIN_OPEN
        }

        return true to null
    }

    /**
     * Attempts to place a wall for the current player.
     */
    fun placeWall(
        currentState: MazeBuilderState,
        wall: Wall
    ): WallPlacementResult {
        if (currentState.isGameOver) {
            return WallPlacementResult.Invalid("Game is already over")
        }

        if (currentState.actionMode != ActionMode.WALL) {
            return WallPlacementResult.Invalid("Not in WALL action mode")
        }

        val (isValid, reason) = canPlaceWall(
            wall = wall,
            player = currentState.currentPlayer,
            walls = currentState.walls,
            board = currentState.board,
            wallsRemaining = currentState.currentWallsRemaining
        )

        if (!isValid) {
            return WallPlacementResult.Invalid(reason ?: PATH_MUST_REMAIN_OPEN)
        }

        val newWalls = currentState.walls + wall
        val nextP1Walls = if (currentState.currentPlayer == Player.PLAYER_ONE) {
            currentState.player1WallsRemaining - 1
        } else {
            currentState.player1WallsRemaining
        }
        val nextP2Walls = if (currentState.currentPlayer == Player.PLAYER_TWO) {
            currentState.player2WallsRemaining - 1
        } else {
            currentState.player2WallsRemaining
        }

        val nextPlayer = currentState.currentPlayer.opponent()
        val nextLegalCells = PathFinder.getLegalPathCells(nextPlayer, currentState.board, newWalls)
        val p1Dist = PathFinder.shortestDistanceToGoal(Player.PLAYER_ONE, currentState.board, newWalls)
        val p2Dist = PathFinder.shortestDistanceToGoal(Player.PLAYER_TWO, currentState.board, newWalls)

        val updatedState = currentState.copy(
            walls = newWalls,
            currentPlayer = nextPlayer,
            actionMode = ActionMode.PATH,
            player1WallsRemaining = nextP1Walls,
            player2WallsRemaining = nextP2Walls,
            legalPathCells = nextLegalCells,
            lastWall = wall,
            lastPathCell = null,
            moveCount = currentState.moveCount + 1,
            player1DistanceToGoal = p1Dist,
            player2DistanceToGoal = p2Dist
        )

        // Check fallback end condition if both are out of walls and moves
        return WallPlacementResult.Success(checkFallbackTiebreaker(updatedState))
    }

    /**
     * Claims a path cell for the current player.
     */
    fun claimPathCell(
        currentState: MazeBuilderState,
        position: GridPosition
    ): PathClaimResult {
        if (currentState.isGameOver) {
            return PathClaimResult.Invalid("Game is already over")
        }

        if (currentState.actionMode != ActionMode.PATH) {
            return PathClaimResult.Invalid("Not in PATH action mode")
        }

        if (position !in currentState.legalPathCells) {
            return PathClaimResult.Invalid("Illegal cell destination")
        }

        val updatedBoard = currentState.board.mapIndexed { r, row ->
            row.mapIndexed { c, cell ->
                if (r == position.row && c == position.col) {
                    currentState.currentPlayer.toCellOwner()
                } else {
                    cell
                }
            }
        }

        val hasWon = PathFinder.hasReachedGoal(currentState.currentPlayer, updatedBoard)
        val p1Dist = PathFinder.shortestDistanceToGoal(Player.PLAYER_ONE, updatedBoard, currentState.walls)
        val p2Dist = PathFinder.shortestDistanceToGoal(Player.PLAYER_TWO, updatedBoard, currentState.walls)

        if (hasWon) {
            val finalDuration = (System.currentTimeMillis() - currentState.startTimeMillis) / 1000
            return PathClaimResult.Success(
                currentState.copy(
                    board = updatedBoard,
                    winner = currentState.currentPlayer,
                    winReason = WinReason.GOAL_REACHED,
                    lastPathCell = position,
                    lastWall = null,
                    moveCount = currentState.moveCount + 1,
                    player1DistanceToGoal = p1Dist,
                    player2DistanceToGoal = p2Dist,
                    matchDurationSeconds = finalDuration
                )
            )
        }

        val nextPlayer = currentState.currentPlayer.opponent()
        val nextLegalCells = PathFinder.getLegalPathCells(nextPlayer, updatedBoard, currentState.walls)

        val intermediateState = currentState.copy(
            board = updatedBoard,
            currentPlayer = nextPlayer,
            actionMode = ActionMode.PATH,
            legalPathCells = nextLegalCells,
            lastPathCell = position,
            lastWall = null,
            moveCount = currentState.moveCount + 1,
            player1DistanceToGoal = p1Dist,
            player2DistanceToGoal = p2Dist
        )

        return PathClaimResult.Success(checkFallbackTiebreaker(intermediateState))
    }

    /**
     * Fallback tiebreaker check if neither player can expand and neither player has walls.
     */
    fun checkFallbackTiebreaker(state: MazeBuilderState): MazeBuilderState {
        if (state.isGameOver) return state

        val p1CanMove = PathFinder.hasAnyPathExpansion(Player.PLAYER_ONE, state.board, state.walls)
        val p2CanMove = PathFinder.hasAnyPathExpansion(Player.PLAYER_TWO, state.board, state.walls)

        if (!p1CanMove && !p2CanMove &&
            state.player1WallsRemaining == 0 &&
            state.player2WallsRemaining == 0
        ) {
            // Measure progress:
            // Player 1 target is row 0. Closer to row 0 is better.
            var p1MinRow = BoardConstants.BOARD_SIZE - 1
            var p2MaxRow = 0

            for (r in 0 until BoardConstants.BOARD_SIZE) {
                for (c in 0 until BoardConstants.BOARD_SIZE) {
                    if (state.board[r][c] == CellOwner.PLAYER_ONE) {
                        if (r < p1MinRow) p1MinRow = r
                    } else if (state.board[r][c] == CellOwner.PLAYER_TWO) {
                        if (r > p2MaxRow) p2MaxRow = r
                    }
                }
            }

            val p1DistanceToGoal = p1MinRow - BoardConstants.PLAYER_ONE_GOAL_ROW
            val p2DistanceToGoal = BoardConstants.PLAYER_TWO_GOAL_ROW - p2MaxRow

            val finalDuration = (System.currentTimeMillis() - state.startTimeMillis) / 1000

            return when {
                p1DistanceToGoal < p2DistanceToGoal -> state.copy(
                    winner = Player.PLAYER_ONE,
                    winReason = WinReason.FURTHEST_PROGRESS,
                    matchDurationSeconds = finalDuration
                )
                p2DistanceToGoal < p1DistanceToGoal -> state.copy(
                    winner = Player.PLAYER_TWO,
                    winReason = WinReason.FURTHEST_PROGRESS,
                    matchDurationSeconds = finalDuration
                )
                else -> state.copy(
                    isDraw = true,
                    matchDurationSeconds = finalDuration
                )
            }
        }

        return state
    }
}
