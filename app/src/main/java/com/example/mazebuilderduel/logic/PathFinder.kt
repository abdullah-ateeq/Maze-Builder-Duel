package com.example.mazebuilderduel.logic

import com.example.mazebuilderduel.model.CellOwner
import com.example.mazebuilderduel.model.GridPosition
import com.example.mazebuilderduel.model.Player
import com.example.mazebuilderduel.model.Wall
import com.example.mazebuilderduel.model.WallOrientation
import java.util.ArrayDeque
import kotlin.math.abs
import kotlin.math.min

object PathFinder {

    /**
     * Checks if movement between two positions is blocked by a wall or if the movement is not orthogonal.
     */
    fun isEdgeBlocked(
        from: GridPosition,
        to: GridPosition,
        walls: Set<Wall>
    ): Boolean {
        val dRow = to.row - from.row
        val dCol = to.col - from.col

        if (abs(dRow) + abs(dCol) != 1) {
            return true
        }

        return if (dCol == 0) {
            val minRow = min(from.row, to.row)
            walls.contains(Wall(row = minRow, col = from.col, orientation = WallOrientation.HORIZONTAL))
        } else {
            val minCol = min(from.col, to.col)
            walls.contains(Wall(row = from.row, col = minCol, orientation = WallOrientation.VERTICAL))
        }
    }

    /**
     * Gets valid orthogonal neighbors within board boundaries.
     */
    fun getNeighbors(position: GridPosition): List<GridPosition> {
        val neighbors = ArrayList<GridPosition>(4)
        val r = position.row
        val c = position.col

        if (r > 0) neighbors.add(GridPosition(r - 1, c))
        if (r < BoardConstants.BOARD_SIZE - 1) neighbors.add(GridPosition(r + 1, c))
        if (c > 0) neighbors.add(GridPosition(r, c - 1))
        if (c < BoardConstants.BOARD_SIZE - 1) neighbors.add(GridPosition(r, c + 1))

        return neighbors
    }

    /**
     * Finds all legal empty cells the given player can expand to.
     */
    fun getLegalPathCells(
        player: Player,
        board: List<List<CellOwner>>,
        walls: Set<Wall>
    ): Set<GridPosition> {
        val legalCells = mutableSetOf<GridPosition>()
        val targetOwner = player.toCellOwner()

        for (r in 0 until BoardConstants.BOARD_SIZE) {
            for (c in 0 until BoardConstants.BOARD_SIZE) {
                if (board[r][c] == targetOwner) {
                    val currentPos = GridPosition(r, c)
                    for (neighbor in getNeighbors(currentPos)) {
                        if (board[neighbor.row][neighbor.col] == CellOwner.EMPTY) {
                            if (!isEdgeBlocked(currentPos, neighbor, walls)) {
                                legalCells.add(neighbor)
                            }
                        }
                    }
                }
            }
        }
        return legalCells
    }

    /**
     * Checks if the player has any legal path expansion move.
     */
    fun hasAnyPathExpansion(
        player: Player,
        board: List<List<CellOwner>>,
        walls: Set<Wall>
    ): Boolean {
        return getLegalPathCells(player, board, walls).isNotEmpty()
    }

    /**
     * Evaluates whether the given player has at least one theoretical route to their goal row.
     * Treats empty cells and player's owned cells as traversable. Opponent cells are blocked.
     */
    fun hasRouteToGoal(
        player: Player,
        board: List<List<CellOwner>>,
        walls: Set<Wall>
    ): Boolean {
        val targetRow = when (player) {
            Player.PLAYER_ONE -> BoardConstants.PLAYER_ONE_GOAL_ROW
            Player.PLAYER_TWO -> BoardConstants.PLAYER_TWO_GOAL_ROW
        }

        val queue = ArrayDeque<GridPosition>()
        val visited = mutableSetOf<GridPosition>()
        val playerOwner = player.toCellOwner()

        // Seed BFS with all current cells owned by player
        for (r in 0 until BoardConstants.BOARD_SIZE) {
            for (c in 0 until BoardConstants.BOARD_SIZE) {
                if (board[r][c] == playerOwner) {
                    val pos = GridPosition(r, c)
                    if (pos.row == targetRow) return true
                    queue.add(pos)
                    visited.add(pos)
                }
            }
        }

        while (queue.isNotEmpty()) {
            val current = queue.pollFirst() ?: break

            if (current.row == targetRow) {
                return true
            }

            for (neighbor in getNeighbors(current)) {
                if (neighbor !in visited) {
                    val cellOwner = board[neighbor.row][neighbor.col]
                    // Traversable if EMPTY or already owned by this player
                    if (cellOwner == CellOwner.EMPTY || cellOwner == playerOwner) {
                        if (!isEdgeBlocked(current, neighbor, walls)) {
                            visited.add(neighbor)
                            queue.add(neighbor)
                        }
                    }
                }
            }
        }

        return false
    }

    /**
     * Calculates the shortest BFS distance from the player's owned path network to their goal row.
     * Returns null if no route is found.
     */
    fun shortestDistanceToGoal(
        player: Player,
        board: List<List<CellOwner>>,
        walls: Set<Wall>
    ): Int? {
        val targetRow = when (player) {
            Player.PLAYER_ONE -> BoardConstants.PLAYER_ONE_GOAL_ROW
            Player.PLAYER_TWO -> BoardConstants.PLAYER_TWO_GOAL_ROW
        }

        val queue = ArrayDeque<Pair<GridPosition, Int>>()
        val visited = mutableSetOf<GridPosition>()
        val playerOwner = player.toCellOwner()

        for (r in 0 until BoardConstants.BOARD_SIZE) {
            for (c in 0 until BoardConstants.BOARD_SIZE) {
                if (board[r][c] == playerOwner) {
                    val pos = GridPosition(r, c)
                    if (pos.row == targetRow) return 0
                    queue.add(pos to 0)
                    visited.add(pos)
                }
            }
        }

        while (queue.isNotEmpty()) {
            val (current, dist) = queue.pollFirst() ?: break

            if (current.row == targetRow) {
                return dist
            }

            for (neighbor in getNeighbors(current)) {
                if (neighbor !in visited) {
                    val cellOwner = board[neighbor.row][neighbor.col]
                    if (cellOwner == CellOwner.EMPTY || cellOwner == playerOwner) {
                        if (!isEdgeBlocked(current, neighbor, walls)) {
                            visited.add(neighbor)
                            queue.add(neighbor to (dist + 1))
                        }
                    }
                }
            }
        }

        return null
    }

    /**
     * Checks if either player has reached their goal row.
     */
    fun hasReachedGoal(
        player: Player,
        board: List<List<CellOwner>>
    ): Boolean {
        val targetRow = when (player) {
            Player.PLAYER_ONE -> BoardConstants.PLAYER_ONE_GOAL_ROW
            Player.PLAYER_TWO -> BoardConstants.PLAYER_TWO_GOAL_ROW
        }
        val targetOwner = player.toCellOwner()

        for (c in 0 until BoardConstants.BOARD_SIZE) {
            if (board[targetRow][c] == targetOwner) {
                return true
            }
        }
        return false
    }
}
