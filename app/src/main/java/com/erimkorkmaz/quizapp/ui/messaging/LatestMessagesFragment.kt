package com.erimkorkmaz.quizapp.ui.messaging

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.model.ChatMessage
import com.erimkorkmaz.quizapp.model.User
import com.erimkorkmaz.quizapp.utils.toolbarTitle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
        listenForLatestMessages()
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
                            val chatMessage = ChatMessage(
                                dc.document["text"].toString(),
                                dc.document["fromId"].toString(),
                                dc.document["toId"].toString(),
                                dc.document["timeStamp"].toString(),
                                dc.document["fromUsername"].toString(),
                                dc.document["toUsername"].toString()
                            )
                            latestMessagesMap[getPartnerId(chatMessage)] = chatMessage
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val chatMessage = ChatMessage(
                                dc.document["text"].toString(),
                                dc.document["fromId"].toString(),
                                dc.document["toId"].toString(),
                                dc.document["timeStamp"].toString(),
                                dc.document["fromUsername"].toString(),
                                dc.document["toUsername"].toString()
                            )
                            latestMessagesMap[getPartnerId(chatMessage)] = chatMessage
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
                        user = User(
                            document["uid"].toString(),
                            document["email"].toString(),
                            document["userName"].toString(),
                            document["profileImageUrl"].toString()
                        )
                        latestMessagesPairList.add(Pair(user, chatMessage))
                    }
                }
            }
            latestMessagesAdapter = LatestMessagesAdapter(this, latestMessagesPairList)
            recyclerLatestMessages?.adapter = latestMessagesAdapter
        }
    }
}