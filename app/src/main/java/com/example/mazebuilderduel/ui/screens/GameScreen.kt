package com.example.mazebuilderduel.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mazebuilderduel.model.Player
import com.example.mazebuilderduel.ui.components.ActionModeSelector
import com.example.mazebuilderduel.ui.components.BoardCanvas
import com.example.mazebuilderduel.ui.components.PlayerPanel
import com.example.mazebuilderduel.ui.components.RestartConfirmationDialog
import com.example.mazebuilderduel.ui.components.TurnIndicator
import com.example.mazebuilderduel.ui.components.WarningBanner
import com.example.mazebuilderduel.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNavigateToResult: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val warningMessage by viewModel.warningMessage.collectAsState()

    var showRestartDialog by remember { mutableStateOf(false) }

    // Navigate to result screen when match ends
    LaunchedEffect(gameState.isGameOver) {
        if (gameState.isGameOver) {
            onNavigateToResult()
        }
    }

    if (showRestartDialog) {
        RestartConfirmationDialog(
            onConfirm = {
                viewModel.restartGame()
                showRestartDialog = false
            },
            onDismiss = { showRestartDialog = false }
        )
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MAZE BUILDER DUEL",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Return Home"
                        )
                    }
                },
                actions = {
                    // Undo Button
                    IconButton(
                        onClick = { viewModel.undoMove() },
                        enabled = viewModel.canUndo
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo Move",
                            tint = if (viewModel.canUndo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }

                    // Restart Button
                    IconButton(onClick = { showRestartDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restart Match"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLandscape) {
                // Landscape / Tablet Split Layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Board
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        BoardCanvas(
                            state = gameState,
                            settings = settings,
                            onCellClick = { viewModel.claimPathCell(it) },
                            onWallClick = { viewModel.placeWall(it) }
                        )
                    }

                    // Right: Panels and Controls
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PlayerPanel(
                            player = Player.PLAYER_TWO,
                            isCurrentTurn = gameState.currentPlayer == Player.PLAYER_TWO && !gameState.isGameOver,
                            cellsClaimed = gameState.player2CellCount,
                            wallsRemaining = gameState.player2WallsRemaining,
                            distanceToGoal = gameState.player2DistanceToGoal
                        )

                        TurnIndicator(
                            currentPlayer = gameState.currentPlayer,
                            actionMode = gameState.actionMode,
                            moveCount = gameState.moveCount
                        )

                        PlayerPanel(
                            player = Player.PLAYER_ONE,
                            isCurrentTurn = gameState.currentPlayer == Player.PLAYER_ONE && !gameState.isGameOver,
                            cellsClaimed = gameState.player1CellCount,
                            wallsRemaining = gameState.player1WallsRemaining,
                            distanceToGoal = gameState.player1DistanceToGoal
                        )

                        ActionModeSelector(
                            currentMode = gameState.actionMode,
                            currentPlayer = gameState.currentPlayer,
                            wallsRemaining = gameState.currentWallsRemaining,
                            selectedOrientation = gameState.selectedWallOrientation,
                            onModeSelected = { viewModel.setActionMode(it) },
                            onOrientationSelected = { viewModel.setWallOrientation(it) }
                        )
                    }
                }
            } else {
                // Portrait Layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top: Player 2 Panel (Goal: Bottom Row)
                    PlayerPanel(
                        player = Player.PLAYER_TWO,
                        isCurrentTurn = gameState.currentPlayer == Player.PLAYER_TWO && !gameState.isGameOver,
                        cellsClaimed = gameState.player2CellCount,
                        wallsRemaining = gameState.player2WallsRemaining,
                        distanceToGoal = gameState.player2DistanceToGoal
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Turn Header Indicator
                    TurnIndicator(
                        currentPlayer = gameState.currentPlayer,
                        actionMode = gameState.actionMode,
                        moveCount = gameState.moveCount
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Center: 7x7 Board Canvas
                    BoardCanvas(
                        state = gameState,
                        settings = settings,
                        onCellClick = { viewModel.claimPathCell(it) },
                        onWallClick = { viewModel.placeWall(it) }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Bottom: Player 1 Panel (Goal: Top Row)
                    PlayerPanel(
                        player = Player.PLAYER_ONE,
                        isCurrentTurn = gameState.currentPlayer == Player.PLAYER_ONE && !gameState.isGameOver,
                        cellsClaimed = gameState.player1CellCount,
                        wallsRemaining = gameState.player1WallsRemaining,
                        distanceToGoal = gameState.player1DistanceToGoal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action Mode & Wall Orientation Controls
                    ActionModeSelector(
                        currentMode = gameState.actionMode,
                        currentPlayer = gameState.currentPlayer,
                        wallsRemaining = gameState.currentWallsRemaining,
                        selectedOrientation = gameState.selectedWallOrientation,
                        onModeSelected = { viewModel.setActionMode(it) },
                        onOrientationSelected = { viewModel.setWallOrientation(it) }
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            // Floating Warning Banner
            WarningBanner(
                message = warningMessage,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            )
        }
    }
}
