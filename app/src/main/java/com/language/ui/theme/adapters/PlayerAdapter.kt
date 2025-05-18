package com.language.ui.theme.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.language.R
import com.language.models.Player

class PlayerAdapter(
    private val players: List<Player>,
    private val onAddPlayerClicked: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_PLAYER = 0
        const val TYPE_ADD_BUTTON = 1
        const val MAX_PLAYERS = 5
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_PLAYER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_existing_player, parent, false)
                PlayerViewHolder(view).apply {
                    if (nameTextView == null || avatarImageView == null) {
                        throw IllegalStateException("Some views are missing in player_item layout")
                    }
                }
            }
            TYPE_ADD_BUTTON -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_player, parent, false)
                AddPlayerViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PlayerViewHolder -> {
                val player = players[position]
                holder.bind(player)
            }
            is AddPlayerViewHolder -> {
                holder.itemView.setOnClickListener { onAddPlayerClicked() }
            }
        }
    }

    override fun getItemCount(): Int {
        // если достигнут максимум игроков, не показываем кнопку добавления
        return if (players.size >= MAX_PLAYERS) players.size else players.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < players.size) TYPE_PLAYER
        else if (position == players.size && players.size < MAX_PLAYERS) TYPE_ADD_BUTTON
        else throw IllegalArgumentException("Invalid position")
    }

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.player_name)
        val avatarImageView: ImageView = itemView.findViewById(R.id.player_image)

        fun bind(player: Player) {
            nameTextView.text = player.name
            avatarImageView.setImageResource(player.avatar)
        }
    }

    inner class AddPlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}