package com.language.ui.theme.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.language.R
import kotlin.collections.mutableSetOf // Добавлен импорт для mutableSet

class WordSetsAdapter(
    private var wordSets: List<String>,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<WordSetsAdapter.WordSetViewHolder>() {

    private val selectedSets = mutableSetOf<String>() // Исправлено на mutableSetOf

    init {
        Log.d("WordSetsAdapter", "Adapter initialized with ${wordSets.size} sets")
    }

    inner class WordSetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val wordSetName: TextView = itemView.findViewById(R.id.setNameTextView)

        fun bind(wordSet: String) {
            wordSetName.text = wordSet
            itemView.isSelected = selectedSets.contains(wordSet)

            itemView.setOnClickListener {
                val wasSelected = selectedSets.contains(wordSet)
                if (wasSelected) {
                    selectedSets.remove(wordSet)
                    Log.d("WordSetsAdapter", "Deselected: $wordSet")
                } else {
                    selectedSets.add(wordSet)
                    Log.d("WordSetsAdapter", "Selected: $wordSet")
                }
                itemView.isSelected = !wasSelected
                onSelectionChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordSetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_word_set, parent, false)
        return WordSetViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordSetViewHolder, position: Int) {
        try {
            holder.bind(wordSets[position])
        } catch (e: Exception) {
            Log.e("WordSetsAdapter", "Error binding view holder", e)
        }
    }

    override fun getItemCount(): Int = wordSets.size

    fun isAnySetSelected(): Boolean = selectedSets.isNotEmpty()

    fun getSelectedSets(): List<String> {
        Log.d("WordSetsAdapter", "Getting selected sets: ${selectedSets.toList()}")
        val selected = selectedSets.toList()
        if (selected.isEmpty()) {
            Log.w("WordSetsAdapter", "No sets selected!")
        }
        return selected
    }

    fun updateData(newWordSets: List<String>) {
        wordSets = newWordSets
        notifyDataSetChanged()
        Log.d("WordSetsAdapter", "Data updated, now ${wordSets.size} sets")
    }
}