package com.language

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.language.databinding.ActivitySettingsBinding
import com.language.utils.ThemeUtils

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private var isUserInteraction = true

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupThemeSwitches()
        updateSwitchStates()
    }

    private fun setupThemeSwitches() {
        binding.lightThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isUserInteraction && isChecked) {
                handleThemeChange(ThemeUtils.THEME_LIGHT)
            }
        }

        binding.darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isUserInteraction && isChecked) {
                handleThemeChange(ThemeUtils.THEME_DARK)
            }
        }

        binding.brightThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isUserInteraction && isChecked) {
                handleThemeChange(ThemeUtils.THEME_BRIGHT)
            }
        }
    }

    private fun handleThemeChange(theme: Int) {
        isUserInteraction = false
        ThemeUtils.saveTheme(this, theme)
        ThemeUtils.restartApp(this)
    }

    private fun updateSwitchStates() {
        isUserInteraction = false

        when (ThemeUtils.getSavedTheme(this)) {
            ThemeUtils.THEME_LIGHT -> setSwitchStates(true, false, false)
            ThemeUtils.THEME_DARK -> setSwitchStates(false, true, false)
            ThemeUtils.THEME_BRIGHT -> setSwitchStates(false, false, true)
        }

        isUserInteraction = true
    }

    private fun setSwitchStates(light: Boolean, dark: Boolean, bright: Boolean) {
        binding.lightThemeSwitch.isChecked = light
        binding.darkThemeSwitch.isChecked = dark
        binding.brightThemeSwitch.isChecked = bright
    }

    override fun onResume() {
        super.onResume()
        updateSwitchStates()
    }
}