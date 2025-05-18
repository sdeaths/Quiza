package com.language

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.language.models.Player

class PlayersViewModel : ViewModel() {

    private val _players = MutableLiveData<List<Player>>(listOf())
    val players: LiveData<List<Player>> = _players

    private val availableAvatars = mutableListOf(
        R.drawable.player_icon_1,
        R.drawable.player_icon_2,
        R.drawable.player_icon_3,
        R.drawable.player_icon_4,
        R.drawable.player_icon_5
    )

    fun setPlayers(players: List<Player>) {
        _players.value = players
        // Восстанавливаем доступные аватары
        availableAvatars.clear()
        availableAvatars.addAll(listOf(
            R.drawable.player_icon_1,
            R.drawable.player_icon_2,
            R.drawable.player_icon_3,
            R.drawable.player_icon_4,
            R.drawable.player_icon_5
        ))
        // Удаляем уже использованные аватары
        players.forEach { player ->
            availableAvatars.remove(player.avatar)
        }
    }

    fun addPlayer(name: String, context: Context) {
        val trimmedName = name.trim()

        when {
            trimmedName.isEmpty() -> {
                Toast.makeText(context, "Имя не может быть пустым", Toast.LENGTH_SHORT).show()
                return
            }
            trimmedName.length > Player.MAX_NAME_LENGTH -> {
                Toast.makeText(context, "Максимум ${Player.MAX_NAME_LENGTH} символов", Toast.LENGTH_SHORT).show()
                return
            }
            _players.value?.size ?: 0 >= 5 -> {
                Toast.makeText(context, "Нельзя добавить больше 5 игроков", Toast.LENGTH_SHORT).show()
                return
            }
            availableAvatars.isEmpty() -> {
                Toast.makeText(context, "Все аватары использованы", Toast.LENGTH_SHORT).show()
                return
            }
            else -> {
                val newAvatar = availableAvatars.random()
                availableAvatars.remove(newAvatar)
                val newPlayer = Player(trimmedName, 0, newAvatar)
                _players.value = _players.value?.plus(newPlayer) ?: listOf(newPlayer)
            }
        }
    }

    fun returnAvatar(avatar: Int) {
        if (!availableAvatars.contains(avatar)) {
            availableAvatars.add(avatar)
        }
    }

    fun resetPlayers() {
        _players.value = emptyList()
        availableAvatars.clear()
        availableAvatars.addAll(listOf(
            R.drawable.player_icon_1,
            R.drawable.player_icon_2,
            R.drawable.player_icon_3,
            R.drawable.player_icon_4,
            R.drawable.player_icon_5
        ))
    }
}