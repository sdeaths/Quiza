package com.language.utils

object WordBank {

    private val allWords = mutableListOf<String>()
    private val usedWords = mutableSetOf<String>()

    fun initialize(words: List<String>) {
        allWords.clear()
        allWords.addAll(words)
        usedWords.clear()
    }

    fun getNextWord(): String? {
        val remainingWords = allWords.filter { it !in usedWords }
        if (remainingWords.isEmpty()) return null

        val next = remainingWords.random()
        usedWords.add(next)
        return next
    }

    fun hasWordsRemaining(): Boolean {
        return allWords.any { it !in usedWords }
    }

    fun getRemainingWordsCount(): Int {
        return allWords.count { it !in usedWords }
    }

    fun reset() {
        usedWords.clear()
    }

    fun getUsedWords(): List<String> = usedWords.toList()
}
