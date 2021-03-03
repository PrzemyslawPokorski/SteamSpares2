package com.wit.steamspares.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.wit.steamspares.R
import com.wit.steamspares.activities.Home
import com.wit.steamspares.models.GameMemStore
import com.wit.steamspares.models.GameModel
import kotlinx.android.synthetic.main.activity_game.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditGameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditGameFragment : Fragment(), AnkoLogger {
    // TODO: Rename and change types of parameters
    private var gameToEdit: GameModel? = null
    private var editing : Boolean = false

    lateinit var memstore: GameMemStore
    lateinit var spinner: Spinner
    lateinit var gameNames: ArrayList<String>
    var editingGameId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        gameNames = ArrayList<String>()
        memstore.steamList.forEach{
            gameNames.add(it.name)
        }
        gameNames.sort()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.title = getString(R.string.edit_fragment)
        return inflater.inflate(R.layout.fragment_edit_game, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as Home).askForMenu(Home.MenuType.EDIT)
        info { "Debug: onActivityCreated - edit fragment "}

        spinner = gameStatus
        context?.let {
            ArrayAdapter.createFromResource(
                it, R.array.new_status_options, android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
        }

        if (gameToEdit != null) {
            gameTitle.setText(gameToEdit!!.name)
            gameCode.setText(gameToEdit!!.code)
            btnAdd.setText(R.string.button_saveGame)
            spinner.setSelection(if(gameToEdit!!.status) 1 else 0)
            editingGameId = gameToEdit!!.id
            editing = true
        }

        btnAdd.setOnClickListener(){
            var name = gameTitle.text.toString().trim()
            var code = gameCode.text.toString().trim()
            var status = spinner.selectedItem.toString().equals("Used", ignoreCase = true)
            var notes = gameNotes.text.toString().trim()
            val keycodePattern = """^[\w\d]{5}(-[\w\d]{5}){2}((-[\w\d]{5}){2})?${'$'}""".toRegex()

            if(code.isNotEmpty() && code.matches(keycodePattern)) {
                if (name.isNotEmpty() && code.isNotEmpty()) {
                    if (!editing)
                        memstore.create(name, code, status, notes)
                    else
                        memstore.update(editingGameId, name, code, status, notes)

                    fragmentManager?.popBackStack()

                } else {
                    activity?.toast(R.string.empty_name_hint)
                }
            }
            else
                activity?.toast(R.string.bad_code_hint)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddGameFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(gameMemStore: GameMemStore, game : GameModel? = null) =
            EditGameFragment().apply {
                arguments = Bundle().apply {
                }
                this.memstore = gameMemStore
                this.gameToEdit = game
            }
    }
}