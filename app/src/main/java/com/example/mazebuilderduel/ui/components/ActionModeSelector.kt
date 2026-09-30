package com.example.mazebuilderduel.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.filled.CropLandscape
import androidx.compose.material.icons.filled.CropPortrait
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mazebuilderduel.model.ActionMode
import com.example.mazebuilderduel.model.Player
import com.example.mazebuilderduel.model.WallOrientation
import com.example.mazebuilderduel.ui.theme.Player1Copper
import com.example.mazebuilderduel.ui.theme.Player2Teal
import com.example.mazebuilderduel.ui.theme.TextMuted
import com.example.mazebuilderduel.ui.theme.WallBronze

@Composable
fun ActionModeSelector(
    currentMode: ActionMode,
    currentPlayer: Player,
    wallsRemaining: Int,
    selectedOrientation: WallOrientation,
    onModeSelected: (ActionMode) -> Unit,
    onOrientationSelected: (WallOrientation) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = if (currentPlayer == Player.PLAYER_ONE) Player1Copper else Player2Teal

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Segmented Button: PATH vs WALL
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // PATH Mode Button
            val isPathSelected = currentMode == ActionMode.PATH
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isPathSelected) activeColor else Color.Transparent
                    )
                    .clickable { onModeSelected(ActionMode.PATH) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.AltRoute,
                        contentDescription = null,
                        tint = if (isPathSelected) Color.White else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BUILD PATH",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPathSelected) Color.White else TextMuted
                    )
                }
            }

            // WALL Mode Button
            val isWallSelected = currentMode == ActionMode.WALL
            val isWallEnabled = wallsRemaining > 0
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isWallSelected) WallBronze else Color.Transparent
                    )
                    .clickable(enabled = isWallEnabled) {
                        onModeSelected(ActionMode.WALL)
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = when {
                            isWallSelected -> Color.Black
                            !isWallEnabled -> TextMuted.copy(alpha = 0.4f)
                            else -> TextMuted
                        },
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isWallEnabled) "PLACE WALL ($wallsRemaining)" else "NO WALLS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isWallSelected -> Color.Black
                            !isWallEnabled -> TextMuted.copy(alpha = 0.4f)
                            else -> TextMuted
                        }
                    )
                }
            }
        }

        // Secondary: Wall Orientation Selector (only shown when WALL mode is active)
        AnimatedVisibility(
            visible = currentMode == ActionMode.WALL,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // HORIZONTAL
                    val isHoriz = selectedOrientation == WallOrientation.HORIZONTAL
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isHoriz) WallBronze.copy(alpha = 0.25f) else Color.Transparent)
                            .border(
                                width = if (isHoriz) 1.dp else 0.dp,
                                color = if (isHoriz) WallBronze else Color.Transparent,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { onOrientationSelected(WallOrientation.HORIZONTAL) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CropLandscape,
                                contentDescription = null,
                                tint = if (isHoriz) WallBronze else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "HORIZONTAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isHoriz) WallBronze else TextMuted
                            )
                        }
                    }

                    // VERTICAL
                    val isVert = selectedOrientation == WallOrientation.VERTICAL
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isVert) WallBronze.copy(alpha = 0.25f) else Color.Transparent)
                            .border(
                                width = if (isVert) 1.dp else 0.dp,
                                color = if (isVert) WallBronze else Color.Transparent,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable { onOrientationSelected(WallOrientation.VERTICAL) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CropPortrait,
                                contentDescription = null,
                                tint = if (isVert) WallBronze else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "VERTICAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isVert) WallBronze else TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
