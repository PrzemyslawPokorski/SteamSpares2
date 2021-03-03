package com.wit.steamspares.activities

import GameListAdapter
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.wit.steamspares.R
import com.wit.steamspares.fragments.EditGameFragment
import com.wit.steamspares.fragments.GameListFragment
import com.wit.steamspares.helpers.UIListener
import com.wit.steamspares.main.MainApp
import com.wit.steamspares.models.GameModel
import kotlinx.android.synthetic.main.activity_game_list.*
import kotlinx.android.synthetic.main.card_game.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivityForResult

class ListActivity : AppCompatActivity(), AnkoLogger{
    enum class MenuType{
        LIST, EDIT
    }

    lateinit var app: MainApp
    lateinit var spinner : Spinner
    lateinit var filter : SearchView
    lateinit var fragmentTransaction : FragmentTransaction
    lateinit var topMenu : MenuType

    val listenerHelper = UIListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_list)
        app = application as MainApp
        topMenu = MenuType.LIST

        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        fragmentTransaction = supportFragmentManager.beginTransaction()

        navigateTo(GameListFragment.newInstance(app.gameMemStore, false), addToStack = false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        when(topMenu){
            MenuType.EDIT -> {
                menuInflater.inflate(R.menu.menu_steamspares_add, menu)
            }
            MenuType.LIST -> {
                menuInflater.inflate(R.menu.menu_steamspares_list, menu)

                if (menu != null) {
                    filter = menu.findItem(R.id.filter_bar).actionView as SearchView
                    filter.setOnQueryTextListener(listenerHelper)
                }
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                navigateTo(EditGameFragment.newInstance(app.gameMemStore))
            }
            R.id.action_cancel ->{
                supportFragmentManager.popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun navigateTo(fragment: Fragment, addToStack: Boolean = true) {
        val ft = supportFragmentManager.beginTransaction()
            .replace(R.id.mainAppFrame, fragment)
        if(addToStack)
            ft.addToBackStack(null)
        ft.commit()
    }

    fun askForMenu(menuType: MenuType){
        topMenu = menuType
        info { "Debug: Navigate asked for menu $menuType" }
        invalidateOptionsMenu()
    }
}