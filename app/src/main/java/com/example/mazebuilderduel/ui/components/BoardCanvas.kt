package com.example.mazebuilderduel.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.mazebuilderduel.logic.BoardConstants
import com.example.mazebuilderduel.logic.PathFinder
import com.example.mazebuilderduel.model.ActionMode
import com.example.mazebuilderduel.model.BoardTheme
import com.example.mazebuilderduel.model.CellOwner
import com.example.mazebuilderduel.model.GridPosition
import com.example.mazebuilderduel.model.MazeBuilderState
import com.example.mazebuilderduel.model.Player
import com.example.mazebuilderduel.model.SettingsState
import com.example.mazebuilderduel.model.Wall
import com.example.mazebuilderduel.model.WallOrientation
import com.example.mazebuilderduel.ui.theme.AccentGold
import com.example.mazebuilderduel.ui.theme.LastMoveRing
import com.example.mazebuilderduel.ui.theme.Player1Copper
import com.example.mazebuilderduel.ui.theme.Player1CopperBright
import com.example.mazebuilderduel.ui.theme.Player1CopperGlow
import com.example.mazebuilderduel.ui.theme.Player2Teal
import com.example.mazebuilderduel.ui.theme.Player2TealBright
import com.example.mazebuilderduel.ui.theme.Player2TealGlow
import com.example.mazebuilderduel.ui.theme.WallBronze
import com.example.mazebuilderduel.ui.theme.WallIvory
import kotlin.math.abs

