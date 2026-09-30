package com.example.mazebuilderduel.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Square
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mazebuilderduel.model.Player
import com.example.mazebuilderduel.ui.theme.Player1Copper
import com.example.mazebuilderduel.ui.theme.Player1CopperBright
import com.example.mazebuilderduel.ui.theme.Player2Teal
import com.example.mazebuilderduel.ui.theme.Player2TealBright
import com.example.mazebuilderduel.ui.theme.TextMuted
import com.example.mazebuilderduel.ui.theme.WallBronze

@Composable
fun PlayerPanel(
    player: Player,
    isCurrentTurn: Boolean,
    cellsClaimed: Int,
    wallsRemaining: Int,
    distanceToGoal: Int?,
    modifier: Modifier = Modifier
) {
    val isP1 = player == Player.PLAYER_ONE
    val primaryColor = if (isP1) Player1Copper else Player2Teal
    val accentColor = if (isP1) Player1CopperBright else Player2TealBright

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isCurrentTurn) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
        label = "borderColor"
    )

    val animatedBgColor by animateColorAsState(
        targetValue = if (isCurrentTurn) primaryColor.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface,
        label = "bgColor"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isCurrentTurn) 2.dp else 1.dp,
                color = animatedBorderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .semantics {
                contentDescription = "${player.displayName}, Cells: $cellsClaimed, Walls: $wallsRemaining of 5." +
                        if (isCurrentTurn) " Current active turn." else ""
            },
        color = animatedBgColor,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Player identity & motif
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(primaryColor.copy(alpha = 0.25f))
                        .border(1.5.dp, primaryColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isP1) {
                        // Diamond Motif (rotated square)
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .graphicsLayer { rotationZ = 45f }
                                .background(accentColor)
                        )
                    } else {
                        // Circle Motif
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = player.displayName.uppercase(),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = if (isCurrentTurn) accentColor else MaterialTheme.colorScheme.onSurface
                        )
                        if (isCurrentTurn) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(accentColor)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "TURN",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    Text(
                        text = if (isP1) "Target: Top Rail (Row 0)" else "Target: Bottom Rail (Row 6)",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            // Right: Cells, Walls, and Distance Metrics
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Cells
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "CELLS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted
                    )
                    Text(
                        text = "$cellsClaimed",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Walls
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "WALLS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        for (i in 1..5) {
                            val isAvailable = i <= wallsRemaining
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (isAvailable) WallBronze else WallBronze.copy(alpha = 0.2f)
                                    )
                            )
                        }
                    }
                }

                // Distance to goal (Shortest theoretical path)
                distanceToGoal?.let { dist ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "DIST",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextMuted
                        )
                        Text(
                            text = "$dist",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }
            }
        }
    }
}
