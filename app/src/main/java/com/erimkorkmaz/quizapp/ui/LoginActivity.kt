package com.erimkorkmaz.quizapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.utils.toolbarIcon
import com.erimkorkmaz.quizapp.utils.toolbarTitle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_common_toolbar.view.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        included_app_bar.toolbar_common.setNavigationOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        button_login.setOnClickListener {
            login()
        }
    }

    override fun onResume() {
        super.onResume()
        toolbarTitle("LOGIN")
        toolbarIcon(R.drawable.ic_baseline_arrow_back_24)
    }

    private fun login() {
        val email = text_email.text.toString()
        val password = text_password.text.toString()
        if (email.isEmpty()) {
            Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show()
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    applicationContext,
                    exception.localizedMessage.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}