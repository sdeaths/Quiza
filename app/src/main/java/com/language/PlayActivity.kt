package com.language

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.language.databinding.QwizPlayBinding
import com.language.models.Player
import com.language.utils.ThemeUtils

class PlayActivity : AppCompatActivity() {

    private lateinit var binding: QwizPlayBinding
    private val playViewModel: PlayViewModel by viewModels()
    private lateinit var frontAnim: AnimatorSet
    private lateinit var backAnim: AnimatorSet
    private var wordFlipped = false
    private var isProcessingClick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        binding = QwizPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })

        loadAnimations()
        setupViews()
        setupObservers()
        startGame()
    }

    private fun loadAnimations() {
        binding.WordContainer.cameraDistance = 8000 * resources.displayMetrics.density
        frontAnim = AnimatorInflater.loadAnimator(this, R.animator.front_animator) as AnimatorSet
        backAnim = AnimatorInflater.loadAnimator(this, R.animator.back_animator) as AnimatorSet
    }

    private fun setupViews() {
        binding.selectedSetTitle.movementMethod = ScrollingMovementMethod()
        binding.selectedSetTitle.isSelected = true
        binding.wordText.movementMethod = ScrollingMovementMethod()

        binding.leftButton.setOnClickListener { handleWordMarking(false) }
        binding.rightButton.setOnClickListener { handleWordMarking(true) }

        binding.centerButton.setOnClickListener {
            playViewModel.currentWord.value?.let {
                showWordAddedPopup()
                playViewModel.addCurrentWordToMySet()
            } ?: run {
                Toast.makeText(this, "Нет текущего слова для добавления", Toast.LENGTH_SHORT).show()
            }
        }

        binding.WordContainer.setOnClickListener { flipCard() }

        binding.logoutIcon.setOnClickListener {
            playViewModel.stopTimer()
            navigateToChooseSetWithReset()
        }
    }

    private fun startGame() {
        val totalTime = intent.getLongExtra("totalTimeInMillis", 60000)
        val currentPlayerIndex = intent.getIntExtra("currentPlayerIndex", 0)
        val players = intent.getParcelableArrayListExtra<Player>("players") ?: emptyList()
        val selectedSets = intent.getStringArrayListExtra("selectedSets") ?: emptyList()
        val language = intent.getStringExtra("language") ?: "English"

        if (selectedSets.isEmpty()) {
            Toast.makeText(this, "Не выбрано ни одного набора слов", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Не очищаем usedWords при инициализации
        playViewModel.initGame(players, language, selectedSets, currentPlayerIndex, totalTime)
        playViewModel.startRound()
    }


    private fun handleWordMarking(guessed: Boolean) {
        if (isProcessingClick) return
        isProcessingClick = true
        playViewModel.markWord(guessed)
        isProcessingClick = false
    }

    private fun setupObservers() {
        playViewModel.players.observe(this) { playersList ->
            binding.languageTitle.text = playersList.getOrNull(playViewModel.getCurrentPlayerIndex())?.name ?: "Игрок"
        }

        playViewModel.selectedSetsText.observe(this) { setsText ->
            binding.selectedSetTitle.text = setsText
        }

        playViewModel.wordText.observe(this) { word ->
            binding.wordText.text = word
            resetCard()
        }

        playViewModel.timerText.observe(this) { timerText ->
            binding.roundTimer.text = timerText
        }

        playViewModel.gameEnded.observe(this) { ended ->
            if (ended) {
                if (playViewModel.wordsExhausted.value == true) {
                    // Если слова закончились, сразу переходим к командным результатам
                    navigateToCommandResults(true)
                } else {
                    // Иначе продолжаем обычный процесс
                    navigateToResults(false)
                }
            }
        }

        playViewModel.wordsExhausted.observe(this) { exhausted ->
            if (exhausted) {
                navigateToResults(true)
            }
        }
    }
    private fun navigateToCommandResults(wordsExhausted: Boolean) {
        Intent(this, CommandResAct::class.java).apply {
            putParcelableArrayListExtra("players", ArrayList(playViewModel.players.value ?: emptyList()))
            putStringArrayListExtra("selectedSets", ArrayList(playViewModel.getSelectedSets()))
            putExtra("language", playViewModel.getLanguage())
            putExtra("totalTimeInMillis", playViewModel.getTotalTime())
            putExtra("wordsExhausted", wordsExhausted)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }.also { startActivity(it) }
        finish()
    }

    private fun navigateToResults(wordsExhausted: Boolean) {
        Intent(this, Result_activity::class.java).apply {
            putParcelableArrayListExtra("players", ArrayList(playViewModel.players.value ?: emptyList()))
            putStringArrayListExtra("guessedWords", ArrayList(playViewModel.guessedWords))
            putStringArrayListExtra("notGuessedWords", ArrayList(playViewModel.notGuessedWords))
            putStringArrayListExtra("selectedSets", ArrayList(playViewModel.getSelectedSets()))
            putExtra("currentPlayerIndex", playViewModel.getCurrentPlayerIndex())
            putExtra("totalTimeInMillis", playViewModel.getTotalTime())
            putExtra("language", playViewModel.getLanguage())
            putExtra("wordsExhausted", wordsExhausted)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }.also { startActivity(it) }
        finish()
    }

    private fun showWordAddedPopup() {
        binding.wordAddedPopup.alpha = 0f
        binding.wordAddedPopup.visibility = View.VISIBLE

        binding.wordAddedPopup.animate()
            .alpha(1f)
            .setDuration(300)
            .start()

        binding.wordAddedPopup.postDelayed({
            binding.wordAddedPopup.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    binding.wordAddedPopup.visibility = View.GONE
                }
                .start()
        }, 2000)
    }

    private fun flipCard() {
        playViewModel.currentWord.value?.let { currentWord ->
            binding.wordText.text = if (!wordFlipped) currentWord.second else currentWord.first
            frontAnim.setTarget(binding.WordContainer)
            backAnim.setTarget(binding.WordContainer)
            frontAnim.start()
            backAnim.start()
            wordFlipped = !wordFlipped
        }
    }

    private fun resetCard() {
        if (wordFlipped) {
            frontAnim.setTarget(binding.WordContainer)
            backAnim.setTarget(binding.WordContainer)
            frontAnim.start()
            backAnim.start()
            wordFlipped = false
        }
    }

    private fun navigateToChooseSetWithReset() {
        playViewModel.resetGameState() // Используем новую функцию

        val resetPlayers = playViewModel.players.value?.map { player ->
            player.copy(
                guessedWords = mutableListOf(),
                notGuessedWords = mutableListOf(),
                lastRoundGuessed = mutableListOf(),
                lastRoundNotGuessed = mutableListOf(),
                score = 0
            )
        } ?: emptyList()

        Intent(this, ChooseLanguageActivity::class.java).apply {
            putParcelableArrayListExtra("players", ArrayList(resetPlayers))
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }.also {
            startActivity(it)
            finish()
        }
    }
}