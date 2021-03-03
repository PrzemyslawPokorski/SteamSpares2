package com.wit.steamspares.fragments

import GameListAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.wit.steamspares.R
import com.wit.steamspares.activities.ListActivity
import com.wit.steamspares.models.GameMemStore
import com.wit.steamspares.models.GameModel
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
class GameListFragment : Fragment(), AnkoLogger {
    private lateinit var adapter: GameListAdapter
    private lateinit var gameMemStore: GameMemStore
    private var gameList = ArrayList<GameModel>()
    private var usedStatus: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameMemStore.gamesLD.observe(viewLifecycleOwner, Observer {
            info { "Debug: Observer fired" }
            gameList.clear()
            gameList.addAll(it)
            adapter.notifyDataSetChanged()
        })

//        gameMemStore.filterQuery.observe(viewLifecycleOwner, Observer {query ->
//            info { "Debug: filter change detected" }
//            gameList.filter {
//                info { "Debug: Filtering to contain $query" }
//                it.name.contains(query, ignoreCase = true) ||
//                        it.notes?.contains(query, ignoreCase = true) ?: true
//            }
//            info { "Debug: Filtered list contains ${gameList.count()} elements" }
//            adapter.notifyDataSetChanged()
//        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        info { "Debug: onActivityCreated - gamelist fragment for $usedStatus "}
        gameRecyclerView.layoutManager = LinearLayoutManager(context)
        gameRecyclerView.adapter = adapter
        (activity as ListActivity).askForMenu(ListActivity.MenuType.LIST)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        info { "Debug: Context item clicked: ${item.toString()}" }
        val game = gameRecyclerView.findViewHolderForAdapterPosition(item.groupId)?.itemView?.tag as GameModel
        when(item.toString()){
            getString(R.string.edit) -> {
                (activity as ListActivity).navigateTo(EditGameFragment.newInstance(gameMemStore, game))
            }
            getString(R.string.delete) ->{
                gameMemStore.delete(game)
            }
            getString(R.string.status_swap) -> {
                game.status = !game.status
                gameMemStore.update(game.id, game.name, game.code, game.status, game.notes)
            }
        }
        return super.onContextItemSelected(item)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GameListFragment.
         */
        @JvmStatic
        fun newInstance(memStore: GameMemStore, usedStatus : Boolean) =
            GameListFragment().apply {
                arguments = Bundle().apply {

                }
                this.gameMemStore = memStore
                this.adapter = GameListAdapter(gameList)
                this.usedStatus = usedStatus
            }
    }
}