package com.language

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.language.models.Player
import com.language.utils.ThemeUtils

class My_set_activity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WordAdapter
    private lateinit var wordList: MutableList<Pair<String, String>>
    private lateinit var selectedLanguage: String
    private lateinit var players: List<Player>
    private val db = FirebaseFirestore.getInstance()

    private val TAG = "MySetActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        setContentView(R.layout.activity_my_set)
        supportActionBar?.hide()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        })

        selectedLanguage = intent.getStringExtra("language") ?: "English"
        players = intent.getParcelableArrayListExtra<Player>("players") ?: emptyList()

        val customSetTitle = findViewById<TextView>(R.id.customSetTitle)
        customSetTitle.text = "Мой набор $selectedLanguage"

        val viewModel = WordsViewModel()
        viewModel.createMySetIfNotExists(selectedLanguage)

        recyclerView = findViewById(R.id.wordsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadWordsFromFirestore()

        val addButton = findViewById<RelativeLayout>(R.id.centerButton)
        addButton.setOnClickListener {
            Log.d(TAG, "Кнопка '+' нажата, показываем всплывающее окно для добавления слова")
            showAddWordPopup()
        }

        val logoutIcon = findViewById<ImageView>(R.id.logoutIcon)
        logoutIcon.setOnClickListener {
            navigateToChooseSet()
        }

        val profileIcon = findViewById<ImageView>(R.id.profileIcon)
        profileIcon.setOnClickListener {
            val intent = Intent(this, PlayersActivity::class.java).apply {
                putParcelableArrayListExtra("players", ArrayList(players))
                putExtra("language", selectedLanguage)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }

        val homeIcon = findViewById<ImageView>(R.id.homeIcon)
        homeIcon.setOnClickListener {
            val intent = Intent(this, ChooseLanguageActivity::class.java).apply {
                putParcelableArrayListExtra("players", ArrayList(players))
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }

    private fun navigateToChooseSet() {
        val intent = Intent(this, ChooseSetActivity::class.java).apply {
            putParcelableArrayListExtra("players", ArrayList(players))
            putExtra("language", selectedLanguage)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val popup = findViewById<RelativeLayout>(R.id.newWordPopup)
            val editPopup = findViewById<RelativeLayout>(R.id.editWordPopup)

            // Обработка попапа добавления слова
            if (popup.visibility == View.VISIBLE) {
                val popupArea = Rect()
                popup.getGlobalVisibleRect(popupArea)

                // Проверяем, было ли нажатие вне попапа
                if (!popupArea.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    popup.visibility = View.GONE
                    return true
                }

                // Получаем границы интерактивных элементов
                val submitButton = findViewById<Button>(R.id.submitButton)
                val wordInput = findViewById<EditText>(R.id.wordInput)
                val translationInput = findViewById<EditText>(R.id.translationInput)

                val buttonArea = Rect()
                val wordInputArea = Rect()
                val translationInputArea = Rect()

                submitButton.getGlobalVisibleRect(buttonArea)
                wordInput.getGlobalVisibleRect(wordInputArea)
                translationInput.getGlobalVisibleRect(translationInputArea)

                // Если нажатие не на интерактивных элементах - блокируем событие
                if (!buttonArea.contains(ev.rawX.toInt(), ev.rawY.toInt()) &&
                    !wordInputArea.contains(ev.rawX.toInt(), ev.rawY.toInt()) &&
                    !translationInputArea.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    return true
                }
            }

            // Обработка попапа редактирования слова
            if (editPopup.visibility == View.VISIBLE) {
                val popupArea = Rect()
                editPopup.getGlobalVisibleRect(popupArea)

                // Проверяем, было ли нажатие вне попапа
                if (!popupArea.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    editPopup.visibility = View.GONE
                    return true
                }

                // Получаем границы интерактивных элементов
                val editSubmitButton = findViewById<Button>(R.id.editSubmitButton)
                val editWordInput = findViewById<EditText>(R.id.editWordInput)
                val editTranslationInput = findViewById<EditText>(R.id.editTranslationInput)

                val buttonArea = Rect()
                val wordInputArea = Rect()
                val translationInputArea = Rect()

                editSubmitButton.getGlobalVisibleRect(buttonArea)
                editWordInput.getGlobalVisibleRect(wordInputArea)
                editTranslationInput.getGlobalVisibleRect(translationInputArea)

                // Если нажатие не на интерактивных элементах - блокируем событие
                if (!buttonArea.contains(ev.rawX.toInt(), ev.rawY.toInt()) &&
                    !wordInputArea.contains(ev.rawX.toInt(), ev.rawY.toInt()) &&
                    !translationInputArea.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    return true
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun loadWordsFromFirestore() {
        Log.d(TAG, "Загружаем слова для языка: $selectedLanguage")

        db.collection("languages")
            .document(selectedLanguage)
            .collection("categories")
            .document("MySet")
            .collection("words")
            .get()
            .addOnSuccessListener { result ->
                wordList = mutableListOf()
                Log.d(TAG, "Загружено слов: ${result.size()}")

                for (document in result) {
                    val word = document.getString("original") ?: ""
                    val translation = document.getString("translation") ?: ""
                    if (word.isNotEmpty() && translation.isNotEmpty()) {
                        wordList.add(word to translation)
                    }
                }

                adapter = WordAdapter(wordList) { position, word, translation ->
                    showEditWordPopup(position, word, translation)
                }
                recyclerView.adapter = adapter

                val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.adapterPosition
                        val (word, translation) = wordList[position]

                        val wordRef = db.collection("languages")
                            .document(selectedLanguage)
                            .collection("categories")
                            .document("MySet")
                            .collection("words")
                            .document(word)

                        wordRef.delete()
                            .addOnSuccessListener {
                                Log.d(TAG, "Слово '$word' удалено из Firestore")
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Ошибка при удалении слова: ${e.message}", e)
                                Toast.makeText(this@My_set_activity, "Ошибка при удалении: ${e.message}", Toast.LENGTH_SHORT).show()
                            }

                        val deletedWord = wordList[position]
                        wordList.removeAt(position)
                        adapter.notifyItemRemoved(position)

                        Snackbar.make(recyclerView, "Слово удалено", Snackbar.LENGTH_LONG)
                            .setAction("Отменить") {
                                wordList.add(position, deletedWord)
                                adapter.notifyItemInserted(position)
                                db.collection("languages")
                                    .document(selectedLanguage)
                                    .collection("categories")
                                    .document("MySet")
                                    .collection("words")
                                    .document(deletedWord.first)
                                    .set(
                                        hashMapOf(
                                            "original" to deletedWord.first,
                                            "translation" to deletedWord.second,
                                            "addedAt" to System.currentTimeMillis()
                                        )
                                    )
                            }
                            .show()
                    }
                })
                itemTouchHelper.attachToRecyclerView(recyclerView)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Ошибка при загрузке слов: ${e.message}", e)
                Toast.makeText(this, "Ошибка при загрузке слов: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAddWordPopup() {
        val newWordPopup = findViewById<RelativeLayout>(R.id.newWordPopup)
        ThemeUtils.applyPopupTheme(this, newWordPopup)
        newWordPopup.visibility = View.VISIBLE

        val submitButton = findViewById<Button>(R.id.submitButton)
        // Устанавливаем цвет фона из lavander_violet
        submitButton.backgroundTintList = ColorStateList.valueOf(getThemeColor(R.attr.lavander_violet))
        // Устанавливаем цвет текста из атрибута text
        submitButton.setTextColor(getThemeColor(R.attr.text))

        findViewById<EditText>(R.id.wordInput).text.clear()
        findViewById<EditText>(R.id.translationInput).text.clear()

        submitButton.setOnClickListener {
            val wordEditText = findViewById<EditText>(R.id.wordInput)
            val translationEditText = findViewById<EditText>(R.id.translationInput)

            val word = wordEditText.text.toString()
            val translation = translationEditText.text.toString()

            if (word.isNotEmpty() && translation.isNotEmpty()) {
                addWordToMySet(word, translation)
                newWordPopup.visibility = View.GONE
            } else {
                Toast.makeText(this, "Пожалуйста, введите слово и перевод", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEditWordPopup(position: Int, oldWord: String, oldTranslation: String) {
        val editPopup = findViewById<RelativeLayout>(R.id.editWordPopup)
        ThemeUtils.applyPopupTheme(this, editPopup)
        editPopup.visibility = View.VISIBLE

        val saveButton = findViewById<Button>(R.id.editSubmitButton)
        // Устанавливаем цвет фона из lavander_violet
        saveButton.backgroundTintList = ColorStateList.valueOf(getThemeColor(R.attr.lavander_violet))
        // Устанавливаем цвет текста из атрибута text
        saveButton.setTextColor(getThemeColor(R.attr.text))

        val wordEditText = findViewById<EditText>(R.id.editWordInput)
        val translationEditText = findViewById<EditText>(R.id.editTranslationInput)

        wordEditText.setText(oldWord)
        translationEditText.setText(oldTranslation)

        saveButton.setOnClickListener {
            val newWord = wordEditText.text.toString()
            val newTranslation = translationEditText.text.toString()

            if (newWord.isNotEmpty() && newTranslation.isNotEmpty()) {
                val wordsRef = db.collection("languages")
                    .document(selectedLanguage)
                    .collection("categories")
                    .document("MySet")
                    .collection("words")

                if (newWord != oldWord) {
                    wordsRef.document(oldWord).delete()
                }

                val newWordData = hashMapOf(
                    "original" to newWord,
                    "translation" to newTranslation,
                    "addedAt" to System.currentTimeMillis()
                )
                wordsRef.document(newWord).set(newWordData)
                    .addOnSuccessListener {
                        wordList[position] = newWord to newTranslation
                        adapter.notifyItemChanged(position)
                        editPopup.visibility = View.GONE
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Ошибка обновления: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Введите слово и перевод", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getThemeColor(attrRes: Int): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue.data
    }



    private fun addWordToMySet(word: String, translation: String) {
        val mySetRef = db.collection("languages")
            .document(selectedLanguage)
            .collection("categories")
            .document("MySet")
            .collection("words")
            .document(word)

        val wordData = hashMapOf(
            "original" to word,
            "translation" to translation,
            "addedAt" to System.currentTimeMillis()
        )

        mySetRef.set(wordData)
            .addOnSuccessListener {
                wordList.add(word to translation)
                adapter.notifyItemInserted(wordList.size - 1)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка при добавлении слова: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}