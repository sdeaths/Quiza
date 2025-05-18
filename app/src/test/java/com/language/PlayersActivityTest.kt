package com.language

import com.language.models.Player
import com.language.ui.theme.adapters.PlayerAdapter
import org.junit.Test
import org.junit.Assert.*

class PlayerAdapterTest {

    @Test
    fun testPlayerAdapterItemCount() {
        val testPlayers = listOf(
            Player("Player1", 0, 1),
            Player("Player2", 0, 2)
        )

        val adapter = PlayerAdapter(testPlayers, {})

        assertEquals(3, adapter.itemCount)
        assertEquals(PlayerAdapter.TYPE_PLAYER, adapter.getItemViewType(0))
        assertEquals(PlayerAdapter.TYPE_PLAYER, adapter.getItemViewType(1))
        assertEquals(PlayerAdapter.TYPE_ADD_BUTTON, adapter.getItemViewType(2))
    }

    @Test
    fun testPlayerAdapterMaxPlayers() {
        val testPlayers = List(PlayerAdapter.MAX_PLAYERS) {
            Player("Player$it", 0, it + 1)
        }

        val adapter = PlayerAdapter(testPlayers, {})

        assertEquals(PlayerAdapter.MAX_PLAYERS, adapter.itemCount)
    }
}