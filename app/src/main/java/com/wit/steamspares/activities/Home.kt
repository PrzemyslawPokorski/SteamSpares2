package com.wit.steamspares.activities

import android.app.Activity
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.google.android.material.navigation.NavigationView
import com.wit.steamspares.R
import com.wit.steamspares.fragments.EditGameFragment
import com.wit.steamspares.fragments.GameListFragment
import com.wit.steamspares.main.MainApp
import com.wit.steamspares.models.GameMemStore
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.content_home.*
import kotlinx.android.synthetic.main.home.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class Home : AppCompatActivity(), AnkoLogger, NavigationView.OnNavigationItemSelectedListener {
    enum class MenuType(val color: Int){
        LIST_USED(R.color.used_game_bar), LIST_UNUSED(R.color.unused_game_bar), EDIT(R.color.colorAccent)
    }

    enum class UsedStatus(val fragName: String, val usedStatus: Boolean){
        USED("USED", true),
        UNUSED("UNUSED", false)
    }

    lateinit var app: MainApp
    lateinit var gameStore: GameMemStore
    lateinit var filter : SearchView
    lateinit var fragmentTransaction : FragmentTransaction
    lateinit var topMenu : MenuType
    lateinit var detector: GestureDetectorCompat

    var touchTime : Long = 0
    var touchX : Float = 0f
    var touchY : Float = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        GameMemStore.mainAppFrame = mainAppFrame

        app = application as MainApp
        topMenu = MenuType.LIST_UNUSED

        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        detector = GestureDetectorCompat(this, GestureListener())

        navView.setNavigationItemSelectedListener(this)
        //TODO: Set id (and image) for user in app bar
        val navHeader = navView.getHeaderView(0)
        navHeader.user_id.text = app.auth.currentUser?.email

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)

        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        if(savedInstanceState == null) {
            fragmentTransaction = supportFragmentManager.beginTransaction()

            navigateTo(UsedStatus.UNUSED, addToStack = false)
        }
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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        info { "Debug4 touch event" }
        return super.onTouchEvent(event)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        when(topMenu){
            MenuType.EDIT -> {
                menuInflater.inflate(R.menu.menu_steamspares_add, menu)
                toolbar.setBackgroundColor(resources.getColor(MenuType.EDIT.color))
            }
            MenuType.LIST_UNUSED -> {
                menuInflater.inflate(R.menu.menu_steamspares_list, menu)
                toolbar.setBackgroundColor(resources.getColor(MenuType.LIST_UNUSED.color))

                if (menu != null) {
                    filter = menu.findItem(R.id.filter_bar).actionView as SearchView
                    filter.setOnQueryTextListener(GameMemStore)
                }
            }
            MenuType.LIST_USED -> {
                menuInflater.inflate(R.menu.menu_steamspares_list, menu)
                toolbar.setBackgroundColor(resources.getColor(MenuType.LIST_USED.color))

                if (menu != null) {
                    filter = menu.findItem(R.id.filter_bar).actionView as SearchView
                    filter.setOnQueryTextListener(GameMemStore)
                }
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                navigateTo(EditGameFragment.newInstance(GameMemStore))
            }
            R.id.action_cancel ->{
                supportFragmentManager.popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun navigateTo(fragment: Fragment, addToStack: Boolean = true) {
        supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            replace(R.id.mainAppFrame, fragment)
            if(addToStack)
                addToBackStack(null)
        }

        hideKeyboard(this)
    }

    fun navigateTo(status: UsedStatus? = null, addToStack: Boolean = true) {
        if(status == null)
            return
        var fragment = supportFragmentManager.findFragmentByTag(status.fragName)
        if(fragment == null){
            fragment = GameListFragment.newInstance(GameMemStore, status.usedStatus)
        }

        info { "Debug multifrag: Should navigate to ${status.fragName} list" }

        supportFragmentManager.commit {
            if(status.usedStatus)
                setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            else
                setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
            replace(R.id.mainAppFrame, fragment, status.fragName)
            if(addToStack)
                addToBackStack(null)
        }

        hideKeyboard(this)
    }

    fun askForMenu(menuType: MenuType){
        topMenu = menuType
        info { "Debug: Navigate asked for menu $menuType" }
        invalidateOptionsMenu()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //Used / unused changing
            R.id.nav_unused -> navigateTo(UsedStatus.UNUSED, addToStack = false)
            R.id.nav_used -> navigateTo(UsedStatus.USED, addToStack = false)

            //Extras
            R.id.nav_sign_out ->
                {
                    app.auth.signOut()
                    startActivity<Login>()
                    finish()
                }

            else -> toast("This screen is not yet implemented")
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

//    https://www.youtube.com/watch?v=j1aydFEOEA0
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
                    //Destinations are placeholder
                    //Right swipe
                    this@Home.navigateTo(UsedStatus.USED, addToStack = false)
                    info { "Debug right swipe" }
                }
                else{
                    //Left swipe
                    this@Home.navigateTo(UsedStatus.UNUSED, addToStack = false)
                    info { "Debug left swipe" }
                }
            }

            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if(topMenu == MenuType.EDIT || drawer_layout.isDrawerOpen(GravityCompat.START))
            return super.dispatchTouchEvent(ev)

        if (ev.action == MotionEvent.ACTION_DOWN) {
            touchTime = SystemClock.uptimeMillis()
            touchX = ev.rawX
        }
        else if(ev.action == MotionEvent.ACTION_UP){
            val timeTouched = SystemClock.uptimeMillis() - touchTime
            val xDist = Math.abs(touchX - ev.rawX)
            val dir = if (touchX - ev.rawX > 0) "left" else "right"
            info { "Debug4: (TouchX: $touchX) Finger raised $timeTouched long with $xDist distance in direction $dir" }
            if(touchX > 50 && (timeTouched > 100 && xDist > 100 && xDist/timeTouched > 1)) {
                info { "Debug4 Its a swipe!" }
                when(dir){
                    "left" -> navigateTo(UsedStatus.USED, false)
                    "right" -> navigateTo(UsedStatus.UNUSED, false)
                }
            }
        }

        return super.dispatchTouchEvent(ev)
    }
}