package com.language

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.language.ui.theme.adapters.PlayerAdapter
import com.language.models.Player
import com.language.utils.ThemeUtils

class PlayersActivity : AppCompatActivity() {

    lateinit var viewModel: PlayersViewModel
    lateinit var adapter: PlayerAdapter

    public override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players)
        supportActionBar?.hide()

        viewModel = ViewModelProvider(this).get(PlayersViewModel::class.java)

        // Восстановление состояния после перезапуска
        if (savedInstanceState != null) {
            val savedPlayers = savedInstanceState.getParcelableArrayList<Player>("players")
            savedPlayers?.let { viewModel.setPlayers(it) }
        } else {
            val incomingPlayers = intent.getParcelableArrayListExtra<Player>("players")
            incomingPlayers?.let { viewModel.setPlayers(it) }
        }

        setupRecyclerView()
        setupSwipeToDelete()
        setupNextButton()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.players.value?.let {
            outState.putParcelableArrayList("players", ArrayList(it))
        }
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.players_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.players.observe(this) { players ->
            adapter = PlayerAdapter(players) { showAddPlayerDialog() }
            recyclerView.adapter = adapter
        }
    }

    fun setupSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val currentPlayers = viewModel.players.value.orEmpty().toMutableList()

                if (position < currentPlayers.size) {
                    val removedPlayer = currentPlayers.removeAt(position)
                    viewModel.returnAvatar(removedPlayer.avatar)
                    viewModel.setPlayers(currentPlayers)
                } else {
                    adapter.notifyItemChanged(position)
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(findViewById(R.id.players_recycler))
    }
    override fun onBackPressed() {
        super.onBackPressed() // завершит активити, но не запустит MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }




    fun setupNextButton() {
        findViewById<TextView>(R.id.next_button).setOnClickListener {
            viewModel.players.value?.let { players ->
                if (players.isEmpty()) {
                    Toast.makeText(this, "Добавьте хотя бы одного игрока", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(this, ChooseLanguageActivity::class.java).apply {
                        putParcelableArrayListExtra("players", ArrayList(players))
                    }
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                }
            }
        }
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    fun showAddPlayerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_player, null)
        val editText = dialogView.findViewById<EditText>(R.id.name_input).apply {
            filters = arrayOf(InputFilter.LengthFilter(Player.MAX_NAME_LENGTH))
        }

        val dialog = AlertDialog.Builder(this, R.style.CustomDialogStyle)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Показать клавиатуру при открытии диалога
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        editText.requestFocus()

        dialogView.findViewById<RelativeLayout>(R.id.confirm_button).setOnClickListener {
            val name = editText.text.toString().trim()
            if (name.isNotEmpty()) {
                viewModel.addPlayer(name, this)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Имя не может быть пустым", Toast.LENGTH_SHORT).show()
            }
        }


        dialog.show()
    }

}