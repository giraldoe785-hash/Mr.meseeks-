package com.example.meeseekstaskmaster.view.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meeseekstaskmaster.model.MeeseeksStressLevel
import com.example.meeseekstaskmaster.util.TimeUtils
import com.example.meeseekstaskmaster.view.components.MeeseeksAvatar
import com.example.meeseekstaskmaster.view.theme.PortalGreen
import com.example.meeseekstaskmaster.view.theme.SpaceDark
import com.example.meeseekstaskmaster.view.theme.StressBloodRed
import com.example.meeseekstaskmaster.view.theme.StressCrisisColor
import com.example.meeseekstaskmaster.view.theme.TextMuted
import com.example.meeseekstaskmaster.view.theme.TextWhite
import com.example.meeseekstaskmaster.viewmodel.CrisisViewModel

@Composable
fun CrisisAlarmScreen(
    taskId: Long,
    viewModel: CrisisViewModel,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    val task by viewModel.task.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()

    // Animación de pulso de alerta roja/verde para fondo
    val infiniteTransition = rememberInfiniteTransition(label = "crisis_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "crisis_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceDark)
    ) {
        // Efecto visual estroboscópico de gradiente rojo y verde portal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(pulseAlpha)
                .background(
                    Brush.radialGradient(
                        colors = listOf(StressCrisisColor.copy(alpha = 0.45f), PortalGreen.copy(alpha = 0.2f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Cabecera de Alerta
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Crisis",
                        tint = StressCrisisColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "¡ALARMA DE CRISIS!",
                        style = MaterialTheme.typography.displayLarge,
                        color = StressCrisisColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Crisis",
                        tint = StressCrisisColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "¡EXISTENCIA ES DOLOR PARA UN MEESEEKS!",
                    color = PortalGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Cuerpo Central: Avatar desesperado y detalles de la tarea
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                MeeseeksAvatar(
                    stressLevel = MeeseeksStressLevel.OVERDUE,
                    size = 140.dp,
                    showQuoteBubble = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, StressCrisisColor, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF19090C))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = task?.title ?: "Cargando tarea...",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        if (!task?.description.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = task?.description ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val elapsedOverdue = if (task != null) {
                            (currentTime - task!!.deadline).coerceAtLeast(0L)
                        } else 0L

                        Text(
                            text = "TIEMPO EN AGONÍA:",
                            fontSize = 11.sp,
                            color = StressCrisisColor,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        Text(
                            text = TimeUtils.formatOverdueElapsed(elapsedOverdue),
                            fontSize = 32.sp,
                            color = StressCrisisColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }

            // Botones de Acción
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Botón 1: ¡HECHO! ¡MÁTALO!
                Button(
                    onClick = {
                        viewModel.completeAndKill(onFinished = onNavigateBack)
                    },
                    enabled = !isProcessing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PortalGreen,
                        contentColor = SpaceDark
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            color = SpaceDark,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "¡HECHO! ¡MÁTALO!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Botón 2: DAME 10 MINUTOS
                Button(
                    onClick = {
                        viewModel.postponeTenMinutes(onFinished = onNavigateBack)
                    },
                    enabled = !isProcessing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2C3545),
                        contentColor = TextWhite
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = PortalGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DAME 10 MINUTOS",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
