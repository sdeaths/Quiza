package com.language

import android.app.Application
import android.content.Intent
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.language.models.Player
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PlayViewModel(application: Application) : AndroidViewModel(application) {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var timer: CountDownTimer? = null

    // LiveData
    private val _players = MutableLiveData<List<Player>>()
    val players: LiveData<List<Player>> = _players

    private val _wordText = MutableLiveData<String>()
    val wordText: LiveData<String> = _wordText

    private val _timerText = MutableLiveData<String>()
    val timerText: LiveData<String> = _timerText

    private val _currentWord = MutableLiveData<Pair<String, String>?>()
    val currentWord: LiveData<Pair<String, String>?> = _currentWord

    private val _selectedSetsText = MutableLiveData<String>()
    val selectedSetsText: LiveData<String> = _selectedSetsText

    private val _gameEnded = MutableLiveData<Boolean>()
    val gameEnded: LiveData<Boolean> = _gameEnded

    private val _wordsExhausted = MutableLiveData<Boolean>()
    val wordsExhausted: LiveData<Boolean> = _wordsExhausted

    // Words management
    private val allWordsBank = mutableListOf<Pair<String, String>>()
    private val usedWords = mutableSetOf<Pair<String, String>>()
    private var currentRoundWords = mutableListOf<Pair<String, String>>()

    // Game state
    val guessedWords = mutableListOf<String>()
    val notGuessedWords = mutableListOf<String>()

    // Game parameters
    private lateinit var selectedSets: List<String>
    private lateinit var language: String
    private var currentPlayerIndex = 0
    private var totalTimeInMillis: Long = 60000

    companion object {
        val globalUsedWords = mutableSetOf<Pair<String, String>>()

        fun clearGlobalWords() {
            globalUsedWords.clear()
        }
    }
    private val allUsedWords = mutableSetOf<Pair<String, String>>()

    fun initGame(players: List<Player>, language: String,
                 selectedSets: List<String>, playerIndex: Int, totalTime: Long) {
        allUsedWords.clear() // Очищаем при новой игре {
        this.selectedSets = selectedSets
        this.language = language
        this.currentPlayerIndex = playerIndex
        this.totalTimeInMillis = totalTime
        _players.value = players
        updateSelectedSetsText()
    }
    fun resetGameState() {
        clearAllWordLists()
        timer?.cancel()
        _gameEnded.value = false
    }
    fun clearAllWordLists() {
        // Очищаем все списки слов
        allWordsBank.clear()
        currentRoundWords.clear()
        usedWords.clear()
        guessedWords.clear()
        notGuessedWords.clear()

        // Очищаем глобальный список
        globalUsedWords.clear()

        // Сбрасываем состояние
        _currentWord.value = null
        _wordText.value = ""
        _wordsExhausted.value = false
    }

    fun startRound() {
        clearRoundState()

        if (allWordsBank.isEmpty()) {
            loadWordsFromFirestore()
        } else {
            prepareRoundWords()
        }

        startCountdownTimer(totalTimeInMillis)
    }

    private fun clearRoundState() {
        guessedWords.clear()
        notGuessedWords.clear()
        _currentWord.value = null
        _wordText.value = ""
        _gameEnded.value = false
        _wordsExhausted.value = false
        timer?.cancel()
    }

    private fun prepareRoundWords() {
        currentRoundWords.clear()
        // Используем globalUsedWords вместо allUsedWords
        val availableWords = allWordsBank.filterNot { globalUsedWords.contains(it) }

        if (availableWords.isEmpty()) {
            _wordsExhausted.value = true
            endGame()
            return
        }

        currentRoundWords.addAll(availableWords.shuffled())
        showNextWord()
    }



    private fun showNextWord() {
        if (currentRoundWords.isEmpty()) {
            prepareRoundWords()
            return
        }

        val nextWord = currentRoundWords.removeAt(0)
        _currentWord.value = nextWord
        _wordText.value = nextWord.first
    }

    private fun loadWordsFromFirestore() {
        viewModelScope.launch {
            allWordsBank.clear()
            usedWords.clear()
            currentRoundWords.clear()

            if (selectedSets.isEmpty()) {
                _wordText.postValue("Не выбрано ни одного набора")
                return@launch
            }

            selectedSets.forEach { categoryName ->
                db.collection("languages")
                    .document(language)
                    .collection("categories")
                    .document(categoryName)
                    .collection("words")
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        querySnapshot.documents.forEach { document ->
                            val original = document.getString("original") ?: ""
                            val translation = document.getString("translation") ?: ""
                            allWordsBank.add(original to translation)
                        }

                        if (allWordsBank.isNotEmpty()) {
                            prepareRoundWords()
                        } else {
                            _wordText.postValue("Нет доступных слов")
                            _wordsExhausted.postValue(true)
                        }
                    }
                    .addOnFailureListener { e ->
                        _wordText.postValue("Ошибка загрузки слов")
                    }
            }
        }
    }

    fun markWord(guessed: Boolean) {
        _currentWord.value?.let { currentWordPair ->
            if (guessed) {
                guessedWords.add(currentWordPair.first)
            } else {
                notGuessedWords.add(currentWordPair.first)
            }
            // Добавляем в глобальный список использованных
            globalUsedWords.add(currentWordPair)
            showNextWord()
        }
    }

    private fun startCountdownTimer(totalTime: Long) {
        timer?.cancel()
        timer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                _timerText.value = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                _timerText.value = "00:00"
                endGame()
            }
        }.start()
    }

    private fun endGame() {
        val currentPlayers = _players.value ?: return
        if (currentPlayers.isEmpty()) return

        val currentPlayer = currentPlayers[currentPlayerIndex]
        val updatedPlayer = currentPlayer.copy(
            guessedWords = (currentPlayer.guessedWords + guessedWords).toMutableList(),
            notGuessedWords = (currentPlayer.notGuessedWords + notGuessedWords).toMutableList(),
            lastRoundGuessed = guessedWords.toMutableList(),
            lastRoundNotGuessed = notGuessedWords.toMutableList(),
            score = currentPlayer.score + guessedWords.size
        )

        _players.value = currentPlayers.toMutableList().apply {
            set(currentPlayerIndex, updatedPlayer)
        }

        // Если слова закончились, сразу переходим к командным результатам
        if (_wordsExhausted.value == true) {
            _gameEnded.value = true
            return
        }

        // Иначе продолжаем обычный процесс
        _gameEnded.value = true
        resetGame()
    }

    fun stopTimer() {
        timer?.cancel()
        _timerText.value = "00:00"
    }

    fun resetGame() {
        allWordsBank.clear()
        currentRoundWords.clear()
        allUsedWords.clear()
        usedWords.clear()
        clearRoundState()
    }
    fun addCurrentWordToMySet() {
        val current = _currentWord.value ?: return
        val userMySetRef = db.collection("languages")
            .document(language)
            .collection("categories")
            .document("MySet")
            .collection("words")

        val wordMap = mapOf(
            "original" to current.first,
            "translation" to current.second
        )

        userMySetRef
            .whereEqualTo("original", current.first)
            .whereEqualTo("translation", current.second)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    userMySetRef.add(wordMap)
                        .addOnSuccessListener {
                            // Успешно добавлено
                        }
                        .addOnFailureListener {
                            _wordText.postValue("Ошибка при добавлении в MySet")
                        }
                }
            }
            .addOnFailureListener {
                _wordText.postValue("Ошибка при проверке MySet")
            }
    }
    private fun updateSelectedSetsText() {
        _selectedSetsText.value = selectedSets.joinToString(", ")
    }

    fun getSelectedSets(): List<String> = selectedSets
    fun getLanguage(): String = language
    fun getCurrentPlayerIndex(): Int = currentPlayerIndex
    fun getTotalTime(): Long = totalTimeInMillis
}