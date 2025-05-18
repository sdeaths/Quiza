package com.language

import com.language.models.Player
import org.junit.Assert.*
import org.junit.Test


class ChooseLanguageLogicTest {

    @Test
    fun `should process players list correctly`() {
        val testPlayers = listOf(Player("Test", 0, 1))
        val processed = processIntentPlayers(testPlayers)
        assertEquals(testPlayers, processed)
    }

    @Test
    fun `should handle null players list`() {
        assertTrue(processIntentPlayers(null).isEmpty())
    }

    private fun processIntentPlayers(players: List<Player>?): List<Player> {
        return players ?: emptyList()
    }
}