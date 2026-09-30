package com.example.mazebuilderduel.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mazebuilderduel.ui.theme.MidnightBg
import com.example.mazebuilderduel.ui.theme.Player1Copper
import com.example.mazebuilderduel.ui.theme.Player1CopperBright
import com.example.mazebuilderduel.ui.theme.Player2Teal
import com.example.mazebuilderduel.ui.theme.Player2TealBright
import com.example.mazebuilderduel.ui.theme.TextMuted
import com.example.mazebuilderduel.ui.theme.WallBronze
import com.example.mazebuilderduel.ui.theme.WallIvory
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    // Animation progress drivers
    val gridAlpha = remember { Animatable(0f) }
    val p1Expansion = remember { Animatable(0f) }
    val wallSlide = remember { Animatable(0f) }
    val p2Expansion = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Step 1: Grid fades in (0-300ms)
        gridAlpha.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
        // Step 2: Player 1 expands (300-600ms)
        p1Expansion.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
        // Step 3: Wall slides into place (600-900ms)
        wallSlide.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
        // Step 4: Player 2 bends around (900-1200ms)
        p2Expansion.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
        // Step 5: Title appears (1200-1500ms)
        titleAlpha.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
        delay(200)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated 7x7 mini grid demonstration
            Canvas(
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer { alpha = gridAlpha.value }
            ) {
                val sizeVal = size.width
                val cellSize = sizeVal / 7f
                val corner = CornerRadius(cellSize * 0.18f, cellSize * 0.18f)

                // Background grid cells
                for (r in 0 until 7) {
                    for (c in 0 until 7) {
                        val pad = cellSize * 0.10f
                        drawRoundRect(
                            color = Color(0xFF161B24),
                            topLeft = Offset(c * cellSize + pad, r * cellSize + pad),
                            size = Size(cellSize - pad * 2, cellSize - pad * 2),
                            cornerRadius = corner
                        )
                    }
                }

                // Initial corner points
                // P1 at (6, 0)
                val pad = cellSize * 0.10f
                drawRoundRect(
                    color = Player1Copper,
                    topLeft = Offset(0f * cellSize + pad, 6f * cellSize + pad),
                    size = Size(cellSize - pad * 2, cellSize - pad * 2),
                    cornerRadius = corner
                )

                // P2 at (0, 6)
                drawRoundRect(
                    color = Player2Teal,
                    topLeft = Offset(6f * cellSize + pad, 0f * cellSize + pad),
                    size = Size(cellSize - pad * 2, cellSize - pad * 2),
                    cornerRadius = corner
                )

                // P1 expands to (5, 0) and (4, 0)
                if (p1Expansion.value > 0f) {
                    val alphaP1 = p1Expansion.value
                    drawRoundRect(
                        color = Player1CopperBright.copy(alpha = alphaP1),
                        topLeft = Offset(0f * cellSize + pad, 5f * cellSize + pad),
                        size = Size(cellSize - pad * 2, cellSize - pad * 2),
                        cornerRadius = corner
                    )
                    drawRoundRect(
                        color = Player1CopperBright.copy(alpha = alphaP1),
                        topLeft = Offset(0f * cellSize + pad, 4f * cellSize + pad),
                        size = Size(cellSize - pad * 2, cellSize - pad * 2),
                        cornerRadius = corner
                    )
                }

                // Wall slides in at horizontal edge between (3, 0) and (4, 0)
                if (wallSlide.value > 0f) {
                    val slideOffset = (1f - wallSlide.value) * cellSize
                    val wallY = 4f * cellSize
                    val wallThick = cellSize * 0.22f
                    drawRoundRect(
                        color = WallBronze.copy(alpha = wallSlide.value),
                        topLeft = Offset(pad - slideOffset, wallY - wallThick / 2f),
                        size = Size(cellSize * 0.9f, wallThick),
                        cornerRadius = CornerRadius(wallThick / 2f, wallThick / 2f)
                    )
                }

                // P2 bends towards goal (0, 5), (1, 5), (2, 5)
                if (p2Expansion.value > 0f) {
                    val alphaP2 = p2Expansion.value
                    drawRoundRect(
                        color = Player2TealBright.copy(alpha = alphaP2),
                        topLeft = Offset(5f * cellSize + pad, 0f * cellSize + pad),
                        size = Size(cellSize - pad * 2, cellSize - pad * 2),
                        cornerRadius = corner
                    )
                    drawRoundRect(
                        color = Player2TealBright.copy(alpha = alphaP2),
                        topLeft = Offset(5f * cellSize + pad, 1f * cellSize + pad),
                        size = Size(cellSize - pad * 2, cellSize - pad * 2),
                        cornerRadius = corner
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Title & Subtitle with fade-in
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer { alpha = titleAlpha.value }
            ) {
                Text(
                    text = "MAZE BUILDER DUEL",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.5.sp
                    ),
                    color = WallIvory
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Build your route. Block theirs.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        letterSpacing = 1.sp
                    ),
                    color = TextMuted
                )
            }
        }
    }
}
