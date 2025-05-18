package com.language

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import android.widget.FrameLayout
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.language.models.Player
import com.language.utils.ThemeUtils
import com.language.viewmodel.SharedViewModel


class ChooseLanguageActivity : AppCompatActivity() {

    private lateinit var viewModel: SharedViewModel
    private lateinit var players: List<Player>

    fun getPlayers(): List<Player> = players


    public override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.language_choose)
        ThemeUtils.updateThemeIcons(this)
        supportActionBar?.hide()

        // Получаем список игроков из интента
        players = intent.getParcelableArrayListExtra<Player>("players") ?: emptyList()
        Log.d("ChooseLanguage", "Received ${players.size} players")

        viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        // если игроки переданы, сохраняем их в ViewModel
        if (players.isNotEmpty()) {
            viewModel.setPlayers(players)
        }

        // настройка кнопок для выбора языка
        val englishButton = findViewById<FrameLayout>(R.id.english_button)
        val frenchButton = findViewById<FrameLayout>(R.id.french_button)
        val germanButton = findViewById<FrameLayout>(R.id.german_button)

        // Обработчики кликов для кнопок языка
        englishButton.setOnClickListener {
            navigateToChooseSetActivity("English")
        }

        frenchButton.setOnClickListener {
            navigateToChooseSetActivity("French")
        }

        germanButton.setOnClickListener {
            navigateToChooseSetActivity("German")
        }

        // Обработчик клика по иконке профиля
        findViewById<View>(R.id.icon_profile).setOnClickListener {
            val intent = Intent(this, PlayersActivity::class.java)
            intent.putParcelableArrayListExtra("players", ArrayList(players))
            startActivity(intent)
        }
        // Отключаем действие системной кнопки "назад"
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Пустое тело - кнопка "назад" ничего не делает
            }
        })

    }

    fun navigateToChooseSetActivity(language: String) {
        Log.d("ChooseLanguage", "Passing ${players.size} players to ChooseSetActivity")
        val intent = Intent(this, ChooseSetActivity::class.java).apply {
            putExtra("language", language)
            putParcelableArrayListExtra("players", ArrayList(players))
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

}