@Composable
fun BoardCanvas(
    state: MazeBuilderState,
    settings: SettingsState,
    onCellClick: (GridPosition) -> Unit,
    onWallClick: (Wall) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentGameState by rememberUpdatedState(state)
    val currentOnCellClick by rememberUpdatedState(onCellClick)
    val currentOnWallClick by rememberUpdatedState(onWallClick)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val lastActionPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lastActionPulse"
    )

    val boardBg = if (settings.boardTheme == BoardTheme.CLASSIC) {
        Color(0xFF131720)
    } else {
        Color(0xFF0C1017)
    }

    val cellBgColor = if (settings.boardTheme == BoardTheme.CLASSIC) {
        Color(0xFF1A212E)
    } else {
        Color(0xFF151B24)
    }

    val cellBorderColor = if (settings.boardTheme == BoardTheme.CLASSIC) {
        Color(0xFF263145)
    } else {
        Color(0xFF1F2937)
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .semantics {
                contentDescription = "7 by 7 Maze Board. Current turn: ${state.currentPlayer.displayName}."
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val currentState = currentGameState
                    if (currentState.isGameOver) return@detectTapGestures

                    val cellSize = size.width / BoardConstants.BOARD_SIZE.toFloat()

                    if (currentState.actionMode == ActionMode.PATH) {
                        val col = (offset.x / cellSize).toInt().coerceIn(0, BoardConstants.BOARD_SIZE - 1)
                        val row = (offset.y / cellSize).toInt().coerceIn(0, BoardConstants.BOARD_SIZE - 1)
                        val tappedPos = GridPosition(row, col)
                        if (tappedPos in currentState.legalPathCells) {
                            currentOnCellClick(tappedPos)
                        }
                    } else if (currentState.actionMode == ActionMode.WALL) {
                        // Detect wall tap
                        if (currentState.selectedWallOrientation == WallOrientation.HORIZONTAL) {
                            // Nearest horizontal grid line (row from 0 to 5)
                            var bestRow = -1
                            var bestCol = -1
                            var minDistance = Float.MAX_VALUE

                            val tapCol = (offset.x / cellSize).toInt().coerceIn(0, BoardConstants.BOARD_SIZE - 1)

                            for (r in 0 until (BoardConstants.BOARD_SIZE - 1)) {
                                val lineY = (r + 1) * cellSize
                                val dist = abs(offset.y - lineY)
                                if (dist < minDistance && dist < cellSize * 0.5f) {
                                    minDistance = dist
                                    bestRow = r
                                    bestCol = tapCol
                                }
                            }

                            if (bestRow in 0..5 && bestCol in 0..6) {
                                currentOnWallClick(Wall(bestRow, bestCol, WallOrientation.HORIZONTAL))
                            }
                        } else {
                            // Nearest vertical grid line (col from 0 to 5)
                            var bestRow = -1
                            var bestCol = -1
                            var minDistance = Float.MAX_VALUE

                            val tapRow = (offset.y / cellSize).toInt().coerceIn(0, BoardConstants.BOARD_SIZE - 1)

                            for (c in 0 until (BoardConstants.BOARD_SIZE - 1)) {
                                val lineX = (c + 1) * cellSize
                                val dist = abs(offset.x - lineX)
                                if (dist < minDistance && dist < cellSize * 0.5f) {
                                    minDistance = dist
                                    bestRow = tapRow
                                    bestCol = c
                                }
                            }

                            if (bestRow in 0..6 && bestCol in 0..5) {
                                currentOnWallClick(Wall(bestRow, bestCol, WallOrientation.VERTICAL))
                            }
                        }
                    }
                }
            }
    ) {
        val boardSize = BoardConstants.BOARD_SIZE
        val cellSize = size.width / boardSize.toFloat()
        val cornerRadius = CornerRadius(cellSize * 0.16f, cellSize * 0.16f)

        // 1. Board Background
        drawRoundRect(
            color = boardBg,
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = CornerRadius(cellSize * 0.22f, cellSize * 0.22f)
        )

        // 2. Base Grid Cells
        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                val padding = cellSize * 0.08f
                val cellRect = Rect(
                    left = c * cellSize + padding,
                    top = r * cellSize + padding,
                    right = (c + 1) * cellSize - padding,
                    bottom = (r + 1) * cellSize - padding
                )

                drawRoundRect(
                    color = cellBgColor,
                    topLeft = cellRect.topLeft,
                    size = cellRect.size,
                    cornerRadius = cornerRadius
                )

                drawRoundRect(
                    color = cellBorderColor,
                    topLeft = cellRect.topLeft,
                    size = cellRect.size,
                    cornerRadius = cornerRadius,
                    style = Stroke(width = cellSize * 0.025f)
                )
            }
        }

        // 3. Connected Path Visuals (bridges between adjacent owned cells)
        val bridgeWidth = cellSize * 0.44f
        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                val owner = state.board[r][c]
                if (owner != CellOwner.EMPTY) {
                    val color = if (owner == CellOwner.PLAYER_ONE) Player1Copper else Player2Teal

                    // Check horizontal connection to right
                    if (c < boardSize - 1 && state.board[r][c + 1] == owner) {
                        val posFrom = GridPosition(r, c)
                        val posTo = GridPosition(r, c + 1)
                        if (!PathFinder.isEdgeBlocked(posFrom, posTo, state.walls)) {
                            val centerX1 = (c + 0.5f) * cellSize
                            val centerX2 = (c + 1.5f) * cellSize
                            val centerY = (r + 0.5f) * cellSize
                            drawRect(
                                color = color,
                                topLeft = Offset(centerX1, centerY - bridgeWidth / 2f),
                                size = Size(centerX2 - centerX1, bridgeWidth)
                            )
                        }
                    }

                    // Check vertical connection downwards
                    if (r < boardSize - 1 && state.board[r + 1][c] == owner) {
                        val posFrom = GridPosition(r, c)
                        val posTo = GridPosition(r + 1, c)
                        if (!PathFinder.isEdgeBlocked(posFrom, posTo, state.walls)) {
                            val centerX = (c + 0.5f) * cellSize
                            val centerY1 = (r + 0.5f) * cellSize
                            val centerY2 = (r + 1.5f) * cellSize
                            drawRect(
                                color = color,
                                topLeft = Offset(centerX - bridgeWidth / 2f, centerY1),
                                size = Size(bridgeWidth, centerY2 - centerY1)
                            )
                        }
                    }
                }
            }
        }

        // 4. Player-Owned Path Cells (Filled with tactical motifs)
        for (r in 0 until boardSize) {
            for (c in 0 until boardSize) {
                val owner = state.board[r][c]
                if (owner != CellOwner.EMPTY) {
                    val padding = cellSize * 0.10f
                    val cellRect = Rect(
                        left = c * cellSize + padding,
                        top = r * cellSize + padding,
                        right = (c + 1) * cellSize - padding,
                        bottom = (r + 1) * cellSize - padding
                    )

                    val mainColor = if (owner == CellOwner.PLAYER_ONE) Player1Copper else Player2Teal
                    val brightColor = if (owner == CellOwner.PLAYER_ONE) Player1CopperBright else Player2TealBright

                    drawRoundRect(
                        color = mainColor,
                        topLeft = cellRect.topLeft,
                        size = cellRect.size,
                        cornerRadius = cornerRadius
                    )

                    // Inner tactical outline
                    drawRoundRect(
                        color = brightColor.copy(alpha = 0.6f),
                        topLeft = cellRect.topLeft,
                        size = cellRect.size,
                        cornerRadius = cornerRadius,
                        style = Stroke(width = cellSize * 0.035f)
                    )

                    // Motifs for accessibility:
                    // Player 1: Diamond motif
                    // Player 2: Circle motif
                    val center = Offset((c + 0.5f) * cellSize, (r + 0.5f) * cellSize)
                    val motifSize = cellSize * 0.22f

                    if (owner == CellOwner.PLAYER_ONE) {
                        drawDiamondMotif(center, motifSize, WallIvory.copy(alpha = 0.85f))
                    } else {
                        drawCircleMotif(center, motifSize, WallIvory.copy(alpha = 0.85f))
                    }
                }
            }
        }

        // 5. Placed Walls
        val wallThickness = cellSize * 0.16f
        val wallShadowOffset = cellSize * 0.02f
        for (wall in state.walls) {
            if (wall.orientation == WallOrientation.HORIZONTAL) {
                val y = (wall.row + 1) * cellSize
                val startX = wall.col * cellSize + cellSize * 0.04f
                val wallLength = cellSize * 0.92f

                // Drop shadow
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.6f),
                    topLeft = Offset(startX, y - wallThickness / 2f + wallShadowOffset),
                    size = Size(wallLength, wallThickness),
                    cornerRadius = CornerRadius(wallThickness / 2f, wallThickness / 2f)
                )

                // Base Bronze Wall
                drawRoundRect(
                    color = WallBronze,
                    topLeft = Offset(startX, y - wallThickness / 2f),
                    size = Size(wallLength, wallThickness),
                    cornerRadius = CornerRadius(wallThickness / 2f, wallThickness / 2f)
                )

                // Ivory core accent
                drawRoundRect(
                    color = WallIvory.copy(alpha = 0.8f),
                    topLeft = Offset(startX + wallLength * 0.15f, y - wallThickness * 0.18f),
                    size = Size(wallLength * 0.70f, wallThickness * 0.36f),
                    cornerRadius = CornerRadius(wallThickness * 0.18f, wallThickness * 0.18f)
                )
            } else {
                val x = (wall.col + 1) * cellSize
                val startY = wall.row * cellSize + cellSize * 0.04f
                val wallLength = cellSize * 0.92f

                // Drop shadow
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.6f),
                    topLeft = Offset(x - wallThickness / 2f + wallShadowOffset, startY),
                    size = Size(wallThickness, wallLength),
                    cornerRadius = CornerRadius(wallThickness / 2f, wallThickness / 2f)
                )

                // Base Bronze Wall
                drawRoundRect(
                    color = WallBronze,
                    topLeft = Offset(x - wallThickness / 2f, startY),
                    size = Size(wallThickness, wallLength),
                    cornerRadius = CornerRadius(wallThickness / 2f, wallThickness / 2f)
                )

                // Ivory core accent
                drawRoundRect(
                    color = WallIvory.copy(alpha = 0.8f),
                    topLeft = Offset(x - wallThickness * 0.18f, startY + wallLength * 0.15f),
                    size = Size(wallThickness * 0.36f, wallLength * 0.70f),
                    cornerRadius = CornerRadius(wallThickness * 0.18f, wallThickness * 0.18f)
                )
            }
        }

        // 6. Wall Placement Guide Slots (in WALL mode)
        if (state.actionMode == ActionMode.WALL && !state.isGameOver && state.currentWallsRemaining > 0) {
            val guideThickness = cellSize * 0.07f
            if (state.selectedWallOrientation == WallOrientation.HORIZONTAL) {
                for (r in 0 until (boardSize - 1)) {
                    val y = (r + 1) * cellSize
                    for (c in 0 until boardSize) {
                        val potentialWall = Wall(r, c, WallOrientation.HORIZONTAL)
                        if (potentialWall !in state.walls) {
                            val startX = c * cellSize + cellSize * 0.1f
                            val length = cellSize * 0.8f

                            drawRoundRect(
                                color = WallBronze.copy(alpha = 0.35f * pulseAlpha),
                                topLeft = Offset(startX, y - guideThickness / 2f),
                                size = Size(length, guideThickness),
                                cornerRadius = CornerRadius(guideThickness / 2f, guideThickness / 2f)
                            )
                        }
                    }
                }
            } else {
                for (r in 0 until boardSize) {
                    for (c in 0 until (boardSize - 1)) {
                        val potentialWall = Wall(r, c, WallOrientation.VERTICAL)
                        if (potentialWall !in state.walls) {
                            val x = (c + 1) * cellSize
                            val startY = r * cellSize + cellSize * 0.1f
                            val length = cellSize * 0.8f

                            drawRoundRect(
                                color = WallBronze.copy(alpha = 0.35f * pulseAlpha),
                                topLeft = Offset(x - guideThickness / 2f, startY),
                                size = Size(guideThickness, length),
                                cornerRadius = CornerRadius(guideThickness / 2f, guideThickness / 2f)
                            )
                        }
                    }
                }
            }
        }

        // 7. Legal Move Highlights (in PATH mode)
        if (settings.showLegalMoves && state.actionMode == ActionMode.PATH && !state.isGameOver) {
            val highlightColor = if (state.currentPlayer == Player.PLAYER_ONE) {
                Player1CopperBright
            } else {
                Player2TealBright
            }

            for (pos in state.legalPathCells) {
                val center = Offset((pos.col + 0.5f) * cellSize, (pos.row + 0.5f) * cellSize)
                val radius = cellSize * 0.22f

                // Outer pulsing halo
                drawCircle(
                    color = highlightColor.copy(alpha = 0.22f * pulseAlpha),
                    radius = radius * 1.35f,
                    center = center
                )

                // Inner ring
                drawCircle(
                    color = highlightColor.copy(alpha = 0.85f * pulseAlpha),
                    radius = radius,
                    center = center,
                    style = Stroke(width = cellSize * 0.045f)
                )

                // Center core dot
                drawCircle(
                    color = highlightColor.copy(alpha = 0.95f),
                    radius = cellSize * 0.06f,
                    center = center
                )
            }
        }

        // 8. Goal Edge Accents
        // Top edge: Player 1 goal (Copper rail)
        val railThickness = cellSize * 0.065f
        drawRoundRect(
            color = Player1CopperBright,
            topLeft = Offset(cellSize * 0.08f, cellSize * 0.015f),
            size = Size(size.width - cellSize * 0.16f, railThickness),
            cornerRadius = CornerRadius(railThickness / 2f, railThickness / 2f)
        )

        // Bottom edge: Player 2 goal (Teal rail)
        drawRoundRect(
            color = Player2TealBright,
            topLeft = Offset(cellSize * 0.08f, size.height - railThickness - cellSize * 0.015f),
            size = Size(size.width - cellSize * 0.16f, railThickness),
            cornerRadius = CornerRadius(railThickness / 2f, railThickness / 2f)
        )

        // 9. Last Action Indicator
        if (settings.showLastAction) {
            state.lastPathCell?.let { lastCell ->
                val center = Offset((lastCell.col + 0.5f) * cellSize, (lastCell.row + 0.5f) * cellSize)
                val padding = cellSize * 0.06f
                val rect = Rect(
                    left = lastCell.col * cellSize + padding,
                    top = lastCell.row * cellSize + padding,
                    right = (lastCell.col + 1) * cellSize - padding,
                    bottom = (lastCell.row + 1) * cellSize - padding
                )

                drawRoundRect(
                    color = LastMoveRing.copy(alpha = 0.85f * lastActionPulse),
                    topLeft = rect.topLeft,
                    size = rect.size,
                    cornerRadius = CornerRadius(cellSize * 0.18f, cellSize * 0.18f),
                    style = Stroke(width = cellSize * 0.045f)
                )
            }

            state.lastWall?.let { lastWall ->
                val wallPulseThickness = wallThickness * 1.25f
                if (lastWall.orientation == WallOrientation.HORIZONTAL) {
                    val y = (lastWall.row + 1) * cellSize
                    val startX = lastWall.col * cellSize + cellSize * 0.04f
                    val length = cellSize * 0.92f

                    drawRoundRect(
                        color = LastMoveRing.copy(alpha = 0.6f * lastActionPulse),
                        topLeft = Offset(startX, y - wallPulseThickness / 2f),
                        size = Size(length, wallPulseThickness),
                        cornerRadius = CornerRadius(wallPulseThickness / 2f, wallPulseThickness / 2f),
                        style = Stroke(width = cellSize * 0.025f)
                    )
                } else {
                    val x = (lastWall.col + 1) * cellSize
                    val startY = lastWall.row * cellSize + cellSize * 0.04f
                    val length = cellSize * 0.92f

                    drawRoundRect(
                        color = LastMoveRing.copy(alpha = 0.6f * lastActionPulse),
                        topLeft = Offset(x - wallPulseThickness / 2f, startY),
                        size = Size(wallPulseThickness, length),
                        cornerRadius = CornerRadius(wallPulseThickness / 2f, wallPulseThickness / 2f),
                        style = Stroke(width = cellSize * 0.025f)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawDiamondMotif(center: Offset, size: Float, color: Color) {
    val path = Path().apply {
        moveTo(center.x, center.y - size)
        lineTo(center.x + size, center.y)
        lineTo(center.x, center.y + size)
        lineTo(center.x - size, center.y)
        close()
    }
    drawPath(path = path, color = color, style = Stroke(width = size * 0.28f))
}

private fun DrawScope.drawCircleMotif(center: Offset, size: Float, color: Color) {
    drawCircle(
        color = color,
        radius = size * 0.85f,
        center = center,
        style = Stroke(width = size * 0.28f)
    )
}
