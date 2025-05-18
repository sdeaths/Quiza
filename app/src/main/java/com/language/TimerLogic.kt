package com.language


class TimerLogic {
    var totalSeconds: Int = 60 // Начальное значение: 1 минута (60 секунд)

    fun increaseTimer() {
        if (totalSeconds < 300) { // Максимум 5 минут (300 секунд)
            totalSeconds += 10
        }
    }

    fun decreaseTimer() {
        if (totalSeconds > 10) { // Минимум 10 секунд
            totalSeconds -= 10
        }
    }

    fun formatTimerText(): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d : %02d", minutes, seconds)
    }
}