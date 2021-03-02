package com.wit.steamspares.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.wit.steamspares.R
import com.wit.steamspares.main.MainApp
import com.wit.steamspares.models.GameModel
import kotlinx.android.synthetic.main.activity_game.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast


class EditActivity : AppCompatActivity(), AnkoLogger {
    lateinit var app: MainApp
    lateinit var spinner: Spinner
    lateinit var gameNames: ArrayList<String>
    var editingGameId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        var editing = false
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        app = application as MainApp

        spinner = gameStatus
        ArrayAdapter.createFromResource(
            this, R.array.new_status_options, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        gameNames = ArrayList<String>()
        app.gameMemStore.steamList.forEach{
            gameNames.add(it.name)
        }
        gameNames.sort()

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line, gameNames
        )
        gameTitle.setAdapter(adapter)

        toolbarAdd.title = title
        setSupportActionBar(toolbarAdd)

        if (intent.hasExtra("game_edit")) {
            val gameToEdit = intent.extras?.getParcelable<GameModel>("game_edit")!!
            gameTitle.setText(gameToEdit.name)
            gameCode.setText(gameToEdit.code)
            btnAdd.setText(R.string.button_saveGame)
            spinner.setSelection(if(gameToEdit.status) 1 else 0)
            editingGameId = gameToEdit.id
            editing = true
        }

        info("Debug: Placemark activity started")

        btnAdd.setOnClickListener(){
            var name = gameTitle.text.toString().trim()
            var code = gameCode.text.toString().trim()
            var status = spinner.selectedItem.toString().equals("Used", ignoreCase = true)
            var notes = gameNotes.text.toString().trim()
            val keycodePattern = """^[\w\d]{5}(-[\w\d]{5}){2}((-[\w\d]{5}){2})?${'$'}""".toRegex()

            if(code.isNotEmpty() && code.matches(keycodePattern)) {
                if (name.isNotEmpty() && code.isNotEmpty()) {
                    if (!editing)
                        app.gameMemStore.create(name, code, status, notes)
                    else
                        app.gameMemStore.update(editingGameId, name, code, status, notes)

                    setResult(AppCompatActivity.RESULT_OK)

                    finish()
                } else {
                    toast(R.string.empty_name_hint)
                }
            }
            else
                toast(R.string.bad_code_hint)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_steamspares_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_cancel -> startActivityForResult<ListActivity>(0)
        }
        return super.onOptionsItemSelected(item)
    }
}