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
import com.erimkorkmaz.quizapp.utils.hideToolbarRightIcon
import com.erimkorkmaz.quizapp.utils.toolbarTitle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_new_message.*

class NewMessageFragment : Fragment(), NewMessageItemClickListener {

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
        db = Firebase.firestore
        recycler_new_message_users.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        fetchUsers()
    }

    override fun onResume() {
        super.onResume()
        toolbarTitle("SELECT USER")
        hideToolbarRightIcon()
    }

    override fun userItemClicked(user: User) {
        (activity as MessagingActivity).switchToFragment(
            ChatLogFragment.newInstance(user)
        )
    }

    private fun fetchUsers() {
        var users = mutableListOf<User>()
        db.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result.documents) {
                    users.add(
                        User(
                            document["uid"].toString(),
                            document["email"].toString(),
                            document["userName"].toString(),
                            document["profileImageUrl"].toString()
                        )
                    )
                }
                newMessageAdapter = NewMessageAdapter(this, users)
                recycler_new_message_users.adapter = newMessageAdapter
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }
    }
}