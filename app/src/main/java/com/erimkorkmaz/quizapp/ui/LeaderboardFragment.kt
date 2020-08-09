package com.erimkorkmaz.quizapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.erimkorkmaz.quizapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_leaderboard.*

class LeaderboardFragment : Fragment(), CategoryClickListener {
    private lateinit var db: FirebaseFirestore
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private lateinit var categoryAdapter: LeaderboardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.firestore
        recycler_categories.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recycler_leaderboard.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        loadCategoryAdapter()
        loadLeaderboardAdapter()
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recycler_categories)
    }

    private fun loadCategoryAdapter() {
        categoryAdapter = LeaderboardAdapter(this, retrieveData(), true, "")
        recycler_categories.adapter = categoryAdapter
        hideProgress()
    }

    private fun loadLeaderboardAdapter() {
        leaderboardAdapter = LeaderboardAdapter(this, retrieveData(), false, "Animals")
        recycler_leaderboard.adapter = leaderboardAdapter
    }

    override fun onCategoryClicked(category: String) {
        leaderboardAdapter = LeaderboardAdapter(this, retrieveData(), false, category)
        recycler_leaderboard.adapter = leaderboardAdapter
    }

    private fun retrieveData(): ArrayList<Triple<String, String, Int>> {
        showProgress()
        val userList = arrayListOf<Triple<String, String, Int>>()
        db.collection("Users").get().addOnSuccessListener { querySnapshot ->
            val users = querySnapshot.documents
            for (user in users) {
                db.collection("Users").document(user.id).collection("Scores").get()
                    .addOnSuccessListener {
                        val scores = it.documents
                        for (score in scores) {
                            userList.add(
                                Triple(
                                    user["username"].toString(),
                                    score.data?.keys.toString().drop(1).dropLast(1),
                                    score.data?.values.toString().drop(1).dropLast(1).toInt()
                                )
                            )
                            categoryAdapter.notifyDataSetChanged()
                            leaderboardAdapter.notifyDataSetChanged()
                        }
                    }
            }
            hideProgress()
        }.addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }
        return userList
    }

    private fun showProgress() {
        progress_leaderboard.visibility = View.VISIBLE
        progress_leaderboard.setAnimation("loading.json")
        progress_leaderboard.playAnimation()
        progress_leaderboard.loop(true)
        recycler_categories.visibility = View.GONE
        recycler_leaderboard.visibility = View.GONE
    }

    private fun hideProgress() {
        progress_leaderboard.visibility = View.GONE
        recycler_categories.visibility = View.VISIBLE
        recycler_leaderboard.visibility = View.VISIBLE
    }
}