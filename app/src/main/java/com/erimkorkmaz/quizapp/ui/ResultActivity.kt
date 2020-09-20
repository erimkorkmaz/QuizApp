package com.erimkorkmaz.quizapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.model.Category
import com.erimkorkmaz.quizapp.utils.toolbarIcon
import com.erimkorkmaz.quizapp.utils.toolbarTitle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.layout_common_toolbar.view.*

class ResultActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var category: Category
    private var score = 0

    companion object {
        private const val CATEGORY_ID = "CATEGORY ID"
        private const val SCORE = "SCORE"

        fun newIntent(context: Context, category: Category, score: Int): Intent {
            val intent = Intent(context, ResultActivity::class.java)
            intent.putExtra(CATEGORY_ID, category)
            intent.putExtra(SCORE, score)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        auth = Firebase.auth
        db = Firebase.firestore

        toolbarTitle("RESULT")
        toolbarIcon(R.drawable.ic_baseline_arrow_back_24)
        included_app_bar.toolbar_common.setNavigationOnClickListener {
            onBackPressed()
            finish()
        }

        category = intent.getParcelableExtra(CATEGORY_ID)
        score = intent.getIntExtra(SCORE, score)

        saveData()
        setupView()

        text_score.setOnClickListener {
            shareTextContent("${category.name}: $score", "Share Your Score")
        }

        button_restart.setOnClickListener {
            val intent = QuizActivity.newIntent(this, category)
            startActivity(intent)
            finish()
        }

        button_backToMenu.setOnClickListener {
            onBackPressed()
            finish()
        }
    }


    private fun setupView() {
        text_category.text = "Category\n${category.name}"
        if (score == 100) {
            text_gameOver.text = "CONGRATULATIONS!"
        } else {
            text_gameOver.text = "GAME OVER!"
        }
        text_score.text = "    Your Score\n    $score"
        button_restart.text = "Play Again"
        button_backToMenu.text = "Select Category"
    }

    private fun shareTextContent(message: String, title: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        startActivity(shareIntent)
    }

    private fun saveData() {
        val scoreData = hashMapOf<String, Int>()
        scoreData[category.name] = score
        val scoresCollection =
            db.collection("Users").document(auth.currentUser!!.uid).collection("Scores")

        val categoryDocument = scoresCollection.document(category.name)
        var existingScore = 0
        categoryDocument.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.data?.get(category.name) == null) {
                scoresCollection.document(category.name).set(scoreData)
                    .addOnSuccessListener {
                        Log.d(
                            "TAG",
                            "DocumentSnapshot successfully written!"
                        )
                    }
                    .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
            } else {
                existingScore = documentSnapshot.data?.get(category.name).toString().toInt()
                if (existingScore < score) {
                    scoresCollection.document(category.name).set(scoreData)
                        .addOnSuccessListener {
                            Log.d(
                                "TAG",
                                "DocumentSnapshot successfully written!"
                            )
                        }
                        .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
                }
            }
        }.addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }
    }
}