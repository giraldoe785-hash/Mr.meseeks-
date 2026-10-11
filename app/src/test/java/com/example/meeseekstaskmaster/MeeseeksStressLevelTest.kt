package com.example.meeseekstaskmaster

import com.example.meeseekstaskmaster.model.MeeseeksStressLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class MeeseeksStressLevelTest {

    @Test
    fun fromProgress_zeroToFortyPercent_returnsNormal() {
        assertEquals(MeeseeksStressLevel.NORMAL, MeeseeksStressLevel.fromProgress(0.0f, false))
        assertEquals(MeeseeksStressLevel.NORMAL, MeeseeksStressLevel.fromProgress(0.25f, false))
        assertEquals(MeeseeksStressLevel.NORMAL, MeeseeksStressLevel.fromProgress(0.40f, false))
        assertEquals("I'm Mr. Meeseeks, look at me!", MeeseeksStressLevel.NORMAL.quote)
    }

    @Test
    fun fromProgress_fortyOneToSeventyFivePercent_returnsStressed() {
        assertEquals(MeeseeksStressLevel.STRESSED, MeeseeksStressLevel.fromProgress(0.41f, false))
        assertEquals(MeeseeksStressLevel.STRESSED, MeeseeksStressLevel.fromProgress(0.60f, false))
        assertEquals(MeeseeksStressLevel.STRESSED, MeeseeksStressLevel.fromProgress(0.75f, false))
        assertEquals("¡Esto se está volviendo raro, tengo que cumplir!", MeeseeksStressLevel.STRESSED.quote)
    }

    @Test
    fun fromProgress_aboveSeventyFivePercentBeforeDeadline_returnsCrisis() {
        assertEquals(MeeseeksStressLevel.CRISIS, MeeseeksStressLevel.fromProgress(0.76f, false))
        assertEquals(MeeseeksStressLevel.CRISIS, MeeseeksStressLevel.fromProgress(0.90f, false))
        assertEquals(MeeseeksStressLevel.CRISIS, MeeseeksStressLevel.fromProgress(0.99f, false))
        assertEquals("¡EL TIEMPO SE AGOTA! ¡EXISTENCIA ES DOLOR!", MeeseeksStressLevel.CRISIS.quote)
    }

    @Test
    fun fromProgress_isOverdue_takesPriorityOverAnyProgress() {
        // El vencimiento tiene prioridad absoluta sobre cualquier porcentaje
        assertEquals(MeeseeksStressLevel.OVERDUE, MeeseeksStressLevel.fromProgress(0.10f, true))
        assertEquals(MeeseeksStressLevel.OVERDUE, MeeseeksStressLevel.fromProgress(0.50f, true))
        assertEquals(MeeseeksStressLevel.OVERDUE, MeeseeksStressLevel.fromProgress(0.99f, true))
        assertEquals(MeeseeksStressLevel.OVERDUE, MeeseeksStressLevel.fromProgress(1.0f, false))
        assertEquals(MeeseeksStressLevel.OVERDUE, MeeseeksStressLevel.fromProgress(1.5f, false))
        assertEquals("¡EXISTENCE IS PAIN! ¡MÁTALO YA!", MeeseeksStressLevel.OVERDUE.quote)
    }
}
