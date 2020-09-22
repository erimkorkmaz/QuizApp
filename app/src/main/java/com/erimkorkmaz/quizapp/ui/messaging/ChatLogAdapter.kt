package com.erimkorkmaz.quizapp.ui.messaging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.model.ChatMessage
import com.erimkorkmaz.quizapp.model.User
import com.erimkorkmaz.quizapp.utils.makeCircularAnonymousImage
import kotlinx.android.synthetic.main.item_chat_from.view.*
import kotlinx.android.synthetic.main.item_chat_to.view.*

class ChatLogAdapter(
    private val chatMessages: MutableList<ChatMessage>,
    private val toUser: User,
    private val fromUser: User
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            FromUserViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_from, parent, false)
            )
        } else {
            ToUserViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_to, parent, false)
            )
        }
    }

    override fun getItemCount() = chatMessages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FromUserViewHolder -> holder.bind(
                orderChatMessages()[position],
                fromUser.profileImageUrl!!
            )
            is ToUserViewHolder -> holder.bind(orderChatMessages()[position], toUser)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (orderChatMessages()[position].fromId == fromUser.uid) 1 else 0
    }

    private fun orderChatMessages(): MutableList<ChatMessage> {
        chatMessages.sortBy { it.timeStamp.toLong() }
        return chatMessages
    }

}

class FromUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(chatMessage: ChatMessage, fromUserImageUrl: String) {
        itemView.text_chat_from.text = chatMessage.text
        Glide.with(itemView.context)
            .load(fromUserImageUrl).apply(
                RequestOptions().circleCrop()
                    .placeholder(
                        makeCircularAnonymousImage(
                            itemView.context,
                            R.drawable.ic_anonymous
                        )
                    )
                    .error(makeCircularAnonymousImage(itemView.context, R.drawable.ic_anonymous))
            )
            .into(itemView.image_chat_from)
    }
}

class ToUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(chatMessage: ChatMessage, toUser: User) {
        itemView.text_chat_to.text = chatMessage.text
        Glide.with(itemView.context)
            .load(toUser.profileImageUrl).apply(
                RequestOptions().circleCrop()
                    .placeholder(
                        makeCircularAnonymousImage(
                            itemView.context,
                            R.drawable.ic_anonymous
                        )
                    )
                    .error(makeCircularAnonymousImage(itemView.context, R.drawable.ic_anonymous))
            )
            .into(itemView.image_chat_to)
    }
}