package com.example.meeseekstaskmaster

import com.example.meeseekstaskmaster.util.TimeUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TimeUtilsTest {

    @Test
    fun formatCountdown_zeroOrNegative_returnsZeroFormatted() {
        assertEquals("00:00:00", TimeUtils.formatCountdown(0L))
        assertEquals("00:00:00", TimeUtils.formatCountdown(-5000L))
    }

    @Test
    fun formatCountdown_validMillis_formatsHHMMSS() {
        // 1 segundo = 1000 ms
        assertEquals("00:00:01", TimeUtils.formatCountdown(1000L))

        // 1 minuto y 5 segundos = 65,000 ms
        assertEquals("00:01:05", TimeUtils.formatCountdown(65_000L))

        // 2 horas, 15 minutos, 30 segundos = (2*3600 + 15*60 + 30) * 1000 = 8,130,000 ms
        assertEquals("02:15:30", TimeUtils.formatCountdown(8_130_000L))
    }

    @Test
    fun isOverdue_evaluatesCorrectly() {
        val now = 1000000L
        val pastDeadline = 999999L
        val futureDeadline = 1000001L

        assertTrue(TimeUtils.isOverdue(pastDeadline, now))
        assertTrue(TimeUtils.isOverdue(now, now)) // En el límite exacto se considera vencida
        assertFalse(TimeUtils.isOverdue(futureDeadline, now))
    }

    @Test
    fun calculateRemainingMillis_boundsAtZero() {
        val now = 5000L
        assertEquals(3000L, TimeUtils.calculateRemainingMillis(8000L, now))
        assertEquals(0L, TimeUtils.calculateRemainingMillis(4000L, now))
    }

    @Test
    fun formatOverdueElapsed_formatsWithPlusSign() {
        val elapsed = (1 * 3600 + 20 * 60 + 5) * 1000L // 1 hora 20 min 5 seg
        assertEquals("+01:20:05", TimeUtils.formatOverdueElapsed(elapsed))
    }
}
