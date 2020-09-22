package com.erimkorkmaz.quizapp.ui.messaging

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.erimkorkmaz.quizapp.ModelPreferencesManager
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.model.ChatMessage
import com.erimkorkmaz.quizapp.model.User
import com.erimkorkmaz.quizapp.utils.convertMapToPOJO
import com.erimkorkmaz.quizapp.utils.hideToolbarRightIcon
import com.erimkorkmaz.quizapp.utils.toolbarTitle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_chat_log.*

class ChatLogFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var toUser: User
    private lateinit var chatLogAdapter: ChatLogAdapter
    private var user: User? = null
    var recyclerChatLog: RecyclerView? = null

    companion object {
        private const val ARG_USER = "arg_user"

        fun newInstance(user: User) =
            ChatLogFragment().apply {
                val args = Bundle().apply {
                    putParcelable(ARG_USER, user)
                }
                arguments = args
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toUser = arguments?.getParcelable(ARG_USER)!!
        auth = Firebase.auth
        db = Firebase.firestore
        recyclerChatLog = requireActivity().findViewById(R.id.recycler_chat_log) as RecyclerView
        listenForMessage()
        user = ModelPreferencesManager.get<User>("KEY_USER")
        button_send_chat_log.setOnClickListener {
            performSendMessage()
        }
    }

    override fun onResume() {
        super.onResume()
        toolbarTitle(toUser.userName)
        hideToolbarRightIcon()
    }

    private fun listenForMessage() {
        val chatMessages = mutableListOf<ChatMessage>()
        db.collection("UserMessages").document(auth.currentUser?.uid!!).collection(toUser.uid)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("TAG", "listen:error", e)
                    return@addSnapshotListener
                }
                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val chatMessage = convertMapToPOJO(
                                dc.document.data,
                                ChatMessage::class.java
                            )
                            chatMessages.add(chatMessage as ChatMessage)
                        }
                    }

                }
                chatLogAdapter = ChatLogAdapter(
                    chatMessages,
                    toUser,
                    user!!
                )
                recyclerChatLog?.adapter = chatLogAdapter
                recyclerChatLog?.scrollToPosition(chatLogAdapter.itemCount - 1)
            }
    }

    private fun performSendMessage() {
        val message = edittext_chat_log.text.toString()
        val fromId = auth.currentUser?.uid
        val toId = toUser.uid

        if (!fromId.isNullOrEmpty()) {
            val chatMessage =
                ChatMessage(
                    message,
                    fromId,
                    toId,
                    (System.currentTimeMillis() / 1000).toString(),
                    user?.userName!!,
                    toUser.userName
                )
            db.collection("UserMessages").document(fromId).collection(toId).add(chatMessage)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot written with ID: ${documentReference.id}")
                    edittext_chat_log.text.clear()
                    recycler_chat_log.scrollToPosition(chatLogAdapter.itemCount - 1)
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                }

            db.collection("UserMessages").document(toId).collection(fromId).add(chatMessage)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot written with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                }

            db.collection("Users").document(fromId).collection("LatestMessages")
                .document(toId).set(chatMessage)
                .addOnSuccessListener { documentReference ->
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                }

            db.collection("Users").document(toId).collection("LatestMessages")
                .document(toId).set(chatMessage)
                .addOnSuccessListener { documentReference ->
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                }
        }
    }
}