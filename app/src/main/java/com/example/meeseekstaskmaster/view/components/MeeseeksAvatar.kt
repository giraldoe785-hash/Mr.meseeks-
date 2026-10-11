package com.example.meeseekstaskmaster.view.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meeseekstaskmaster.model.MeeseeksStressLevel
import com.example.meeseekstaskmaster.view.theme.MeeseeksBlue
import com.example.meeseekstaskmaster.view.theme.MeeseeksHairOrange
import com.example.meeseekstaskmaster.view.theme.PortalGreen
import com.example.meeseekstaskmaster.view.theme.SpaceDark
import com.example.meeseekstaskmaster.view.theme.StressBloodRed
import com.example.meeseekstaskmaster.view.theme.StressCrisisColor
import com.example.meeseekstaskmaster.view.theme.StressWarningColor
import com.example.meeseekstaskmaster.view.theme.TextWhite

@Composable
fun MeeseeksAvatar(
    stressLevel: MeeseeksStressLevel,
    modifier: Modifier = Modifier,
    size: Dp = 90.dp,
    showQuoteBubble: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "meeseeks_anim")

    // Animación de flotación suave para NORMAL
    val normalFloatOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "normal_float"
    )

    // Animación de temblor nervioso para ESTRESADO
    val nervousTremor by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(80, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nervous_tremor"
    )

    // Animación de crisis frenética para CRISIS y OVERDUE
    val crisisShakeX by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(45, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "crisis_shake_x"
    )
    val crisisShakeY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(35, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "crisis_shake_y"
    )

    // Desplazamiento dinámico según el estrés
    val offsetX = when (stressLevel) {
        MeeseeksStressLevel.NORMAL -> 0f
        MeeseeksStressLevel.STRESSED -> nervousTremor
        MeeseeksStressLevel.CRISIS, MeeseeksStressLevel.OVERDUE -> crisisShakeX
    }

    val offsetY = when (stressLevel) {
        MeeseeksStressLevel.NORMAL -> normalFloatOffset
        MeeseeksStressLevel.STRESSED -> 0f
        MeeseeksStressLevel.CRISIS, MeeseeksStressLevel.OVERDUE -> crisisShakeY
    }

    // Color del aura del borde animado
    val borderColor by animateColorAsState(
        targetValue = when (stressLevel) {
            MeeseeksStressLevel.NORMAL -> PortalGreen
            MeeseeksStressLevel.STRESSED -> StressWarningColor
            MeeseeksStressLevel.CRISIS -> StressCrisisColor
            MeeseeksStressLevel.OVERDUE -> StressBloodRed
        },
        label = "border_color"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showQuoteBubble) {
            SpeechBubble(
                quote = stressLevel.quote,
                stressLevel = stressLevel
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        Box(
            modifier = Modifier
                .size(size)
                .offset(x = offsetX.dp, y = offsetY.dp)
                .clip(CircleShape)
                .border(2.5.dp, borderColor, CircleShape)
                .background(SpaceDark),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size)) {
                drawMeeseeksFace(stressLevel)
            }
        }
    }
}

