package com.erimkorkmaz.quizapp.ui.messaging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.model.ChatMessage
import com.erimkorkmaz.quizapp.model.User
import kotlinx.android.synthetic.main.list_item_last_messages.view.*

class LatestMessagesAdapter(
    private val listener: NewMessageItemClickListener,
    private val latestMessagesList: ArrayList<Pair<User, ChatMessage>>
) :
    RecyclerView.Adapter<LatestMessagesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LatestMessagesAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_last_messages, parent, false), listener
        )
    }

    override fun getItemCount() = orderLatestMessages().size

    override fun onBindViewHolder(holder: LatestMessagesAdapter.ViewHolder, position: Int) {
        holder.bind(
            orderLatestMessages()[position].first,
            orderLatestMessages()[position].second
        )
    }

    private fun orderLatestMessages(): MutableList<Pair<User, ChatMessage>> {
        latestMessagesList.sortByDescending { it.second.timeStamp.toLong() }
        return latestMessagesList
    }

    inner class ViewHolder(
        itemView: View, private val listener: NewMessageItemClickListener
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private lateinit var user: User

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(user: User, chatMessage: ChatMessage) {
            this.user = user
            itemView.text_latest_messages_username.text = user.userName
            itemView.text_latest_message.text = chatMessage.text
            Glide.with(itemView.context)
                .load(user.profileImageUrl).apply(
                    RequestOptions().centerCrop().transform(RoundedCorners(64))
                    //   .placeholder(R.drawable.art)
                    //    .error(R.drawable.art)
                )
                .into(itemView.image_latest_messages)
        }

        override fun onClick(view: View) {
            listener.userItemClicked(user)
        }
    }
}