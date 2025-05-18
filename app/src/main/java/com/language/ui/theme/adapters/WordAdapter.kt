package com.language

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WordAdapter(
    private val wordList: MutableList<Pair<String, String>>,
    private val onItemClick: (position: Int, word: String, translation: String) -> Unit
) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textForeignWord: TextView = view.findViewById(R.id.textForeignWord)
        val textTranslation: TextView = view.findViewById(R.id.textTranslation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val (foreignWord, translation) = wordList[position]
        holder.textForeignWord.text = foreignWord
        holder.textTranslation.text = translation

        holder.itemView.setOnClickListener {
            onItemClick(position, foreignWord, translation)
        }
    }

    override fun getItemCount(): Int = wordList.size
}

