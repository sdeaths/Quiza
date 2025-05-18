package com.language.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.language.MainActivity
import com.language.R

object ThemeUtils {
    private const val PREFS_NAME = "AppThemePrefs"
    private const val THEME_KEY = "app_theme"

    const val THEME_LIGHT = 0
    const val THEME_DARK = 1
    const val THEME_BRIGHT = 2


    fun updateThemeIcons(activity: Activity) {
        try {
            // Получаем все иконки из layout
            val folderIcon = activity.findViewById<ImageView?>(R.id.icon_folder)
            val profileIcon = activity.findViewById<ImageView?>(R.id.icon_profile)
            val homeIcon = activity.findViewById<ImageView?>(R.id.icon_home)

            // Устанавливаем соответствующие иконки в зависимости от темы
            when (getSavedTheme(activity)) {
                THEME_DARK -> {
                    // Для тёмной темы
                    folderIcon?.setImageResource(R.drawable.folder)
                    profileIcon?.setImageResource(R.drawable.profile)
                    homeIcon?.setImageResource(R.drawable.home_light)
                }
                THEME_BRIGHT -> {
                    // Для яркой темы
                    folderIcon?.setImageResource(R.drawable.folder)
                    profileIcon?.setImageResource(R.drawable.profile)
                    homeIcon?.setImageResource(R.drawable.home_bright) // Используем специальную иконку для яркой темы
                }
                else -> {
                    // Для светлой темы (по умолчанию)
                    folderIcon?.setImageResource(R.drawable.folder)
                    profileIcon?.setImageResource(R.drawable.profile)
                    homeIcon?.setImageResource(R.drawable.home)
                }
            }
        } catch (e: Exception) {
            Log.e("ThemeUtils", "Ошибка при обновлении иконок", e)
        }
    }


    fun saveTheme(context: Context, theme: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(THEME_KEY, theme)
            .apply()
    }

    fun getSavedTheme(context: Context): Int {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(THEME_KEY, THEME_LIGHT)
    }

    fun applyTheme(activity: Activity) {
        when (getSavedTheme(activity)) {
            THEME_LIGHT -> setLightTheme(activity)
            THEME_DARK -> setDarkTheme(activity)
            THEME_BRIGHT -> setBrightTheme(activity)
        }
    }

    private fun setLightTheme(activity: Activity) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        activity.setTheme(R.style.Theme_Language)

    }

    private fun setDarkTheme(activity: Activity) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        activity.setTheme(R.style.Theme_Language_Dark)

    }

    private fun setBrightTheme(activity: Activity) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        activity.setTheme(R.style.Theme_Language_Bright)

    }



    fun restartApp(activity: Activity) {
        val intent = Intent(activity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        activity.startActivity(intent)
        activity.finish()
        activity.overridePendingTransition(0, 0)
    }
    fun applyPopupTheme(context: Context, popupView: View) {
        // Получаем цвет текста из темы
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.text, typedValue, true)
        val textColor = typedValue.data

        // Применяем цвет ко всем текстовым элементам
        applyTextColorToViews(popupView, textColor)
    }

    private fun applyTextColorToViews(view: View, color: Int) {
        when (view) {
            is TextView -> {
                view.setTextColor(color)
                if (view is EditText) {
                    view.setHintTextColor(color.withAlpha(0x7F))
                }
            }
            is ViewGroup -> {
                for (i in 0 until view.childCount) {
                    applyTextColorToViews(view.getChildAt(i), color)
                }
            }
        }
    }

    private fun Int.withAlpha(alpha: Int): Int {
        return (alpha shl 24) or (this and 0x00FFFFFF)
    }
}