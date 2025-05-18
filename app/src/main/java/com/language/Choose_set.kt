package com.language

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.language.models.Player
import com.language.ui.theme.adapters.WordSetsAdapter
import com.language.utils.ThemeUtils

class ChooseSetActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var wordSetsAdapter: WordSetsAdapter
    private val allWordSets = mutableListOf<String>()
    private val filteredWordSets = mutableListOf<String>()
    private lateinit var confirmButton: View
    private lateinit var selectedLanguage: String
    private lateinit var players: List<Player>

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.set_choose)
        supportActionBar?.hide()

        // Получаем данные из интента
        selectedLanguage = intent.getStringExtra("language") ?: "English"
        players = intent.getParcelableArrayListExtra<Player>("players") ?: emptyList()

        Log.d("ChooseSetActivity", "Received data:")
        Log.d("ChooseSetActivity", "Language: $selectedLanguage")
        Log.d("ChooseSetActivity", "Players: ${players.joinToString { it.name }}")



        initViews()
        setupRecyclerView()
        loadWordSetsFromFirebase(selectedLanguage)
        setupSearch()
        setupNavigation()


    }

    private fun initViews() {
        recyclerView = findViewById(R.id.sets_recycler_view)
        confirmButton = findViewById(R.id.confirm_button)
    }

    private fun setupNavigation() {
        // Обработчик для перехода на ChooseLanguageActivity
        findViewById<View>(R.id.icon_home).setOnClickListener {
            val intent = Intent(this, ChooseLanguageActivity::class.java)
            intent.putParcelableArrayListExtra("players", ArrayList(players))
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Обработчик для перехода на My_set_activity
        findViewById<ImageView>(R.id.icon_folder).setOnClickListener {
            val intent = Intent(this, My_set_activity::class.java).apply {
                putExtra("language", selectedLanguage)
                putParcelableArrayListExtra("players", ArrayList(players))
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Обработчик для перехода на PlayersActivity
        findViewById<View>(R.id.icon_profile).setOnClickListener {
            val intent = Intent(this, PlayersActivity::class.java)
            intent.putParcelableArrayListExtra("players", ArrayList(players))
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun loadWordSetsFromFirebase(language: String) {
        Log.d("ChooseSetActivity", "Loading sets for language: $language")
        val db = FirebaseFirestore.getInstance()
        db.collection("languages").document(language).collection("categories")
            .get()
            .addOnSuccessListener { categoryDocs ->
                allWordSets.clear()

                // Загружаем только категории, без "MySet"
                categoryDocs.forEach { categoryDoc ->
                    allWordSets.add(categoryDoc.id)
                }

                Log.d("ChooseSetActivity", "Loaded ${allWordSets.size} sets")
                filterWordSets("") // После загрузки данных вызываем фильтрацию
            }
            .addOnFailureListener { e ->
                Log.e("ChooseSetActivity", "Error loading categories", e)
                Toast.makeText(this, "Failed to load sets", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupSearch() {
        findViewById<TextView>(R.id.searchField).addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    filterWordSets(s?.toString() ?: "")
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }
        )
    }

    private fun filterWordSets(query: String) {
        filteredWordSets.clear()

        // Добавляем "MySet" только если оно еще не добавлено в список
        filteredWordSets.addAll(
            if (query.isEmpty()) allWordSets
            else allWordSets.filter { it.contains(query, ignoreCase = true) }
        )



        wordSetsAdapter.notifyDataSetChanged()
    }

    private fun showConfirmButton() {
        confirmButton.visibility = if (wordSetsAdapter.isAnySetSelected()) View.VISIBLE else View.GONE
    }

    private fun navigateToChooseTimerActivity() {
        val selectedSets = wordSetsAdapter.getSelectedSets()
        Log.d("ChooseSetActivity", "Selected sets: ${selectedSets.joinToString()}")

        val intent = Intent(this, ChooseTimerActivity::class.java).apply {
            putExtra("language", selectedLanguage)
            putStringArrayListExtra("selectedSets", ArrayList(selectedSets))
            putParcelableArrayListExtra("players", ArrayList(players))
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
    private fun setupRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        wordSetsAdapter = WordSetsAdapter(filteredWordSets) { showConfirmButton() }
        recyclerView.adapter = wordSetsAdapter

        confirmButton.setOnClickListener {
            if (wordSetsAdapter.isAnySetSelected()) {
                navigateToChooseTimerActivity()
            } else {
                Toast.makeText(this, "Please select at least one set", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}
