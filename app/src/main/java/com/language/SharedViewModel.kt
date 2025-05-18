package com.language.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.language.models.Player

class SharedViewModel : ViewModel() {
    private val _players = MutableLiveData<List<Player>>(emptyList())
    val players: LiveData<List<Player>> get() = _players

    fun setPlayers(players: List<Player>) {
        _players.value = players
    }
}
