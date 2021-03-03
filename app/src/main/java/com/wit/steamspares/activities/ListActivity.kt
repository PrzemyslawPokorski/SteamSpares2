package com.wit.steamspares.activities

import android.os.Bundle
import android.view.*
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
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.app_bar.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

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
        setContentView(R.layout.app_bar)
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
                    filter.setOnQueryTextListener(app.gameMemStore)
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