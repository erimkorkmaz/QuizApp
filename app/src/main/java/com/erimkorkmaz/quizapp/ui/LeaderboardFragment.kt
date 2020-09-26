package com.erimkorkmaz.quizapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.listeners.OnSnapPositionChangeListener
import com.erimkorkmaz.quizapp.listeners.SnapOnScrollListener
import com.erimkorkmaz.quizapp.model.User
import com.erimkorkmaz.quizapp.utils.attachSnapHelperWithListener
import com.erimkorkmaz.quizapp.utils.convertMapToPOJO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_leaderboard.*

class LeaderboardFragment : Fragment(), OnSnapPositionChangeListener {
    private lateinit var db: FirebaseFirestore
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private lateinit var categoryAdapter: LeaderboardAdapter
    private var userListForCategoryName = arrayListOf<Triple<User, String, Int>>()


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
        retrieveData()
        val snapHelper = LinearSnapHelper()
        recycler_categories.attachSnapHelperWithListener(
            snapHelper,
            SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
            this
        )
    }

    override fun onSnapPositionChange(position: Int) {
        val category = categoryNameFromPosition(position)
        leaderboardAdapter = LeaderboardAdapter(userListForCategoryName, false, category)
        recycler_leaderboard.adapter = leaderboardAdapter
    }

    private fun retrieveData(): ArrayList<Triple<User, String, Int>> {
        layout_shimmer.visibility = View.VISIBLE
        val userList = arrayListOf<Triple<User, String, Int>>()
        db.collection("Users").get().addOnSuccessListener { querySnapshot ->
            val users = querySnapshot.documents
            for (user in users) {
                user.reference.collection("Scores").get().addOnSuccessListener {
                    val scores = it.documents
                    val person = convertMapToPOJO(user.data!!, User::class.java)
                    for (score in scores) {
                        userList.add(
                            Triple(
                                person as User,
                                score.data?.keys.toString().drop(1).dropLast(1),
                                score.data?.values.toString().drop(1).dropLast(1).toInt()
                            )
                        )
                    }
                    userListForCategoryName = userList
                    layout_shimmer.visibility = View.GONE
                    userListForCategoryName = userList
                    loadCategoryAdapter()
                    loadLeaderboardAdapter()
                }
            }
        }
        return userList
    }

    private fun loadCategoryAdapter() {
        categoryAdapter = LeaderboardAdapter(userListForCategoryName, true)
        recycler_categories.adapter = categoryAdapter
        categoryAdapter.notifyDataSetChanged()
    }

    private fun loadLeaderboardAdapter() {
        leaderboardAdapter = LeaderboardAdapter(userListForCategoryName, false)
        recycler_leaderboard.adapter = leaderboardAdapter
        leaderboardAdapter.notifyDataSetChanged()
    }

    private fun categoryNameFromPosition(position: Int): String {
        var categoryName = ""
        val categories = arrayListOf<String>()
        for (value in userListForCategoryName) {
            categories.add(value.second)
        }
        val sortedList = categories.distinct().sorted().toMutableList()
        for (i in sortedList.indices) {
            if (position == i) {
                categoryName = sortedList[i]
            }
        }
        return categoryName
    }

    override fun onDestroyView() {
        userListForCategoryName.clear()
        super.onDestroyView()
    }
}