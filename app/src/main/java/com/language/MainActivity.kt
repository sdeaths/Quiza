package com.language


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.language.utils.ThemeUtils

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        ThemeUtils.applyTheme(this)
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        checkFirstRunAndUploadWords()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Ничего не делаем
            }
        })

        // Используем RelativeLayout вместо Button
        val playButton: RelativeLayout = findViewById(R.id.play_button)
        playButton.setOnClickListener {
            val intent = Intent(this, PlayersActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        val themeButton: RelativeLayout = findViewById(R.id.theme_button)
        themeButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        val rulesButton: RelativeLayout = findViewById(R.id.rules_button)
        rulesButton.setOnClickListener {
            val intent = Intent(this, RulesActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun checkFirstRunAndUploadWords() {
        val prefs: SharedPreferences = getSharedPreferences("com.language.prefs", MODE_PRIVATE)
        val isFirstRun = prefs.getBoolean("is_first_run", true)

        if (isFirstRun) {
            val wordsViewModel = WordsViewModel()
            wordsViewModel.uploadWordSets()
            listOf("English", "German", "French").forEach { language ->
                wordsViewModel.createMySetIfNotExists(language)
            }
            prefs.edit().putBoolean("is_first_run", false).apply()
            Toast.makeText(this, "Начальная загрузка данных выполнена", Toast.LENGTH_SHORT).show()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }


}
