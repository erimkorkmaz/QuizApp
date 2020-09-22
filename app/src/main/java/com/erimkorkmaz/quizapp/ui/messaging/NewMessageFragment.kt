package com.erimkorkmaz.quizapp.ui.messaging

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.model.User
import com.erimkorkmaz.quizapp.utils.convertMapToPOJO
import com.erimkorkmaz.quizapp.utils.hideToolbarRightIcon
import com.erimkorkmaz.quizapp.utils.toolbarTitle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_new_message.*

class NewMessageFragment : Fragment(), NewMessageItemClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var newMessageAdapter: NewMessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore
        recycler_new_message_users.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        if (::newMessageAdapter.isInitialized.not()) {
            showProgress()
            fetchUsers()
        } else {
            recycler_new_message_users.adapter = newMessageAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        toolbarTitle("Send Messages to")
        hideToolbarRightIcon()
    }

    override fun userItemClicked(user: User) {
        requireActivity().supportFragmentManager.popBackStack()
        (activity as MessagingActivity).switchToFragment(
            ChatLogFragment.newInstance(user)
        )
    }

    private fun fetchUsers() {
        val users = mutableListOf<User>()
        val usersRef = db.collection("Users")
        usersRef.orderBy("userName")
            .get()
            .addOnSuccessListener { result ->
                for (document in result.documents) {
                    if (document["uid"].toString() != auth.currentUser?.uid) {
                        val user = convertMapToPOJO(document.data!!, User::class.java)
                        users.add(user as User)
                    }
                }
                newMessageAdapter = NewMessageAdapter(this, users)
                recycler_new_message_users.adapter = newMessageAdapter
                hideProgress()
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }

    private fun showProgress() {
        if (progress_new_message != null) {
            progress_new_message.visibility = View.VISIBLE
            progress_new_message.setAnimation("loading.json")
            progress_new_message.playAnimation()
            progress_new_message.loop(true)
            recycler_new_message_users?.visibility = View.GONE
        }
    }

    private fun hideProgress() {
        if (progress_new_message != null) {
            progress_new_message.visibility = View.GONE
            recycler_new_message_users?.visibility = View.VISIBLE
        }
    }
}