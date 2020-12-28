package com.example.steamspares.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.steamspares.R
import com.example.steamspares.main.MainApp
import kotlinx.android.synthetic.main.activity_game.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import com.example.steamspares.models.GameModel
import kotlinx.android.synthetic.main.activity_game.gameTitle
import org.jetbrains.anko.startActivityForResult

class EditActivity : AppCompatActivity(), AnkoLogger {
    val IMAGE_REQUEST = 1
    lateinit var app: MainApp
    lateinit var spinner: Spinner
    var game = GameModel(appid = 0, code = "NULL", status = false)

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

        toolbarAdd.title = title
        setSupportActionBar(toolbarAdd)

        if (intent.hasExtra("game_edit")) {
            game = intent.extras?.getParcelable<GameModel>("game_edit")!!
            gameTitle.setText(game.name)
            gameCode.setText(game.code)
            btnAdd.setText(R.string.button_saveGame)
            editing = true
        }

        info("Debug: Placemark activity started")

        btnAdd.setOnClickListener(){
            game.name = gameTitle.text.toString()
            game.code = gameCode.text.toString()
            game.status = spinner.selectedItem.toString().equals("Used", ignoreCase = true)
            game.notes = gameNotes.text.toString()

            if(game.name.isNotEmpty() && game.code.isNotEmpty()){
                if(!editing)
                    app.gameMemStore.create(game)
                else
                    app.gameMemStore.update(game)

                toast("Add button pressed: ${app.gameMemStore}")
                setResult(AppCompatActivity.RESULT_OK)

                finish()
            }
            else{
                toast(R.string.empty_name_hint)
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IMAGE_REQUEST -> {
                if (data != null) {
//                    Image no longer included
                }
            }
        }
    }
}