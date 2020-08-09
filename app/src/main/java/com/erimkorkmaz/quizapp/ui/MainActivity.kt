package com.erimkorkmaz.quizapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.erimkorkmaz.quizapp.R
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val categoriesFragment = CategoriesFragment()
    private val leaderboardFragment = LeaderboardFragment()

    var isConnected: Boolean = true
    val compositeDisposable = CompositeDisposable()

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.navigation_categories -> {
                    actionBar?.title = "Categories"
                    title = "Categories"
                    categoriesFragment
                }
                R.id.navigation_leaderboard -> {
                    actionBar?.title = "Leaderboard"
                    title = "Leaderboard"
                    leaderboardFragment
                }
                else -> categoriesFragment
            }
            switchToFragment(fragment)
            true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setSupportActionBar(main_toolbar)
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
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun switchToFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_container, fragment).commit()
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
