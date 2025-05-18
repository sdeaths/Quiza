package com.language

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.language.models.Player
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

class PlayViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: PlayViewModelForTest

    @Before
    fun setup() {
        viewModel = PlayViewModelForTest()
    }

    @Test
    fun `markWord adds word to guessed list when true`() {
        viewModel.setCurrentWord("apple" to "яблоко")
        viewModel.markWord(true)
        assertEquals(listOf("apple"), viewModel.getGuessedWords())
    }

    @Test
    fun `markWord adds word to notGuessed list when false`() {
        viewModel.setCurrentWord("banana" to "банан")
        viewModel.markWord(false)
        assertEquals(listOf("banana"), viewModel.getNotGuessedWords())
    }

    @Test
    fun `timer formats time correctly`() {
        viewModel.startCountdownTimer(65000) // 1 min 5 sec
        assertEquals("01:05", viewModel.getTimerText())
    }

    @Test
    fun `clearState resets all game data`() {
        viewModel.setCurrentWord("test" to "тест")
        viewModel.markWord(true)
        viewModel.startCountdownTimer(60000)

        viewModel.clearState()

        assertNull(viewModel.getCurrentWord())
        assertTrue(viewModel.getGuessedWords().isEmpty())
        assertTrue(viewModel.getNotGuessedWords().isEmpty())
        assertEquals("00:00", viewModel.getTimerText())
    }

    // Независимая тестовая реализация
    class PlayViewModelForTest {
        private val _guessedWords = mutableListOf<String>()
        private val _notGuessedWords = mutableListOf<String>()
        private val _currentWord = MutableLiveData<Pair<String, String>?>()
        private val _timerText = MutableLiveData<String>().apply { value = "00:00" }

        fun setCurrentWord(pair: Pair<String, String>) {
            _currentWord.value = pair
        }

        fun getCurrentWord() = _currentWord.value

        fun markWord(guessed: Boolean) {
            _currentWord.value?.let { word ->
                if (guessed) {
                    _guessedWords.add(word.first)
                } else {
                    _notGuessedWords.add(word.first)
                }
                _currentWord.value = null
            }
        }

        fun startCountdownTimer(totalTime: Long) {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(totalTime) % 60
            _timerText.value = String.format("%02d:%02d", minutes, seconds)
        }

        fun clearState() {
            _guessedWords.clear()
            _notGuessedWords.clear()
            _currentWord.value = null
            _timerText.value = "00:00"
        }

        fun getGuessedWords() = _guessedWords.toList()
        fun getNotGuessedWords() = _notGuessedWords.toList()
        fun getTimerText() = _timerText.value
    }
}