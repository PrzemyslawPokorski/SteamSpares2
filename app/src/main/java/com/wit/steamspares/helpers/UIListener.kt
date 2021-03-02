package com.wit.steamspares.helpers
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.AdapterView
import android.widget.SearchView
import com.wit.steamspares.models.GameModel
import kotlinx.android.synthetic.main.card_game.*
import kotlinx.android.synthetic.main.fragment_game_list.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class UIListener : AnkoLogger, SearchView.OnQueryTextListener, AdapterView.OnItemSelectedListener {
    //SEARCH QUERY
    override fun onQueryTextSubmit(query: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        //TODO: update/refresh recycle views
        //gameRecyclerView.adapter = GameListAdapter(app.gameMemStore.getFiltered(newText.toString()).toMutableList())
        info { "Debug: Query text changed" }
        return false
    }

    //FILTERING SPINNER (Soon to be obsolete?)
    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //TODO Used to use spinner - soon to be obsolete?
//        val status = spinner.selectedItem.toString()
//        refreshView(status)
    }

    //TODO: Soon to be obsolete?
    fun refreshView(status: String){
        info { "Debug: Refreshing" }
        when (status){
//            "All" -> gameRecyclerView.adapter = GameListAdapter(app.gameMemStore.getFiltered(filter.query.toString()).toMutableList())
//            "Used" -> gameRecyclerView.adapter = GameListAdapter(app.gameMemStore.getFiltered(filter.query.toString(), true).toMutableList())
//            "Unused" -> gameRecyclerView.adapter = GameListAdapter(app.gameMemStore.getFiltered(filter.query.toString(), false).toMutableList())
        }
    }
}