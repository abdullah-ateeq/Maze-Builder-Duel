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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mazebuilderduel.model.GameStatistics
import com.example.mazebuilderduel.ui.theme.Player1Copper
import com.example.mazebuilderduel.ui.theme.Player1CopperBright
import com.example.mazebuilderduel.ui.theme.Player2Teal
import com.example.mazebuilderduel.ui.theme.Player2TealBright
import com.example.mazebuilderduel.ui.theme.TextMuted
import com.example.mazebuilderduel.ui.theme.WallBronze
import com.example.mazebuilderduel.ui.theme.WallIvory

@Composable
fun HomeScreen(
    statistics: GameStatistics,
    onPlayClick: () -> Unit,
    onHowToPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val scrollState = rememberScrollState()

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
            Spacer(modifier = Modifier.height(16.dp))

            // Game Emblem / Header
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(2.dp, WallBronze.copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Player1CopperBright)
                    )
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(24.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(WallBronze)
                    )
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Player2TealBright)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MAZE BUILDER DUEL",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Build your route. Block theirs.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = 0.5.sp
                ),
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Quick Stats Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CAREER STATS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(label = "PLAYED", value = "${statistics.gamesPlayed}")
                        StatItem(label = "P1 WINS", value = "${statistics.player1Wins}", color = Player1CopperBright)
                        StatItem(label = "P2 WINS", value = "${statistics.player2Wins}", color = Player2TealBright)
                        StatItem(label = "DRAWS", value = "${statistics.draws}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Primary PLAY Button
            Button(
                onClick = onPlayClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WallBronze)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PLAY LOCAL 2-PLAYER",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Secondary Buttons
            MenuSecondaryButton(
                text = "HOW TO PLAY",
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                onClick = onHowToPlayClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            MenuSecondaryButton(
                text = "SETTINGS",
                icon = Icons.Default.Settings,
                onClick = onSettingsClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            MenuSecondaryButton(
                text = "ABOUT",
                icon = Icons.Default.Info,
                onClick = onAboutClick
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMuted
        )
    }
}

@Composable
private fun MenuSecondaryButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
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
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
