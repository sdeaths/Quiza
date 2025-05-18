package com.language


import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class TestApplication : Application() {
    // Можно добавить специфичную для тестов логику инициализации
}

// Необязательно: кастомный тестовый раннер
class CustomTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, TestApplication::class.java.name, context)
    }
}