package com.wit.steamspares.fragments

import GameListAdapter
import GameListener
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import androidx.transition.TransitionInflater
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.wit.steamspares.R
import com.wit.steamspares.activities.Home
import com.wit.steamspares.main.MainApp
import com.wit.steamspares.models.GameMemStore
import com.wit.steamspares.models.GameModel
import kotlinx.android.synthetic.main.fragment_game_list.*
import kotlinx.android.synthetic.main.fragment_game_list.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


/**
 * A simple [Fragment] subclass.
 * Use the [GameListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameListFragment : Fragment(), AnkoLogger, GameListener {
    private lateinit var adapter: GameListAdapter
    private var gameList = ArrayList<GameModel>()
    private var usedStatus: Boolean = false
    lateinit var root: View
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

        arguments?.let {
            if(savedInstanceState != null){
                val bundleStatus = it.getBoolean("usedStatus")
                usedStatus = bundleStatus
                info { "Debug2 bundle status saved for $bundleStatus" }
            }
            else{
                info { "Debug2 nothing saved in instance" }
            }
        }

        info { "Debug2 created fragment for status $usedStatus" }
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
        root = inflater.inflate(R.layout.fragment_game_list, container, false)

        root.gameRecyclerView.setLayoutManager(LinearLayoutManager(context))

        var query = FirebaseDatabase.getInstance()
            .reference
            .child("users").child(app.currentUser.uid)

        var options = FirebaseRecyclerOptions.Builder<GameModel>()
            .setQuery(query, GameModel::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = GameListAdapter(options, this)
        root.gameRecyclerView.adapter = adapter

        //Using shared view model properly broke some things in the process - might redo slightly later
        GameMemStore.gamesLD!!.observe(viewLifecycleOwner, Observer {
            info { "Debug: Observer fired" }
            gameList.clear()
            gameList.addAll(it.filter {
                it.status == this.usedStatus
            })
            adapter.notifyDataSetChanged()
        })
        GameMemStore.filterQuery.observe(viewLifecycleOwner, Observer {
            val query = it
            info { "Debug: Filter updated to $it" }
            val filteredList = GameMemStore.gamesLD.value!!.filter {
                (it.name.contains(query, ignoreCase = true) || it.notes?.contains(query, ignoreCase = true) ?: true) &&
                        it.status == this.usedStatus
            }
            gameList.clear()
            gameList.addAll(filteredList)
            info { "Debug: Filtered list item count: ${filteredList.count()} vs gameList count: ${gameList.count()}" }
            adapter.notifyDataSetChanged()
        })

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        info { "Debug: onActivityCreated - gamelist fragment "}

        val menuRequest = if(usedStatus) Home.MenuType.LIST_USED else Home.MenuType.LIST_UNUSED
        (activity as Home).askForMenu(menuRequest)

        val mOnItemTouchListener: OnItemTouchListener = object : OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_DOWN && rv.scrollState == RecyclerView.SCROLL_STATE_SETTLING) {
                    info { "Debug touch intercept event" }
                    rv.findChildViewUnder(e.x, e.y)!!.performClick()
                    return true
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                info { "Debug touch event" }
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        }

        gameRecyclerView.addOnItemTouchListener(mOnItemTouchListener)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        info { "Debug: Context item clicked: ${item.toString()}" }
        val game = gameRecyclerView.findViewHolderForAdapterPosition(item.groupId)?.itemView?.tag as GameModel
        when(item.toString()){
            getString(R.string.edit) -> {
                (activity as Home).navigateTo(EditGameFragment.newInstance(GameMemStore, game))
            }
            getString(R.string.delete) ->{
                GameMemStore.delete(game)
            }
            getString(R.string.status_swap) -> {
                game.status = !game.status
                GameMemStore.update(game.id, game.name, game.code, game.status, game.notes)
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
        fun newInstance(memStore: GameMemStore, status : Boolean) =
            GameListFragment().apply {
                arguments = Bundle().apply {
//                    gameMemStore = memStore
                    this.putBoolean("usedStatus", status)
                    usedStatus = status
                }
            }
    }

    override fun onGameClick(game: GameModel) {
        info { "Debug5: onGameClick from GameListFragment listener launched - did it overwrite default behaviour?" }
    }
}