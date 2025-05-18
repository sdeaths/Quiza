
package com.language

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TimerLogicTest {

    private lateinit var timerLogic: TimerLogic

    @Before
    fun setup() {
        timerLogic = TimerLogic()
    }

    @Test
    fun `test timer increase within limits`() {
        timerLogic.totalSeconds = 60
        timerLogic.increaseTimer()
        assertEquals(70, timerLogic.totalSeconds)

        timerLogic.totalSeconds = 300
        timerLogic.increaseTimer()
        assertEquals(300, timerLogic.totalSeconds) // не должно превысить максимум
    }

    @Test
    fun `test timer decrease within limits`() {
        timerLogic.totalSeconds = 60
        timerLogic.decreaseTimer()
        assertEquals(50, timerLogic.totalSeconds)

        timerLogic.totalSeconds = 10
        timerLogic.decreaseTimer()
        assertEquals(10, timerLogic.totalSeconds) // не должно быть меньше минимума
    }

    @Test
    fun `test timer display format`() {
        timerLogic.totalSeconds = 90 // 1 минута 30 секунд
        assertEquals("01 : 30", timerLogic.formatTimerText())

        timerLogic.totalSeconds = 5 // хотя минимальное значение 10, проверим формат
        assertEquals("00 : 05", timerLogic.formatTimerText())

        timerLogic.totalSeconds = 125 // 2 минуты 5 секунд
        assertEquals("02 : 05", timerLogic.formatTimerText())
    }
}