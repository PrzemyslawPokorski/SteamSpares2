package com.example.placemark.activities

import GameListAdapter
import GameListener
import android.content.Intent
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.placemark.R
import com.example.placemark.main.MainApp
import com.example.placemark.models.GameModel
import kotlinx.android.synthetic.main.activity_game_list.*
import kotlinx.android.synthetic.main.card_game.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivityForResult

class ListActivity : AppCompatActivity(), AnkoLogger, GameListener,
    SearchView.OnQueryTextListener, AdapterView.OnItemSelectedListener {

    lateinit var app: MainApp
    lateinit var spinner : Spinner
    lateinit var filter : SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_list)
        app = application as MainApp

        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = GameListAdapter(app.gameMemStore.findAll(), this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_steamspares_list, menu)
        if (menu != null) {
            filter = menu.findItem(R.id.filter_bar).actionView as SearchView
            filter.setOnQueryTextListener(this)

            spinner = menu.findItem(R.id.status_spinner).actionView as Spinner
            supportActionBar?.let {
                ArrayAdapter.createFromResource(
                    it.themedContext, R.array.status_options, android.R.layout.simple_spinner_item
                ).also { adapter ->
                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                    spinner.onItemSelectedListener = this
                }
            }
        }


        return super.onCreateOptionsMenu(menu)
    }

    fun filter(){

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        info("Debug: List activity add button clicked")
        when (item.itemId) {
            R.id.item_add -> startActivityForResult<EditActivity>(0)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onGameClick(game: GameModel) {
//        startActivityForResult(intentFor<EditActivity>().putExtra("game_edit", game), 0)

//        https://www.youtube.com/watch?v=ehk4jbCjFbc
        info { "Debug: ${gameCardView.id}" }
        TransitionManager.beginDelayedTransition(gameCardView, AutoTransition())
        if(expandableCard.visibility == View.GONE)
            expandableCard.visibility = View.VISIBLE
        else
            expandableCard.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        recyclerView.adapter?.notifyDataSetChanged()
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        info { "Debug: Query submitted" }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        recyclerView.adapter = GameListAdapter(app.gameMemStore.getFiltered(newText.toString()), this)
        info { "Debug: Query text changed" }
        return false
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val status = spinner.selectedItem.toString()
        when (status){
            "All" -> recyclerView.adapter = GameListAdapter(app.gameMemStore.getFiltered(filter.query.toString()), this)
            "Used" -> recyclerView.adapter = GameListAdapter(app.gameMemStore.getFiltered(filter.query.toString(), true), this)
            "Unused" -> recyclerView.adapter = GameListAdapter(app.gameMemStore.getFiltered(filter.query.toString(), false), this)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
//        TODO("Not yet implemented")
    }
}