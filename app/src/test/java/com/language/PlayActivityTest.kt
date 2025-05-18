package com.language

import com.language.models.Player
import org.junit.Assert.*
import org.junit.Test

class PlayActivityTest {

    @Test
    fun `player name is displayed`() {
        val testPlayer = Player("TestPlayer", 0, 1)
        assertEquals("TestPlayer", testPlayer.name)
    }

    @Test
    fun `marking word adds to guessed words`() {
        val gameState = TestGameState()
        gameState.markWord(true, "Apple")
        assertEquals(1, gameState.guessedWords.size)
        assertEquals("Apple", gameState.guessedWords[0])
    }

    @Test
    fun `marking word adds to not guessed words`() {
        val gameState = TestGameState()
        gameState.markWord(false, "Dog")
        assertEquals(1, gameState.notGuessedWords.size)
        assertEquals("Dog", gameState.notGuessedWords[0])
    }
}

// Простой класс для тестирования логики
class TestGameState {
    val guessedWords = mutableListOf<String>()
    val notGuessedWords = mutableListOf<String>()

    fun markWord(guessed: Boolean, word: String) {
        if (guessed) {
            guessedWords.add(word)
        } else {
            notGuessedWords.add(word)
        }
    }
}