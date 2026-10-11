package com.example.meeseekstaskmaster.view.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meeseekstaskmaster.model.MeeseeksStressLevel
import com.example.meeseekstaskmaster.model.Task
import com.example.meeseekstaskmaster.model.TaskStatus
import com.example.meeseekstaskmaster.util.TimeUtils
import com.example.meeseekstaskmaster.view.theme.CardBackground
import com.example.meeseekstaskmaster.view.theme.MeeseeksBlue
import com.example.meeseekstaskmaster.view.theme.PortalGreen
import com.example.meeseekstaskmaster.view.theme.SpaceDark
import com.example.meeseekstaskmaster.view.theme.StressBloodRed
import com.example.meeseekstaskmaster.view.theme.StressCrisisColor
import com.example.meeseekstaskmaster.view.theme.StressWarningColor
import com.example.meeseekstaskmaster.view.theme.TextMuted
import com.example.meeseekstaskmaster.view.theme.TextWhite

@Composable
fun TaskCard(
    task: Task,
    currentTime: Long,
    onComplete: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onOpenCrisis: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val stressLevel = task.getStressLevel(currentTime)
    val remainingMillis = task.getRemainingMillis(currentTime)
    val isOverdue = task.isOverdue(currentTime)

    val borderColor = when (stressLevel) {
        MeeseeksStressLevel.NORMAL -> PortalGreen.copy(alpha = 0.35f)
        MeeseeksStressLevel.STRESSED -> StressWarningColor.copy(alpha = 0.5f)
        MeeseeksStressLevel.CRISIS -> StressCrisisColor.copy(alpha = 0.7f)
        MeeseeksStressLevel.OVERDUE -> StressBloodRed
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Fila superior: Nivel de estrés y botón de eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StressBadge(stressLevel = stressLevel)

                IconButton(
                    onClick = { onDelete(task) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar tarea",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Contenido central: Avatar a la izquierda, detalles a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MeeseeksAvatar(
                    stressLevel = stressLevel,
                    size = 72.dp,
                    showQuoteBubble = false
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (task.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Vence: ${TimeUtils.formatDateTime(task.deadline)}",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Frase temática del Meeseeks
            SpeechBubble(
                quote = stressLevel.quote,
                stressLevel = stressLevel,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Fila inferior: Cuenta regresiva y Botón de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CountdownTimer(
                    remainingMillis = if (isOverdue) -(currentTime - task.deadline) else remainingMillis,
                    stressLevel = stressLevel,
                    fontSize = 14.sp
                )

                if (isOverdue) {
                    Button(
                        onClick = { onOpenCrisis(task) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StressBloodRed,
                            contentColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "¡CRISIS!", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                } else {
                    Button(
                        onClick = { onComplete(task) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PortalGreen,
                            contentColor = SpaceDark
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "¡Completar!", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StressBadge(
    stressLevel: MeeseeksStressLevel,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (stressLevel) {
        MeeseeksStressLevel.NORMAL -> Color(0xFF003822) to PortalGreen
        MeeseeksStressLevel.STRESSED -> Color(0xFF3E2700) to StressWarningColor
        MeeseeksStressLevel.CRISIS -> Color(0xFF42000B) to StressCrisisColor
        MeeseeksStressLevel.OVERDUE -> Color(0xFF5E000F) to Color(0xFFFF5252)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = stressLevel.title,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}