private fun DrawScope.drawMeeseeksFace(stressLevel: MeeseeksStressLevel) {
    val w = size.width
    val h = size.height
    val centerX = w / 2f
    val centerY = h / 2f

    // 1. Cabeza celeste Meeseeks
    val headColor = when (stressLevel) {
        MeeseeksStressLevel.NORMAL -> MeeseeksBlue
        MeeseeksStressLevel.STRESSED -> Color(0xFF4DB6AC)
        MeeseeksStressLevel.CRISIS -> Color(0xFF26A69A)
        MeeseeksStressLevel.OVERDUE -> Color(0xFF00897B)
    }

    drawCircle(
        color = headColor,
        radius = w * 0.42f,
        center = Offset(centerX, centerY)
    )

    // 2. Mechón naranja de cabello en la parte superior
    val hairPath = Path().apply {
        moveTo(centerX - w * 0.08f, centerY - h * 0.42f)
        quadraticTo(
            centerX - w * 0.04f, centerY - h * 0.52f,
            centerX, centerY - h * 0.48f
        )
        quadraticTo(
            centerX + w * 0.05f, centerY - h * 0.53f,
            centerX + w * 0.08f, centerY - h * 0.42f
        )
        close()
    }
    drawPath(path = hairPath, color = MeeseeksHairOrange)

    // 3. Ojos según el nivel de estrés
    when (stressLevel) {
        MeeseeksStressLevel.NORMAL -> {
            // Ojos grandes negros y felices con destellos blancos
            val eyeRadius = w * 0.07f
            val eyeOffsetY = centerY - h * 0.06f

            // Ojo izquierdo
            drawCircle(Color.Black, eyeRadius, Offset(centerX - w * 0.16f, eyeOffsetY))
            drawCircle(Color.White, eyeRadius * 0.4f, Offset(centerX - w * 0.18f, eyeOffsetY - eyeRadius * 0.3f))

            // Ojo derecho
            drawCircle(Color.Black, eyeRadius, Offset(centerX + w * 0.16f, eyeOffsetY))
            drawCircle(Color.White, eyeRadius * 0.4f, Offset(centerX + w * 0.14f, eyeOffsetY - eyeRadius * 0.3f))

            // Sonrisa alegre abierta
            val mouthPath = Path().apply {
                moveTo(centerX - w * 0.18f, centerY + h * 0.10f)
                quadraticTo(
                    centerX, centerY + h * 0.28f,
                    centerX + w * 0.18f, centerY + h * 0.10f
                )
                close()
            }
            drawPath(mouthPath, Color.Black)
            // Lengua rosada
            drawCircle(
                color = Color(0xFFFF8A80),
                radius = w * 0.07f,
                center = Offset(centerX, centerY + h * 0.18f)
            )
        }

        MeeseeksStressLevel.STRESSED -> {
            // Cejas preocupadas
            drawLine(
                color = Color.Black,
                start = Offset(centerX - w * 0.22f, centerY - h * 0.16f),
                end = Offset(centerX - w * 0.08f, centerY - h * 0.11f),
                strokeWidth = 3f
            )
            drawLine(
                color = Color.Black,
                start = Offset(centerX + w * 0.22f, centerY - h * 0.16f),
                end = Offset(centerX + w * 0.08f, centerY - h * 0.11f),
                strokeWidth = 3f
            )

            // Ojos ovalados preocupados
            val eyeOffsetY = centerY - h * 0.05f
            drawCircle(Color.Black, w * 0.065f, Offset(centerX - w * 0.15f, eyeOffsetY))
            drawCircle(Color.White, w * 0.025f, Offset(centerX - w * 0.16f, eyeOffsetY - 3f))

            drawCircle(Color.Black, w * 0.065f, Offset(centerX + w * 0.15f, eyeOffsetY))
            drawCircle(Color.White, w * 0.025f, Offset(centerX + w * 0.14f, eyeOffsetY - 3f))

            // Gota de sudor
            val sweatPath = Path().apply {
                moveTo(centerX + w * 0.32f, centerY - h * 0.15f)
                lineTo(centerX + w * 0.28f, centerY - h * 0.08f)
                quadraticTo(
                    centerX + w * 0.32f, centerY - h * 0.04f,
                    centerX + w * 0.36f, centerY - h * 0.08f
                )
                close()
            }
            drawPath(sweatPath, Color(0xFF80DEEA))

            // Boca temblorosa en zigzag
            val mouthPath = Path().apply {
                moveTo(centerX - w * 0.16f, centerY + h * 0.14f)
                lineTo(centerX - w * 0.06f, centerY + h * 0.11f)
                lineTo(centerX, centerY + h * 0.16f)
                lineTo(centerX + w * 0.06f, centerY + h * 0.12f)
                lineTo(centerX + w * 0.16f, centerY + h * 0.15f)
            }
            drawPath(mouthPath, Color.Black, style = Stroke(width = 4f))
        }

        MeeseeksStressLevel.CRISIS, MeeseeksStressLevel.OVERDUE -> {
            // Ojos desorbitados inyectados de sangre y desesperación
            val eyeRadius = w * 0.09f
            val eyeOffsetY = centerY - h * 0.06f

            // Ojo izquierdo (fondo blanco)
            drawCircle(Color.White, eyeRadius, Offset(centerX - w * 0.16f, eyeOffsetY))
            drawCircle(Color(0xFFFF1744), eyeRadius * 0.35f, Offset(centerX - w * 0.16f, eyeOffsetY))
            drawCircle(Color.Black, eyeRadius * 0.2f, Offset(centerX - w * 0.16f, eyeOffsetY))

            // Venas rojas en ojo izquierdo
            drawLine(
                Color.Red,
                Offset(centerX - w * 0.23f, eyeOffsetY - 5f),
                Offset(centerX - w * 0.19f, eyeOffsetY),
                strokeWidth = 2f
            )

            // Ojo derecho (fondo blanco)
            drawCircle(Color.White, eyeRadius, Offset(centerX + w * 0.16f, eyeOffsetY))
            drawCircle(Color(0xFFFF1744), eyeRadius * 0.35f, Offset(centerX + w * 0.16f, eyeOffsetY))
            drawCircle(Color.Black, eyeRadius * 0.2f, Offset(centerX + w * 0.16f, eyeOffsetY))

            // Venas rojas en ojo derecho
            drawLine(
                Color.Red,
                Offset(centerX + w * 0.23f, eyeOffsetY - 5f),
                Offset(centerX + w * 0.19f, eyeOffsetY),
                strokeWidth = 2f
            )

            // Boca abierta gritando en agonía (¡EXISTENCE IS PAIN!)
            val mouthPath = Path().apply {
                moveTo(centerX - w * 0.20f, centerY + h * 0.10f)
                quadraticTo(
                    centerX, centerY + h * 0.06f,
                    centerX + w * 0.20f, centerY + h * 0.10f
                )
                quadraticTo(
                    centerX, centerY + h * 0.34f,
                    centerX - w * 0.20f, centerY + h * 0.10f
                )
                close()
            }
            drawPath(mouthPath, Color.Black, style = Fill)

            // Dientes superiores visibles
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(centerX - w * 0.12f, centerY + h * 0.10f),
                size = Size(w * 0.24f, h * 0.05f),
                cornerRadius = CornerRadius(2f, 2f)
            )
        }
    }
}

@Composable
fun SpeechBubble(
    quote: String,
    stressLevel: MeeseeksStressLevel,
    modifier: Modifier = Modifier
) {
    val bubbleColor = when (stressLevel) {
        MeeseeksStressLevel.NORMAL -> Color(0xFF1E2638)
        MeeseeksStressLevel.STRESSED -> Color(0xFF2C2415)
        MeeseeksStressLevel.CRISIS -> Color(0xFF351218)
        MeeseeksStressLevel.OVERDUE -> Color(0xFF420B13)
    }

    val textColor = when (stressLevel) {
        MeeseeksStressLevel.NORMAL -> MeeseeksBlue
        MeeseeksStressLevel.STRESSED -> StressWarningColor
        MeeseeksStressLevel.CRISIS -> StressCrisisColor
        MeeseeksStressLevel.OVERDUE -> Color(0xFFFF5252)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bubbleColor)
            .border(1.dp, textColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = "\"$quote\"",
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}
