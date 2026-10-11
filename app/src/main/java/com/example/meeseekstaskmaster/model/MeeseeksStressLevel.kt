package com.example.meeseekstaskmaster.model

enum class MeeseeksStressLevel(
    val title: String,
    val quote: String,
    val minPercentage: Float,
    val maxPercentage: Float
) {
    NORMAL(
        title = "NORMAL",
        quote = "I'm Mr. Meeseeks, look at me!",
        minPercentage = 0f,
        maxPercentage = 0.40f
    ),
    STRESSED(
        title = "ESTRESADO",
        quote = "¡Esto se está volviendo raro, tengo que cumplir!",
        minPercentage = 0.40f,
        maxPercentage = 0.75f
    ),
    CRISIS(
        title = "CRISIS INMINENTE",
        quote = "¡EL TIEMPO SE AGOTA! ¡EXISTENCIA ES DOLOR!",
        minPercentage = 0.75f,
        maxPercentage = 1.0f
    ),
    OVERDUE(
        title = "ALARMA VENCIDA",
        quote = "¡EXISTENCE IS PAIN! ¡MÁTALO YA!",
        minPercentage = 1.0f,
        maxPercentage = Float.MAX_VALUE
    );

    companion object {
        /**
         * Determina el nivel de estrés del Meeseeks basado en el progreso temporal y vencimiento.
         * El estado de vencimiento tiene prioridad absoluta.
         */
        fun fromProgress(progressRatio: Float, isOverdue: Boolean): MeeseeksStressLevel {
            if (isOverdue) {
                return OVERDUE
            }

            val clamped = progressRatio.coerceIn(0f, 1f)

            return when {
                clamped <= 0.40f -> NORMAL
                clamped <= 0.75f -> STRESSED
                clamped < 1.0f -> CRISIS
                else -> OVERDUE
            }
        }
    }
}
