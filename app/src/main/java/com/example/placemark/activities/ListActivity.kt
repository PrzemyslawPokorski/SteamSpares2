package com.example.placemark.activities

import GameListAdapter
import GameListener
import android.content.Intent
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
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
    SearchView.OnQueryTextListener {

    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_list)
        app = application as MainApp

        toolbar.title = title
        setSupportActionBar(toolbar)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = GameListAdapter(app.placemarks.getUsed(false), this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_steamspares_list, menu)

        val searchItem = menu?.findItem(R.id.app_bar_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(this)

        return super.onCreateOptionsMenu(menu)
    }

    fun filter(){

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        info("Debug: List activity add button clicked")
        when (item.itemId) {
            R.id.item_add -> startActivityForResult<EditActivity>(0)
            R.id.app_bar_search ->{

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onGameClick(game: GameModel) {
//        startActivityForResult(intentFor<EditActivity>().putExtra("placemark_edit", game), 0)
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
        recyclerView.adapter = GameListAdapter(app.placemarks.findAll().filter { it.title.contains(newText.toString(), ignoreCase = true) }, this)
        info { "Debug: Query text changed" }
        return false
    }
}