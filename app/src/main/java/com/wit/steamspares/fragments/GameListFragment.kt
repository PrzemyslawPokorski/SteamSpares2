package com.wit.steamspares.fragments

import GameListAdapter
import GameListener
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.wit.steamspares.R
import com.wit.steamspares.main.MainApp
import com.wit.steamspares.models.GameModel
import kotlinx.android.synthetic.main.card_game.*
import kotlinx.android.synthetic.main.fragment_game_list.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GameListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameListFragment : Fragment(), GameListener, AnkoLogger,
    SearchView.OnQueryTextListener {
    // TODO: Rename and change types of parameters
    private lateinit var app: MainApp
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutManager = LinearLayoutManager(activity?.applicationContext)
        info { "Debug: ${gameRecyclerView} "}
        gameRecyclerView.layoutManager = layoutManager
        gameRecyclerView.adapter = GameListAdapter(app!!.gameMemStore.findAll().toMutableList())
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_list, container, false)
    }


//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_steamspares_list, menu)
//        if (menu != null) {
//            filter = menu.findItem(R.id.filter_bar).actionView as SearchView
//            filter.setOnQueryTextListener(this)
//
//            spinner = menu.findItem(R.id.status_spinner).actionView as Spinner
//            supportActionBar?.let {
//                ArrayAdapter.createFromResource(
//                    it.themedContext, R.array.status_options, android.R.layout.simple_spinner_item
//                ).also { adapter ->
//                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
//                    spinner.adapter = adapter
//                    spinner.onItemSelectedListener = this
//                    spinner.setSelection(1)
//                }
//            }
//        }
//
//
//        return super.onCreateOptionsMenu(menu)
//    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        info { "Debug: Context item clicked: ${item.toString()}" }
//        val game = recyclerView.findViewHolderForAdapterPosition(item.groupId)?.itemView?.tag as GameModel
//        when(item.toString()){
//            getString(R.string.edit) -> {
//                startActivityForResult(intentFor<EditActivity>().putExtra("game_edit", game), 0)
//            }
//            getString(R.string.delete) ->{
//                app.gameMemStore.delete(game)
//                refreshView()
//                recyclerView.adapter?.notifyItemRemoved(item.groupId)
//                info { "Debug: ${app.gameMemStore.games.count()}" }
//            }
//            getString(R.string.status_swap) -> {
//                game.status = !game.status
//                app.gameMemStore.update(game.id, game.name, game.code, game.status, game.notes)
//                refreshView()
//                //Had to refresh view manually as well, since notifying the change didn't
//            }
//        }
        return super.onContextItemSelected(item)
    }

    fun filter(){

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        info("Debug: List activity add button clicked")
        when (item.itemId) {
            //TODO: R.id.nav_donate -> navigateTo(DonateFragment.newInstance()) - rework for here
//            R.id.item_add -> startActivityForResult<EditActivity>(0)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onGameClick(game: GameModel) {
//        https://www.youtube.com/watch?v=ehk4jbCjFbc
        info { "Debug: ${gameCardView.id}" }
        TransitionManager.beginDelayedTransition(gameCardView, AutoTransition())
        if(expandableCard.visibility == View.GONE)
            expandableCard.visibility = View.VISIBLE
        else
            expandableCard.visibility = View.GONE
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        info { "Debug: Query submitted" }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        gameRecyclerView.adapter = GameListAdapter(app.gameMemStore.getFiltered(newText.toString()).toMutableList())
        info { "Debug: Query text changed" }
        return false
    }

//    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        val status = spinner.selectedItem.toString()
//        refreshView(status)
//    }

//    fun refreshView(status: String = spinner.selectedItem.toString()){
//        info { "Debug: Refreshing" }
////        when (status){
////            "All" -> recyclerView.adapter = GameListAdapter(app.gameMemStore.getFiltered(filter.query.toString()).toMutableList(), this)
////            "Used" -> recyclerView.adapter = GameListAdapter(app.gameMemStore.getFiltered(filter.query.toString(), true).toMutableList(), this)
////            "Unused" -> recyclerView.adapter = GameListAdapter(app.gameMemStore.getFiltered(filter.query.toString(), false).toMutableList(), this)
////        }
//    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GameListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(app: MainApp) =
            GameListFragment().apply {
                arguments = Bundle().apply {

                }
                //TODO: Is this ok?
                this.app = app
            }
    }
}