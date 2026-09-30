package com.example.mazebuilderduel.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mazebuilderduel.model.ActionMode
import com.example.mazebuilderduel.model.Player
import com.example.mazebuilderduel.ui.theme.Player1CopperBright
import com.example.mazebuilderduel.ui.theme.Player2TealBright
import com.example.mazebuilderduel.ui.theme.TextMuted

@Composable
fun TurnIndicator(
    currentPlayer: Player,
    actionMode: ActionMode,
    moveCount: Int,
    modifier: Modifier = Modifier
) {
    val turnColor = if (currentPlayer == Player.PLAYER_ONE) Player1CopperBright else Player2TealBright

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = currentPlayer,
            transitionSpec = {
                slideInVertically { height -> height } togetherWith slideOutVertically { height -> -height }
            },
            label = "turnChange"
        ) { player ->
            Text(
                text = "${player.displayName.uppercase()}'S TURN",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                ),
                color = turnColor
            )
        }

        Text(
            text = when (actionMode) {
                ActionMode.PATH -> "Select an adjacent empty cell to expand path"
                ActionMode.WALL -> "Tap an edge slot to position a tactical barrier"
            },
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
    }
}
