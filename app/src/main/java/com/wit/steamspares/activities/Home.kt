package com.wit.steamspares.activities

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Spinner
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.navigation.NavigationView
import com.wit.steamspares.R
import com.wit.steamspares.fragments.EditGameFragment
import com.wit.steamspares.fragments.GameListFragment
import com.wit.steamspares.main.MainApp
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.home.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import java.util.jar.Manifest

class Home : AppCompatActivity(), AnkoLogger, NavigationView.OnNavigationItemSelectedListener {
    enum class MenuType{
        LIST, EDIT
    }

    lateinit var app: MainApp
    lateinit var spinner : Spinner
    lateinit var filter : SearchView
    lateinit var fragmentTransaction : FragmentTransaction
    lateinit var topMenu : MenuType
    lateinit var detector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        app = application as MainApp
        topMenu = MenuType.LIST

        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        detector = GestureDetector(this, GestureListener())

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        info { "Debug: Navbar element: ${navView}" }

        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        info { "Debug: drawerlayout is $drawer" }
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        fragmentTransaction = supportFragmentManager.beginTransaction()

        navigateTo(GameListFragment.newInstance(app.gameMemStore, false), addToStack = false)
    }



    /**
     * Taken from:
     * https://rmirabelle.medium.com/close-hide-the-soft-keyboard-in-android-db1da22b09d2
     */
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        info { "Debug: Hiding keyboard" }
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
        hideKeyboard(this)
    }

    fun askForMenu(menuType: MenuType){
        topMenu = menuType
        info { "Debug: Navigate asked for menu $menuType" }
        invalidateOptionsMenu()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_unused -> navigateTo(GameListFragment.newInstance(app.gameMemStore, false), false)
            R.id.nav_used -> navigateTo(GameListFragment.newInstance(app.gameMemStore, true), false)

            else -> toast("This screen is not yet implemented")
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    //https://www.youtube.com/watch?v=j1aydFEOEA0
    inner class GestureListener : GestureDetector.SimpleOnGestureListener(){
        private val SWIPE_THRESHOLD = 100 //100 pixels minimum for swipe
        private val SWIPE_VEL_THRESHOLD = 100 //Test!
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            var diffX = e2?.x?.minus(e1!!.x) ?: 0.0f
            if(Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VEL_THRESHOLD){
                if(diffX > 0 ){
                    //Right swipe
                    this@Home.navigateTo(GameListFragment.newInstance(app.gameMemStore, true), false)
                    info { "Debug right swipe" }
                }
                else{
                    //Left swipe
                    this@Home.navigateTo(GameListFragment.newInstance(app.gameMemStore, false), false)
                    info { "Debug left swipe" }
                }
            }

            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }
}