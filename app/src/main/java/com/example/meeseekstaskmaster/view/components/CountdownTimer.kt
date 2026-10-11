package com.example.meeseekstaskmaster.view.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meeseekstaskmaster.model.MeeseeksStressLevel
import com.example.meeseekstaskmaster.util.TimeUtils
import com.example.meeseekstaskmaster.view.theme.PortalGreen
import com.example.meeseekstaskmaster.view.theme.StressBloodRed
import com.example.meeseekstaskmaster.view.theme.StressCrisisColor
import com.example.meeseekstaskmaster.view.theme.StressWarningColor

@Composable
fun CountdownTimer(
    remainingMillis: Long,
    stressLevel: MeeseeksStressLevel,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp
) {
    val isOverdue = stressLevel == MeeseeksStressLevel.OVERDUE || remainingMillis <= 0

    // Animación de parpadeo de alerta para crisis y vencimiento
    val infiniteTransition = rememberInfiniteTransition(label = "timer_pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = if (isOverdue || stressLevel == MeeseeksStressLevel.CRISIS) 0.35f else 1.0f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isOverdue) 400 else 700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val timerColor by animateColorAsState(
        targetValue = when (stressLevel) {
            MeeseeksStressLevel.NORMAL -> PortalGreen
            MeeseeksStressLevel.STRESSED -> StressWarningColor
            MeeseeksStressLevel.CRISIS -> StressCrisisColor
            MeeseeksStressLevel.OVERDUE -> StressBloodRed
        },
        label = "timer_color"
    )

    val displayText = if (isOverdue) {
        val elapsedOverdue = (-remainingMillis).coerceAtLeast(0L)
        TimeUtils.formatOverdueElapsed(elapsedOverdue)
    } else {
        TimeUtils.formatCountdown(remainingMillis)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F1520))
            .border(1.dp, timerColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.alpha(if (isOverdue || stressLevel == MeeseeksStressLevel.CRISIS) alphaAnim else 1f)
        ) {
            Text(
                text = if (isOverdue) "⏰ VENCIDO " else "⏳ ",
                fontSize = fontSize * 0.8f
            )
            Text(
                text = displayText,
                color = timerColor,
                fontSize = fontSize,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
