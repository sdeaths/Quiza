package com.language

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.language.utils.ThemeUtils

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Устанавливаем тему до создания любой активности
        AppCompatDelegate.setDefaultNightMode(
            when (ThemeUtils.getSavedTheme(this)) {
                ThemeUtils.THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
                ThemeUtils.THEME_BRIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                else -> AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}