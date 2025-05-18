package com.language

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.language.utils.ThemeUtils

class RulesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        // Привязываем разметку activity_rules.xml
        setContentView(R.layout.rules)
    }
}
