package com.language

import org.junit.Assert.*
import org.junit.Test

class MySetWordsLogicTest {

    @Test
    fun `adding word increases list size`() {
        val wordManager = TestWordManager()
        assertEquals(0, wordManager.wordCount())

        wordManager.addWord("Apple", "Яблоко")
        assertEquals(1, wordManager.wordCount())
    }

    @Test
    fun `removing word decreases list size`() {
        val wordManager = TestWordManager()
        wordManager.addWord("Dog", "Собака")
        wordManager.addWord("Cat", "Кошка")

        wordManager.removeWord(0)
        assertEquals(1, wordManager.wordCount())
    }
}

// Простой класс для тестирования логики работы со словами
class TestWordManager {
    private val words = mutableListOf<Pair<String, String>>()

    fun addWord(word: String, translation: String) {
        words.add(word to translation)
    }

    fun removeWord(position: Int) {
        words.removeAt(position)
    }

    fun wordCount(): Int = words.size
}