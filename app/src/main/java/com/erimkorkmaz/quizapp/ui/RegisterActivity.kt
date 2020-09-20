package com.erimkorkmaz.quizapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.erimkorkmaz.quizapp.ModelPreferencesManager
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.model.User
import com.erimkorkmaz.quizapp.utils.toolbarTitle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth
        db = Firebase.firestore

        button_register.setOnClickListener {
            register()
        }

        text_have_account.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        toolbarTitle("REGISTER")
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
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    applicationContext,
                    exception.localizedMessage?.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun saveUserToDb(username: String) {
        val user = User(auth.currentUser!!.uid, auth.currentUser!!.email.toString(), username)
        ModelPreferencesManager.put(user, "KEY_USER")
        db.collection("Users").document(auth.currentUser!!.uid).set(user)
            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }
}
