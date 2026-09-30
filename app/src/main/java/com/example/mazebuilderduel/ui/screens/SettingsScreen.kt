package com.example.mazebuilderduel.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mazebuilderduel.model.BoardTheme
import com.example.mazebuilderduel.model.ThemeMode
import com.example.mazebuilderduel.ui.theme.DangerRed
import com.example.mazebuilderduel.ui.theme.TextMuted
import com.example.mazebuilderduel.ui.theme.WallBronze
import com.example.mazebuilderduel.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    var showResetStatsDialog by remember { mutableStateOf(false) }

    if (showResetStatsDialog) {
        AlertDialog(
            onDismissRequest = { showResetStatsDialog = false },
            title = { Text("Reset Statistics?") },
            text = { Text("This will clear all recorded games, win/loss tallies, and turn records.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetStatistics()
                        showResetStatsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Reset", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetStatsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SETTINGS",
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

            // Appearance Section
            SettingsSectionCard(title = "APPEARANCE") {
                // App Theme Mode
                Column {
                    Text(
                        text = "Theme Mode",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ThemeChip(
                            label = "System",
                            selected = settings.themeMode == ThemeMode.SYSTEM,
                            onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) }
                        )
                        ThemeChip(
                            label = "Dark",
                            selected = settings.themeMode == ThemeMode.DARK,
                            onClick = { viewModel.setThemeMode(ThemeMode.DARK) }
                        )
                        ThemeChip(
                            label = "Light",
                            selected = settings.themeMode == ThemeMode.LIGHT,
                            onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) }
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Board Theme
                Column {
                    Text(
                        text = "Board Palette",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ThemeChip(
                            label = "Midnight",
                            selected = settings.boardTheme == BoardTheme.MIDNIGHT,
                            onClick = { viewModel.setBoardTheme(BoardTheme.MIDNIGHT) }
                        )
                        ThemeChip(
                            label = "Classic Slate",
                            selected = settings.boardTheme == BoardTheme.CLASSIC,
                            onClick = { viewModel.setBoardTheme(BoardTheme.CLASSIC) }
                        )
                    }
                }
            }

            // Audio & Haptics Section
            SettingsSectionCard(title = "FEEDBACK") {
                SettingToggleRow(
                    label = "Sound Effects",
                    description = "Procedural audio cues for moves, walls, and wins",
                    checked = settings.soundEnabled,
                    onCheckedChange = { viewModel.setSoundEnabled(it) }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                SettingToggleRow(
                    label = "Haptic Feedback",
                    description = "Tactile response on placements and invalid moves",
                    checked = settings.hapticsEnabled,
                    onCheckedChange = { viewModel.setHapticsEnabled(it) }
                )
            }

            // Gameplay Assists Section
            SettingsSectionCard(title = "ASSISTS & DISPLAY") {
                SettingToggleRow(
                    label = "Show Legal Path Moves",
                    description = "Highlight reachable cells for active player",
                    checked = settings.showLegalMoves,
                    onCheckedChange = { viewModel.setShowLegalMoves(it) }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                SettingToggleRow(
                    label = "Show Last Action",
                    description = "Display indicator on the most recent move or wall",
                    checked = settings.showLastAction,
                    onCheckedChange = { viewModel.setShowLastAction(it) }
                )
            }

            // Data Management Section
            SettingsSectionCard(title = "DATA") {
                Button(
                    onClick = { showResetStatsDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DangerRed.copy(alpha = 0.15f),
                        contentColor = DangerRed
                    )
                ) {
                    Text(
                        text = "Reset All Career Statistics",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextMuted
            )
            content()
        }
    }
}

@Composable
private fun ThemeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = WallBronze.copy(alpha = 0.25f),
            selectedLabelColor = WallBronze
        )
    )
}

@Composable
private fun SettingToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = TextMuted
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = WallBronze,
                checkedTrackColor = WallBronze.copy(alpha = 0.4f)
            )
        )
    }
}
