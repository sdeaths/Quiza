package com.language

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.language.models.Player
import com.language.utils.ThemeUtils
import java.util.Locale

class ChooseTimerActivity : AppCompatActivity() {

    private lateinit var languageTitle: TextView
    private lateinit var selectedSetTitle: TextView
    private lateinit var logoutIcon: ImageView
    private lateinit var timerText: TextView
    private lateinit var plusIcon: ImageView
    private lateinit var minusIcon: ImageView
    private lateinit var startButton: FrameLayout

    var totalSeconds: Int = 60 // Начальное значение 1 минута
    lateinit var players: List<Player>
    lateinit var selectedSets: List<String>
    lateinit var language: String


    override fun onCreate(savedInstanceState: Bundle?) {

        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.timer_choose)
        supportActionBar?.hide()
        Log.d("ChooseTimerActivity", "Activity created")

        // Получаем данные из интента
        players = intent.getParcelableArrayListExtra<Player>("players") ?: emptyList()
        selectedSets = intent.getStringArrayListExtra("selectedSets") ?: emptyList()
        language = intent.getStringExtra("language") ?: "Quiz"

        Log.d("ChooseTimerActivity", "Received data:")
        Log.d("ChooseTimerActivity", "Players: ${players.joinToString { it.name }}")
        Log.d("ChooseTimerActivity", "Selected sets: ${selectedSets.joinToString()}")
        Log.d("ChooseTimerActivity", "Language: $language")

        initViews()
        setupClickListeners()
        updateTimerDisplay()
        updateUI()
        // Отключаем действие системной кнопки "назад"
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Пустое тело - кнопка "назад" ничего не делает
            }
        })
    }

    private fun initViews() {
        languageTitle = findViewById(R.id.languageTitle)
        selectedSetTitle = findViewById(R.id.selectedSetTitle)
        logoutIcon = findViewById(R.id.logoutIcon)
        timerText = findViewById(R.id.timerText)
        plusIcon = findViewById(R.id.plusIcon)
        minusIcon = findViewById(R.id.minusIcon)
        startButton = findViewById(R.id.startButton)
    }

    private fun updateUI() {
        languageTitle.text = "$language Quiz"
        selectedSetTitle.text = if (selectedSets.isNotEmpty()) {
            selectedSets.joinToString(", ")
        } else {
            "No sets selected"
        }
    }

    private fun setupClickListeners() {
        logoutIcon.setOnClickListener {
            navigateToChooseSetActivity()
        }

        plusIcon.setOnClickListener {
            if (totalSeconds < 300) { // Максимум 5 минут
                totalSeconds += 10
                updateTimerDisplay()
            }
        }

        minusIcon.setOnClickListener {
            if (totalSeconds > 10) { // Минимум 10 секунд
                totalSeconds -= 10
                updateTimerDisplay()
            }
        }

        startButton.setOnClickListener {
            Log.d("ChooseTimerActivity", "Starting game with:")
            Log.d("ChooseTimerActivity", "Time: $totalSeconds seconds")
            Log.d("ChooseTimerActivity", "Players: ${players.joinToString { it.name }}")
            Log.d("ChooseTimerActivity", "Sets: ${selectedSets.joinToString()}")

            val intent = Intent(this, PlayActivity::class.java).apply {
                putExtra("totalTimeInMillis", totalSeconds * 1000L)
                putParcelableArrayListExtra("players", ArrayList(players))
                putStringArrayListExtra("selectedSets", ArrayList(selectedSets))
                putExtra("language", language)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun updateTimerDisplay() {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        timerText.text = String.format(Locale.getDefault(), "%02d : %02d", minutes, seconds)
    }

    private fun navigateToChooseSetActivity() {
        val intent = Intent(this, ChooseSetActivity::class.java).apply {
            putExtra("language", language)
            putParcelableArrayListExtra("players", ArrayList(players))
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }




    //  нужны для тестирования
    internal fun onIncreaseTimer() {
        if (totalSeconds < 300) { // Максимум 5 минут
            totalSeconds += 10
            updateTimerDisplay()
        }
    }

    internal fun onDecreaseTimer() {
        if (totalSeconds > 10) { // Минимум 10 секунд
            totalSeconds -= 10
            updateTimerDisplay()
        }
    }

    internal fun formatTimerText(): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d : %02d", minutes, seconds)
    }

    internal fun prepareGameIntent(): Intent {
        return Intent(this, PlayActivity::class.java).apply {
            putExtra("totalTimeInMillis", totalSeconds * 1000L)
            putParcelableArrayListExtra("players", ArrayList(players))
            putStringArrayListExtra("selectedSets", ArrayList(selectedSets))
            putExtra("language", language)
        }
    }
}


