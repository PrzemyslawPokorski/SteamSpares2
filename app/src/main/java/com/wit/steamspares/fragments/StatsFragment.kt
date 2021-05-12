package com.wit.steamspares.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseUser
import com.wit.steamspares.R
import com.wit.steamspares.activities.Home
import com.wit.steamspares.models.GameMemStore
import kotlinx.android.synthetic.main.fragment_stats.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


/**
 * A simple [Fragment] subclass.
 * Use the [StatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StatsFragment : Fragment() {

    private var userid: String = "0"
    private var username: String = "NONAME"
    private var usermail: String = "NOMAIL"
    private var userSince: LocalDateTime = LocalDateTime.now()
    private var gamesTotal = 0
    private var gamesUsed = 0
    private var gamesUnused = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as Home).askForMenu(Home.MenuType.EDIT)
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user_id_text.text = userid
        usernametext.text = username
        usermailtext.text = usermail
        usersincetext.text = userSince.toString()

        gamestotaltext.text = gamesTotal.toString()
        gamesunusedcounttext.text = gamesUnused.toString()
        gamesusedcounttext.text = gamesUsed.toString()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param gameMemStore Parameter 1.
         * @param user Parameter 2.
         * @return A new instance of fragment StatsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(gameMemStore: GameMemStore, user: FirebaseUser) =
            StatsFragment().apply {
                arguments = Bundle().apply {
                }
                this.gamesTotal = gameMemStore.getUsed(true).count() + gameMemStore.getUsed(false).count()
                this.gamesUsed = gameMemStore.getUsed(true).count()
                this.gamesUnused = gamesTotal - gamesUsed

                this.username = user.displayName.toString()
                this.userid = user.uid
                this.usermail = user.email.toString()
                this.userSince = Instant.ofEpochSecond(user.metadata!!.creationTimestamp).atZone(
                    ZoneId.systemDefault()).toLocalDateTime()
            }
    }
}