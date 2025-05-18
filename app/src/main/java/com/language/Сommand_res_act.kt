package com.language

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.language.models.Player
import com.language.utils.ThemeUtils

class CommandResAct : AppCompatActivity() {

    private lateinit var players: List<Player>
    private lateinit var selectedSets: List<String>
    private var totalTimeInMillis: Long = 60000
    private lateinit var language: String
    private var wordsExhausted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        setContentView(R.layout.command_result)
        supportActionBar?.hide()

        players = intent.getParcelableArrayListExtra("players") ?: emptyList()
        selectedSets = intent.getStringArrayListExtra("selectedSets") ?: emptyList()
        totalTimeInMillis = intent.getLongExtra("totalTimeInMillis", 60000)
        language = intent.getStringExtra("language") ?: "English"
        wordsExhausted = intent.getBooleanExtra("wordsExhausted", false)

        initUI()
        setupClickListeners()
    }

    private fun initUI() {
        val setsTextView = findViewById<TextView>(R.id.teamSets)
        val statsContainer = findViewById<LinearLayout>(R.id.playerStatsContainer)
        val nextRoundBtn = findViewById<FrameLayout>(R.id.leftButton)
        val logoutBtn = findViewById<ImageView>(R.id.logoutIcon)

        setsTextView.text = selectedSets.joinToString(", ")
        statsContainer.removeAllViews()

        players.sortedByDescending { it.score }.forEach { player ->
            layoutInflater.inflate(R.layout.item_player_result, statsContainer, false).apply {
                findViewById<TextView>(R.id.playerName).text = player.name
                findViewById<TextView>(R.id.playerScore).text = player.score.toString()
                statsContainer.addView(this)
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToChooseSetWithReset()
            }
        })
    }

    private fun setupClickListeners() {
        findViewById<FrameLayout>(R.id.leftButton).setOnClickListener {
            if (wordsExhausted) {
                navigateToChooseLanguage()
            } else {
                startNewRound()
            }
        }

        findViewById<ImageView>(R.id.logoutIcon).setOnClickListener {
            navigateToChooseSetWithReset()
        }
    }


    private fun startNewRound() {
        Intent(this, PlayActivity::class.java).apply {
            // Не нужно сбрасывать игроков здесь
            putParcelableArrayListExtra("players", ArrayList(players))
            putStringArrayListExtra("selectedSets", ArrayList(selectedSets))
            putExtra("language", language)
            putExtra("totalTimeInMillis", totalTimeInMillis)
            putExtra("currentPlayerIndex", 0) // Начинаем с первого игрока
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }.also {
            startActivity(it)
            finish()
        }
    }

    private fun navigateToChooseLanguage() {
        // Получаем ViewModel и очищаем слова
        val viewModel: PlayViewModel by viewModels()
        viewModel.resetGameState()

        val resetPlayers = players.map { player ->
            player.copy(
                guessedWords = mutableListOf(),
                notGuessedWords = mutableListOf(),
                lastRoundGuessed = mutableListOf(),
                lastRoundNotGuessed = mutableListOf(),
                score = 0
            )
        }

        Intent(this, ChooseLanguageActivity::class.java).apply {
            putParcelableArrayListExtra("players", ArrayList(resetPlayers))
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }.also {
            startActivity(it)
            finish()
        }
    }

    private fun navigateToChooseSetWithReset() {
        navigateToChooseLanguage()
    }
}