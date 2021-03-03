package com.wit.steamspares.fragments

import GameListAdapter
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.wit.steamspares.R
import com.wit.steamspares.activities.Home
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

        //Using shared view model properly broke some things in the process - might redo slightly later
        gameMemStore.filterQuery.observe(this, Observer {
            val query = it
            info { "Debug: Filter updated to $it" }
            val filteredList = gameMemStore.gamesLD.value!!.filter {
                (it.name.contains(query, ignoreCase = true) || it.notes?.contains(query, ignoreCase = true) ?: true) &&
                        it.status == this.usedStatus
            }
            gameList.clear()
            gameList.addAll(filteredList)
            info { "Debug: Filtered list item count: ${filteredList.count()} vs gameList count: ${gameList.count()}" }
            adapter.notifyDataSetChanged()
        })

        gameMemStore.gamesLD!!.observe(this, Observer {
            info { "Debug: Observer fired" }
            gameList.clear()
            gameList.addAll(it.filter {
                it.status == this.usedStatus
            })
            adapter.notifyDataSetChanged()
        })
    }

    override fun onPause() {
        super.onPause()
        (activity as Home).hideKeyboard(activity as Home)
    }

    override fun onResume() {
        super.onResume()
        (activity as Home).hideKeyboard((activity as Home))
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
        info { "Debug: onActivityCreated - gamelist fragment "}
        gameRecyclerView.layoutManager = LinearLayoutManager(context)
        gameRecyclerView.adapter = adapter
        (activity as Home).askForMenu(Home.MenuType.LIST)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        info { "Debug: Context item clicked: ${item.toString()}" }
        val game = gameRecyclerView.findViewHolderForAdapterPosition(item.groupId)?.itemView?.tag as GameModel
        when(item.toString()){
            getString(R.string.edit) -> {
                (activity as Home).navigateTo(EditGameFragment.newInstance(gameMemStore, game))
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
                //TODO: Is this ok?
                this.gameMemStore = memStore
                this.adapter = GameListAdapter(gameList)
                this.usedStatus = usedStatus
            }
    }
}