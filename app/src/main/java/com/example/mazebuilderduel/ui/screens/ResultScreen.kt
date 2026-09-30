package com.example.mazebuilderduel.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mazebuilderduel.model.MazeBuilderState
import com.example.mazebuilderduel.model.Player
import com.example.mazebuilderduel.model.WinReason
import com.example.mazebuilderduel.ui.theme.Player1CopperBright
import com.example.mazebuilderduel.ui.theme.Player2TealBright
import com.example.mazebuilderduel.ui.theme.TextMuted
import com.example.mazebuilderduel.ui.theme.WallBronze
import com.example.mazebuilderduel.ui.theme.WallIvory

@Composable
fun ResultScreen(
    state: MazeBuilderState,
    onPlayAgainClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    val winner = state.winner
    val isDraw = state.isDraw

    val headlineText = when {
        isDraw -> "MATCH DRAW"
        winner == Player.PLAYER_ONE -> "PLAYER 1 WINS"
        winner == Player.PLAYER_TWO -> "PLAYER 2 WINS"
        else -> "MATCH COMPLETE"
    }

    val winnerColor = when {
        isDraw -> WallBronze
        winner == Player.PLAYER_ONE -> Player1CopperBright
        winner == Player.PLAYER_TWO -> Player2TealBright
        else -> WallBronze
    }

    val reasonText = when (state.winReason) {
        WinReason.GOAL_REACHED -> "GOAL REACHED"
        WinReason.FURTHEST_PROGRESS -> "FURTHEST PROGRESS"
        null -> if (isDraw) "EQUAL DISTANCE" else "VICTORY"
    }

    val p1WallsUsed = 5 - state.player1WallsRemaining
    val p2WallsUsed = 5 - state.player2WallsRemaining

    val minutes = state.matchDurationSeconds / 60
    val seconds = state.matchDurationSeconds % 60
    val formattedDuration = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Trophy / Crown Icon
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(winnerColor.copy(alpha = 0.2f))
                    .border(2.dp, winnerColor, RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = winnerColor,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = headlineText,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                ),
                color = winnerColor
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Reason: $reasonText",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Match Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "MATCH METRICS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TextMuted
                    )

                    MetricRow(
                        label = "Turns Played",
                        value = "${state.moveCount}"
                    )

                    MetricRow(
                        label = "Match Duration",
                        value = formattedDuration
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Player Comparison
                    MetricRow(
                        label = "P1 Cells Claimed",
                        value = "${state.player1CellCount}",
                        valueColor = Player1CopperBright
                    )

                    MetricRow(
                        label = "P2 Cells Claimed",
                        value = "${state.player2CellCount}",
                        valueColor = Player2TealBright
                    )

                    MetricRow(
                        label = "P1 Walls Deployed",
                        value = "$p1WallsUsed / 5",
                        valueColor = Player1CopperBright
                    )

                    MetricRow(
                        label = "P2 Walls Deployed",
                        value = "$p2WallsUsed / 5",
                        valueColor = Player2TealBright
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Play Again Button
            Button(
                onClick = onPlayAgainClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WallBronze)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PLAY AGAIN",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Home Button
            OutlinedButton(
                onClick = onHomeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HOME",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = TextMuted
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
