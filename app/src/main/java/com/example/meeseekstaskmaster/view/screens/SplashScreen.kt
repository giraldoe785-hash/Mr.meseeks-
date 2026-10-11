package com.example.meeseekstaskmaster.view.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meeseekstaskmaster.view.theme.MeeseeksBlue
import com.example.meeseekstaskmaster.view.theme.PortalGreen
import com.example.meeseekstaskmaster.view.theme.PortalGreenBright
import com.example.meeseekstaskmaster.view.theme.SpaceDark
import com.example.meeseekstaskmaster.view.theme.TextMuted
import com.example.meeseekstaskmaster.view.theme.TextWhite
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit
) {
    val progressAnimatable = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "portal_spin")
    val portalRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val portalPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LaunchedEffect(Unit) {
        // Animamos la barra temática durante ~2.5 segundos
        progressAnimatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2400, easing = LinearEasing)
        )
        delay(100L)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Portal animado con Canvas
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val baseRadius = (size.width / 2f) * 0.8f * portalPulse

                    // Fondo de resplandor verde
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(PortalGreenBright.copy(alpha = 0.5f), Color.Transparent),
                            center = center,
                            radius = baseRadius * 1.3f
                        ),
                        radius = baseRadius * 1.2f,
                        center = center
                    )

                    // Anillo exterior giratorio
                    rotate(portalRotation, center) {
                        drawCircle(
                            brush = Brush.sweepGradient(
                                colors = listOf(PortalGreen, PortalGreenBright, Color(0xFF1DE9B6), PortalGreen)
                            ),
                            radius = baseRadius,
                            center = center,
                            style = Stroke(width = 16f)
                        )

                        // Gotas / Remolinos de energía del portal
                        for (i in 0 until 6) {
                            val angle = (i * 60) * (Math.PI / 180).toFloat()
                            val blobX = center.x + (baseRadius * 0.7f) * kotlin.math.cos(angle)
                            val blobY = center.y + (baseRadius * 0.7f) * kotlin.math.sin(angle)
                            drawCircle(
                                color = PortalGreenBright,
                                radius = 10f,
                                center = Offset(blobX, blobY)
                            )
                        }
                    }

                    // Anillo interior en rotación contraria
                    rotate(-portalRotation * 1.5f, center) {
                        drawCircle(
                            brush = Brush.sweepGradient(
                                colors = listOf(Color(0xFF76FF03), Color(0xFF00B0FF), Color(0xFF76FF03))
                            ),
                            radius = baseRadius * 0.55f,
                            center = center,
                            style = Stroke(width = 12f)
                        )
                    }

                    // Núcleo del portal
                    drawCircle(
                        color = Color(0xFFE0F7FA),
                        radius = baseRadius * 0.25f,
                        center = center
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Título MEESEEKS
            Text(
                text = "MEESEEKS",
                style = MaterialTheme.typography.displayLarge,
                color = PortalGreenBright,
                fontSize = 36.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp
            )

            // Subtítulo
            Text(
                text = "TASK MASTER & ALARM",
                style = MaterialTheme.typography.labelLarge,
                color = MeeseeksBlue,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "\"I'm Mr. Meeseeks, look at me!\"",
                color = TextMuted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Barra de progreso temática
            LinearProgressIndicator(
                progress = { progressAnimatable.value },
                modifier = Modifier
                    .width(180.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PortalGreen,
                trackColor = Color(0xFF16212D)
            )
        }
    }
}
