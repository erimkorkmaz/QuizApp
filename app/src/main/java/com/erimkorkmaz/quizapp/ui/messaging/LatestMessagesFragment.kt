package com.erimkorkmaz.quizapp.ui.messaging

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.model.ChatMessage
import com.erimkorkmaz.quizapp.model.User
import com.erimkorkmaz.quizapp.utils.convertMapToPOJO
import com.erimkorkmaz.quizapp.utils.toolbarTitle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_latest_messages.*
import kotlinx.android.synthetic.main.layout_no_messages.*

class LatestMessagesFragment : Fragment(), NewMessageItemClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var latestMessagesAdapter: LatestMessagesAdapter
    private var recyclerLatestMessages: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_latest_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore
        recyclerLatestMessages =
            requireActivity().findViewById(R.id.recycler_latest_messages) as RecyclerView
        val divider =
            DividerItemDecoration(recyclerLatestMessages!!.context, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.custom_divider)!!);
        recyclerLatestMessages!!.addItemDecoration(divider)
        listenForLatestMessages()
        button_no_messages.setOnClickListener {
            (activity as MessagingActivity).switchToFragment(NewMessageFragment())
        }
    }

    override fun onResume() {
        super.onResume()
        toolbarTitle("MESSAGES")
    }

    override fun userItemClicked(user: User) {
        (activity as MessagingActivity).switchToFragment(
            ChatLogFragment.newInstance(user)
        )
    }

    private fun listenForLatestMessages() {
        showProgress()
        val latestMessagesMap = HashMap<String, ChatMessage>()
        db.collection("Users").document(auth.currentUser?.uid!!).collection("LatestMessages")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("TAG", "listen:error", error)
                    return@addSnapshotListener
                }
                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val chatMessage = convertMapToPOJO(
                                dc.document.data,
                                ChatMessage::class.java
                            )
                            latestMessagesMap[getPartnerId(chatMessage as ChatMessage)] =
                                chatMessage
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val chatMessage = convertMapToPOJO(
                                dc.document.data,
                                ChatMessage::class.java
                            )
                            latestMessagesMap[getPartnerId(chatMessage as ChatMessage)] =
                                chatMessage
                        }
                    }
                }
                orderLatestMessages(latestMessagesMap)
            }
    }

    private fun getPartnerId(chatMessage: ChatMessage): String {
        return if (chatMessage.fromId == auth.currentUser?.uid!!) {
            chatMessage.toId
        } else {
            chatMessage.fromId
        }
    }

    private fun orderLatestMessages(map: HashMap<String, ChatMessage>) {
        val latestMessagesPairList = ArrayList<Pair<User, ChatMessage>>()
        var user: User
        db.collection("Users").get().addOnSuccessListener { result ->
            for (document in result.documents) {
                for ((userId, chatMessage) in map) {
                    if (document["uid"] == userId) {
                        user =
                            convertMapToPOJO(document.data!!, User::class.java) as User
                        latestMessagesPairList.add(Pair(user, chatMessage))
                    }
                }
            }
            latestMessagesAdapter = LatestMessagesAdapter(this, latestMessagesPairList)
            recyclerLatestMessages?.adapter = latestMessagesAdapter
            hideProgress()
            if (latestMessagesPairList.isEmpty() && included_layout_no_messages != null) {
                included_layout_no_messages.visibility = View.VISIBLE
            }
        }
    }

    private fun showProgress() {
        if (progress_latest_messages != null) {
            progress_latest_messages.visibility = View.VISIBLE
            progress_latest_messages.setAnimation("loading.json")
            progress_latest_messages.playAnimation()
            progress_latest_messages.loop(true)
            recyclerLatestMessages?.visibility = View.GONE
        }
    }

    private fun hideProgress() {
        if (progress_latest_messages != null) {
            progress_latest_messages.visibility = View.GONE
            recyclerLatestMessages?.visibility = View.VISIBLE
        }
    }
}