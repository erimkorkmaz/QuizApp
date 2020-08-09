package com.erimkorkmaz.quizapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.utils.toolbarIcon
import com.erimkorkmaz.quizapp.utils.toolbarTitle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.layout_common_toolbar.view.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth
        db = Firebase.firestore

        included_app_bar.toolbar_common.setNavigationOnClickListener {
            finish()
        }
        button_register.setOnClickListener {
            register()
        }
    }

    override fun onResume() {
        super.onResume()
        toolbarTitle("REGISTER")
        toolbarIcon(R.drawable.ic_baseline_arrow_back_24)
    }

    private fun register() {
        val email = text_email.text.toString().trim()
        val username = text_username.text.toString().trim()
        val password = text_password.text.toString().trim()

        if (username.isEmpty()) {
            text_username.error = "Please enter username"
            text_username.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            text_email.error = "Please enter valid email"
            text_email.requestFocus()
            return
        }

        if (password.isEmpty()) {
            text_password.error = "Please enter password"
            text_password.requestFocus()
            return
        }
        if (password.length < 6) {
            text_password.error = "At least 6 characters"
            text_password.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveUserToDb(username)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    applicationContext,
                    exception.localizedMessage?.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun saveUserToDb(username : String) {
        val userData = hashMapOf<String, Any>()
        userData["username"] = username
        userData["email"] = auth.currentUser!!.email.toString()
        db.collection("Users").document(auth.currentUser!!.uid).set(userData)
            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }
}
