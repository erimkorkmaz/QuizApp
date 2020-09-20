package com.erimkorkmaz.quizapp.ui.messaging

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.utils.toolbarIcon
import com.erimkorkmaz.quizapp.utils.toolbarRightIcon
import kotlinx.android.synthetic.main.activity_quiz.*
import kotlinx.android.synthetic.main.layout_common_toolbar.*
import kotlinx.android.synthetic.main.layout_common_toolbar.view.*

class MessagingActivity : AppCompatActivity() {

    private val latestMessagesFragment = LatestMessagesFragment()
    private val newMessageFragment = NewMessageFragment()
    private var chatLogFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging)
        switchToFragment(latestMessagesFragment)
        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.main_container)
            if (currentFragment is ChatLogFragment) {
                chatLogFragment = currentFragment
            }

            if (latestMessagesFragment.isVisible) {
                toolbarRightIconClicked()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        toolbarIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbarRightIconClicked()
        included_app_bar.toolbar_common.setNavigationOnClickListener {
            if (newMessageFragment.isVisible || (chatLogFragment != null && chatLogFragment!!.isVisible)) {
                supportFragmentManager.popBackStack()
            } else {
                finish()
            }
        }

    }

    private fun toolbarRightIconClicked() {
        toolbarRightIcon(R.drawable.ic_new_message)
        image_toolbar_right_common.setOnClickListener {
            switchToFragment(newMessageFragment)
        }
    }

    fun switchToFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_container, fragment).addToBackStack(null).commit()
    }
}