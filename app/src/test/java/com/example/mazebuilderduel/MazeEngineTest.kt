package com.example.mazebuilderduel

import com.example.mazebuilderduel.logic.BoardConstants
import com.example.mazebuilderduel.logic.MazeEngine
import com.example.mazebuilderduel.logic.PathFinder
import com.example.mazebuilderduel.logic.PathClaimResult
import com.example.mazebuilderduel.logic.WallPlacementResult
import com.example.mazebuilderduel.model.ActionMode
import com.example.mazebuilderduel.model.CellOwner
import com.example.mazebuilderduel.model.GridPosition
import com.example.mazebuilderduel.model.MazeBuilderState
import com.example.mazebuilderduel.model.Player
import com.example.mazebuilderduel.model.Wall
import com.example.mazebuilderduel.model.WallOrientation
import com.example.mazebuilderduel.model.WinReason
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MazeEngineTest {

    @Test
    fun testInitialSetup() {
        val state = MazeEngine.createInitialState()
        assertEquals(7, state.board.size)
        assertEquals(7, state.board[0].size)
        assertEquals(CellOwner.PLAYER_ONE, state.board[6][0])
        assertEquals(CellOwner.PLAYER_TWO, state.board[0][6])
        assertEquals(Player.PLAYER_ONE, state.currentPlayer)
        assertEquals(ActionMode.PATH, state.actionMode)
        assertEquals(5, state.player1WallsRemaining)
        assertEquals(5, state.player2WallsRemaining)
        assertTrue(state.walls.isEmpty())
        assertFalse(state.isGameOver)
        assertNull(state.winner)

        // Starting legal cells for Player 1 at (6,0) should be (5,0) and (6,1)
        val expectedLegal = setOf(GridPosition(5, 0), GridPosition(6, 1))
        assertEquals(expectedLegal, state.legalPathCells)
    }

    @Test
    fun testLegalOrthogonalPathExpansion() {
        val state = MazeEngine.createInitialState()
        val result = MazeEngine.claimPathCell(state, GridPosition(5, 0))

        assertTrue(result is PathClaimResult.Success)
        val newState = (result as PathClaimResult.Success).newState

        assertEquals(CellOwner.PLAYER_ONE, newState.board[5][0])
        assertEquals(Player.PLAYER_TWO, newState.currentPlayer)
        assertEquals(1, newState.moveCount)
        assertEquals(GridPosition(5, 0), newState.lastPathCell)

        // Player 2 at (0,6) should have legal cells (0,5) and (1,6)
        val expectedP2Legal = setOf(GridPosition(0, 5), GridPosition(1, 6))
        assertEquals(expectedP2Legal, newState.legalPathCells)
    }

    @Test
    fun testDiagonalExpansionRejection() {
        val state = MazeEngine.createInitialState()
        // (5, 1) is diagonal from (6, 0)
        val diagonal = GridPosition(5, 1)
        assertFalse(diagonal in state.legalPathCells)

        val result = MazeEngine.claimPathCell(state, diagonal)
        assertTrue(result is PathClaimResult.Invalid)
    }

    @Test
    fun testOpponentCellBlocksExpansion() {
        // Create state where Player 2 cell is adjacent to Player 1 cell
        var state = MazeEngine.createInitialState()
        // Manually place Player 2 at (5, 0)
        val customBoard = state.board.mapIndexed { r, row ->
            row.mapIndexed { c, cell ->
                if (r == 5 && c == 0) CellOwner.PLAYER_TWO else cell
            }
        }
        state = state.copy(
            board = customBoard,
            legalPathCells = PathFinder.getLegalPathCells(Player.PLAYER_ONE, customBoard, state.walls)
        )

        // (5, 0) is opponent-owned, so it cannot be in legal path cells
        assertFalse(GridPosition(5, 0) in state.legalPathCells)
        val claimOpponent = MazeEngine.claimPathCell(state, GridPosition(5, 0))
        assertTrue(claimOpponent is PathClaimResult.Invalid)
    }

    @Test
    fun testWallBlocksExpansion() {
        var state = MazeEngine.createInitialState()
        // Place horizontal wall at (5,0) which blocks between (5,0) and (6,0)
        val wall = Wall(row = 5, col = 0, orientation = WallOrientation.HORIZONTAL)
        val wallSet = setOf(wall)
        val legalCells = PathFinder.getLegalPathCells(Player.PLAYER_ONE, state.board, wallSet)

        // (5, 0) should be blocked by the wall
        assertFalse(GridPosition(5, 0) in legalCells)
        // (6, 1) should still be open
        assertTrue(GridPosition(6, 1) in legalCells)
    }

    @Test
    fun testValidWallPlacement() {
        var state = MazeEngine.createInitialState()
        state = state.copy(actionMode = ActionMode.WALL)

        // Horizontal wall at (4, 0)
        val wall = Wall(row = 4, col = 0, orientation = WallOrientation.HORIZONTAL)
        val result = MazeEngine.placeWall(state, wall)

        assertTrue(result is WallPlacementResult.Success)
        val newState = (result as WallPlacementResult.Success).newState

        assertTrue(wall in newState.walls)
        assertEquals(4, newState.player1WallsRemaining)
        assertEquals(5, newState.player2WallsRemaining)
        assertEquals(Player.PLAYER_TWO, newState.currentPlayer)
        assertEquals(ActionMode.PATH, newState.actionMode) // Reset to PATH
        assertEquals(wall, newState.lastWall)
    }

    @Test
    fun testDuplicateWallRejection() {
        var state = MazeEngine.createInitialState()
        val wall = Wall(row = 4, col = 0, orientation = WallOrientation.HORIZONTAL)
        state = state.copy(walls = setOf(wall), actionMode = ActionMode.WALL)

        val result = MazeEngine.placeWall(state, wall)
        assertTrue(result is WallPlacementResult.Invalid)
    }

    @Test
    fun testWallCountDecrement() {
        var state = MazeEngine.createInitialState()
        state = state.copy(actionMode = ActionMode.WALL)

        val wall1 = Wall(row = 4, col = 0, orientation = WallOrientation.HORIZONTAL)
        val res1 = MazeEngine.placeWall(state, wall1) as WallPlacementResult.Success
        assertEquals(4, res1.newState.player1WallsRemaining)
        assertEquals(5, res1.newState.player2WallsRemaining)

        // Player 2's turn
        val stateP2 = res1.newState.copy(actionMode = ActionMode.WALL)
        val wall2 = Wall(row = 2, col = 5, orientation = WallOrientation.VERTICAL)
        val res2 = MazeEngine.placeWall(stateP2, wall2) as WallPlacementResult.Success
        assertEquals(4, res2.newState.player1WallsRemaining)
        assertEquals(4, res2.newState.player2WallsRemaining)
    }

    @Test
    fun testWallBlockingPlayer1CompletelyRejected() {
        var state = MazeEngine.createInitialState()
        state = state.copy(actionMode = ActionMode.WALL)

        // P1 start is at (6, 0).
        // If we place horizontal wall at (5, 0) [blocks up to (5,0)]
        // and vertical wall at (6, 0) [blocks right to (6,1)]
        // Player 1 would be completely trapped!
        val wallUp = Wall(row = 5, col = 0, orientation = WallOrientation.HORIZONTAL)
        val wallRight = Wall(row = 6, col = 0, orientation = WallOrientation.VERTICAL)

        // Place wallUp first
        val step1 = MazeEngine.placeWall(state, wallUp) as WallPlacementResult.Success
        assertEquals(Player.PLAYER_TWO, step1.newState.currentPlayer)

        // Switch back to P1 or test directly with P2 trying to trap P1
        val step1P2 = step1.newState.copy(actionMode = ActionMode.WALL)
        val step2 = MazeEngine.placeWall(step1P2, wallRight)

        // Should be rejected because it completely eliminates P1's route!
        assertTrue(step2 is WallPlacementResult.Invalid)
        assertEquals(MazeEngine.PATH_MUST_REMAIN_OPEN, (step2 as WallPlacementResult.Invalid).reason)
    }

    @Test
    fun testWallBlockingPlayer2CompletelyRejected() {
        var state = MazeEngine.createInitialState()
        state = state.copy(
            currentPlayer = Player.PLAYER_TWO,
            actionMode = ActionMode.WALL
        )

        // P2 start is at (0, 6).
        // Down is (1, 6), blocked by horizontal wall at (0, 6)
        // Left is (0, 5), blocked by vertical wall at (0, 5)
        val wallDown = Wall(row = 0, col = 6, orientation = WallOrientation.HORIZONTAL)
        val wallLeft = Wall(row = 0, col = 5, orientation = WallOrientation.VERTICAL)

        // Add wallDown
        val stateWithDown = state.copy(walls = setOf(wallDown))
        val attemptWallLeft = MazeEngine.placeWall(stateWithDown, wallLeft)

        assertTrue(attemptWallLeft is WallPlacementResult.Invalid)
        assertEquals(MazeEngine.PATH_MUST_REMAIN_OPEN, (attemptWallLeft as WallPlacementResult.Invalid).reason)
    }

    @Test
    fun testPlayer1ReachingTopRowWinsImmediately() {
        var state = MazeEngine.createInitialState()
        // Put player 1 path reaching row 1
        val customBoard = state.board.mapIndexed { r, row ->
            row.mapIndexed { c, cell ->
                if (r == 1 && c == 0) CellOwner.PLAYER_ONE else cell
            }
        }
        val legalCells = setOf(GridPosition(0, 0))
        state = state.copy(
            board = customBoard,
            legalPathCells = legalCells,
            currentPlayer = Player.PLAYER_ONE
        )

        val result = MazeEngine.claimPathCell(state, GridPosition(0, 0))
        assertTrue(result is PathClaimResult.Success)
        val finalState = (result as PathClaimResult.Success).newState

        assertEquals(Player.PLAYER_ONE, finalState.winner)
        assertEquals(WinReason.GOAL_REACHED, finalState.winReason)
        assertTrue(finalState.isGameOver)
    }

    @Test
    fun testPlayer2ReachingBottomRowWinsImmediately() {
        var state = MazeEngine.createInitialState()
        val customBoard = state.board.mapIndexed { r, row ->
            row.mapIndexed { c, cell ->
                if (r == 5 && c == 6) CellOwner.PLAYER_TWO else cell
            }
        }
        val legalCells = setOf(GridPosition(6, 6))
        state = state.copy(
            board = customBoard,
            legalPathCells = legalCells,
            currentPlayer = Player.PLAYER_TWO
        )

        val result = MazeEngine.claimPathCell(state, GridPosition(6, 6))
        assertTrue(result is PathClaimResult.Success)
        val finalState = (result as PathClaimResult.Success).newState

        assertEquals(Player.PLAYER_TWO, finalState.winner)
        assertEquals(WinReason.GOAL_REACHED, finalState.winReason)
        assertTrue(finalState.isGameOver)
    }

    @Test
    fun testFallbackProgressComparison() {
        // Neither can expand and both have 0 walls
        // Suppose P1 reached row 2, and P2 reached row 3
        // P1 distance to row 0: 2 - 0 = 2
        // P2 distance to row 6: 6 - 3 = 3
        // P1 closer (2 < 3), so P1 should win FURTHEST_PROGRESS
        val customBoard = List(7) { r ->
            List(7) { c ->
                when {
                    r == 2 && c == 0 -> CellOwner.PLAYER_ONE
                    r == 3 && c == 6 -> CellOwner.PLAYER_TWO
                    else -> CellOwner.EMPTY
                }
            }
        }

        // Walls blocking all expansion around P1 at (2,0) and P2 at (3,6)
        val blockingWalls = setOf(
            Wall(1, 0, WallOrientation.HORIZONTAL),
            Wall(2, 0, WallOrientation.HORIZONTAL),
            Wall(2, 0, WallOrientation.VERTICAL),
            Wall(2, 6, WallOrientation.HORIZONTAL),
            Wall(3, 6, WallOrientation.HORIZONTAL),
            Wall(3, 5, WallOrientation.VERTICAL)
        )

        val state = MazeBuilderState(
            board = customBoard,
            walls = blockingWalls,
            player1WallsRemaining = 0,
            player2WallsRemaining = 0,
            legalPathCells = emptySet()
        )

        val resolved = MazeEngine.checkFallbackTiebreaker(state)
        assertEquals(Player.PLAYER_ONE, resolved.winner)
        assertEquals(WinReason.FURTHEST_PROGRESS, resolved.winReason)
    }

    @Test
    fun testUndoRestoresPreviousState() {
        val initialState = MazeEngine.createInitialState()
        val history = java.util.ArrayDeque<MazeBuilderState>()
        history.push(initialState)

        // Make move
        val claimResult = MazeEngine.claimPathCell(initialState, GridPosition(5, 0)) as PathClaimResult.Success
        val stateAfterMove1 = claimResult.newState
        assertEquals(1, stateAfterMove1.moveCount)
        assertEquals(Player.PLAYER_TWO, stateAfterMove1.currentPlayer)

        // Undo move
        val restoredState = history.pop()
        assertEquals(initialState.moveCount, restoredState.moveCount)
        assertEquals(Player.PLAYER_ONE, restoredState.currentPlayer)
        assertEquals(CellOwner.EMPTY, restoredState.board[5][0])
    }
}
