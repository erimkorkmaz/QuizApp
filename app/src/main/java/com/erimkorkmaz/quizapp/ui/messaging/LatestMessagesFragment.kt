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
import com.erimkorkmaz.quizapp.utils.toolbarTitle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LatestMessagesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var latestMessagesAdapter: LatestMessagesAdapter
    var recyclerLatestMessages: RecyclerView? = null

    val latestMessagesMap = HashMap<String, ChatMessage>()

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
        recyclerLatestMessages?.removeAllViews()
        listenForLatestMessages()
    }

    override fun onResume() {
        super.onResume()
        toolbarTitle("MESSAGES")
    }

    private fun listenForLatestMessages() {
        val chatMessages = mutableListOf<ChatMessage>()
        db.collection("Users").document(auth.currentUser?.uid!!).collection("LatestMessages")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("TAG", "listen:error", error)
                    return@addSnapshotListener
                }
                for (dc in snapshots!!.documentChanges) {
                    var chatPartnerName: String? = null
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
                            //                      latestMessagesMap[dc.document.id] = chatMessage
                            //                      refreshRecyclerViewMessages()
                            chatPartnerName = if (chatMessage.fromId == auth.currentUser?.uid!!) {
                                chatMessage.toUsername
                            } else {
                                chatMessage.fromUsername
                            }

                            chatMessages.add(chatMessage)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            /*         db.collection("Users").document(dc.document.id)
                                         .get().addOnSuccessListener {
                                             userName = it?.data!!["userName"].toString()
                                         }

                             */
                            val chatMessage = ChatMessage(
                                dc.document["text"].toString(),
                                dc.document["fromId"].toString(),
                                dc.document["toId"].toString(),
                                dc.document["timeStamp"].toString(),
                                dc.document["fromUsername"].toString(),
                                dc.document["toUsername"].toString()
                            )
                            chatMessages.add(chatMessage)
                            chatPartnerName = if (chatMessage.fromId == auth.currentUser?.uid!!) {
                                chatMessage.toUsername
                            } else {
                                chatMessage.fromUsername
                            }
                        }
                    }
                    latestMessagesAdapter =
                        LatestMessagesAdapter(chatMessages, chatPartnerName!!, "")
                    recyclerLatestMessages?.removeAllViews()
                    recyclerLatestMessages?.adapter = latestMessagesAdapter
                }
            }
    }
}