package com.erimkorkmaz.quizapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.ui.messaging.MessagingActivity
import com.erimkorkmaz.quizapp.utils.toolbarRightIcon
import com.erimkorkmaz.quizapp.utils.toolbarTitle
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_common_toolbar.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val categoriesFragment = CategoriesFragment()
    private val leaderboardFragment = LeaderboardFragment()
    private val profileFragment = ProfileFragment()

    var isConnected: Boolean = true
    val compositeDisposable = CompositeDisposable()

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.navigation_categories -> {
                    toolbarTitle("CATEGORIES")
                    categoriesFragment
                }
                R.id.navigation_leaderboard -> {
                    toolbarTitle("LEADERBOARD")
                    leaderboardFragment
                }
                R.id.navigation_profile -> {
                    toolbarTitle("PROFILE")
                    profileFragment
                }
                else -> categoriesFragment
            }
            switchToFragment(fragment)
            true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbarTitle("CATEGORIES")
        auth = Firebase.auth
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        if (savedInstanceState == null)
            switchToFragment(categoriesFragment)
        checkConnectivity()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            auth.signOut()
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun switchToFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_container, fragment).commit()
    }

    override fun onResume() {
        super.onResume()
        toolbarRightIcon(R.drawable.ic_round_message)
        image_toolbar_right_common.setOnClickListener {
            startActivity(Intent(this, MessagingActivity::class.java))
        }

    }

    private fun checkConnectivity() {
        compositeDisposable.add(
            ReactiveNetwork
                .observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val noInternetFragment =
                        NoInternetFragment.newInstance()
                    isConnected = it
                    if (isConnected) {
                        supportFragmentManager.popBackStack()
                    } else {
                        supportFragmentManager.beginTransaction().setCustomAnimations(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out,
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                        )
                            .add(android.R.id.content, noInternetFragment)
                            .addToBackStack("Nointernet").commit()
                    }
                }
        )
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
