package com.language

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.language.models.Player
import com.language.utils.ThemeUtils

class Result_activity : AppCompatActivity() {
    private lateinit var players: List<Player>
    private var currentPlayerIndex = 0
    private lateinit var selectedSets: List<String>
    private lateinit var language: String
    private var totalTimeInMillis: Long = 60000
    private var wordsExhausted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        setContentView(R.layout.resultat)
        supportActionBar?.hide()

        players = intent.getParcelableArrayListExtra("players") ?: emptyList()
        currentPlayerIndex = intent.getIntExtra("currentPlayerIndex", 0)
        selectedSets = intent.getStringArrayListExtra("selectedSets") ?: emptyList()
        language = intent.getStringExtra("language") ?: "English"
        totalTimeInMillis = intent.getLongExtra("totalTimeInMillis", 60000)
        wordsExhausted = intent.getBooleanExtra("wordsExhausted", false)

        val nameTextView = findViewById<TextView>(R.id.playerName)
        val guessedTextView = findViewById<TextView>(R.id.guessedWords)
        val notGuessedTextView = findViewById<TextView>(R.id.notGuessedWords)
        val languageTitle = findViewById<TextView>(R.id.languageTitle)
        val selectedSetTitle = findViewById<TextView>(R.id.selectedSetTitle)
        val logoutIcon = findViewById<ImageView>(R.id.logoutIcon)
        val nextButton = findViewById<FrameLayout>(R.id.leftButton)

        val currentPlayer = players.getOrNull(currentPlayerIndex) ?: return
        val guessedWords = currentPlayer.lastRoundGuessed
        val notGuessedWords = currentPlayer.lastRoundNotGuessed

        selectedSetTitle.movementMethod = ScrollingMovementMethod()
        guessedTextView.movementMethod = ScrollingMovementMethod()
        notGuessedTextView.movementMethod = ScrollingMovementMethod()

        languageTitle.text = currentPlayer.name
        selectedSetTitle.text = selectedSets.joinToString(", ")
        nameTextView.text = currentPlayer.name
        guessedTextView.text = guessedWords.joinToString("\n")
        notGuessedTextView.text = notGuessedWords.joinToString("\n")

        logoutIcon.setOnClickListener {
            navigateToChooseSetWithReset()
        }

        nextButton.setOnClickListener {
            moveToNextPlayerOrFinish()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToChooseSetWithReset()
            }
        })
    }

    private fun moveToNextPlayerOrFinish() {
        if (wordsExhausted) {
            // Если слова закончились, сразу переходим к командным результатам
            navigateToCommandResults()
            return
        }

        val nextPlayerIndex = currentPlayerIndex + 1

        if (nextPlayerIndex >= players.size) {
            navigateToCommandResults()
        } else {
            val intent = Intent(this, PlayActivity::class.java).apply {
                putParcelableArrayListExtra("players", ArrayList(players))
                putStringArrayListExtra("selectedSets", ArrayList(selectedSets))
                putExtra("language", language)
                putExtra("currentPlayerIndex", nextPlayerIndex)
                putExtra("totalTimeInMillis", totalTimeInMillis)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
        }
        finish()
    }

    private fun navigateToCommandResults() {
        Intent(this, CommandResAct::class.java).apply {
            putParcelableArrayListExtra("players", ArrayList(players))
            putStringArrayListExtra("selectedSets", ArrayList(selectedSets))
            putExtra("language", language)
            putExtra("totalTimeInMillis", totalTimeInMillis)
            putExtra("wordsExhausted", wordsExhausted)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }.also { startActivity(it) }
    }

    private fun navigateToChooseSetWithReset() {
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
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }
}