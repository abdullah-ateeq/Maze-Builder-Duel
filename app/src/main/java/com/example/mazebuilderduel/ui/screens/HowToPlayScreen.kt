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
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mazebuilderduel.ui.theme.Player1CopperBright
import com.example.mazebuilderduel.ui.theme.Player2TealBright
import com.example.mazebuilderduel.ui.theme.TextMuted
import com.example.mazebuilderduel.ui.theme.WallBronze

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowToPlayScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "HOW TO PLAY",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Objective
            RuleCard(
                title = "CORE OBJECTIVE",
                icon = Icons.Default.EmojiEvents,
                iconTint = WallBronze,
                description = "Two players race across a 7×7 grid. Player 1 starts at the bottom-left and must reach any cell on the top row. Player 2 starts at the top-right and must reach any cell on the bottom row."
            )

            // Turn Actions
            RuleCard(
                title = "TWO ACTIONS PER TURN",
                icon = Icons.AutoMirrored.Filled.AltRoute,
                iconTint = Player1CopperBright,
                description = "On each turn, choose exactly ONE action:\n\n• BUILD PATH: Claim one empty cell orthogonally adjacent to your existing route.\n• PLACE WALL: Erect a barrier edge to redirect or delay your opponent."
            )

            // Path Rules
            RuleCard(
                title = "EXPANDING YOUR ROUTE",
                icon = Icons.AutoMirrored.Filled.AltRoute,
                iconTint = Player2TealBright,
                description = "• Paths expand only Up, Down, Left, or Right (no diagonal expansion).\n• Claimed cells remain permanently yours — opponent territory cannot be crossed or stolen.\n• Walls block movement between adjacent cells."
            )

            // The Golden Rule (Wall Validation)
            RuleCard(
                title = "THE GOLDEN BARRIER RULE",
                icon = Icons.Default.Shield,
                iconTint = WallBronze,
                description = "Each player starts with 5 walls. Walls redirect and force detours, but NO wall placement may completely trap a player or eliminate every possible path to their goal. The game validates this via BFS before accepting a wall."
            )

            // Fallback Tiebreaker
            RuleCard(
                title = "FALLBACK PROGRESS TIEBREAKER",
                icon = Icons.Default.Lock,
                iconTint = TextMuted,
                description = "In the rare edge case where both players have 0 walls remaining and neither player has any legal path moves left, the player whose network is closest to their goal row wins."
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun RuleCard(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconTint.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = TextMuted
            )
        }
    }
}
