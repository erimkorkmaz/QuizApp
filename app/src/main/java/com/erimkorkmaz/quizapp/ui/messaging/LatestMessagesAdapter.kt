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
import kotlinx.android.synthetic.main.list_item_last_messages.view.*

class LatestMessagesAdapter(
    private val latestMessagesMap: HashMap<String, ChatMessage>,
    private val userImageUrl: String
) :
    RecyclerView.Adapter<LatestMessagesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LatestMessagesAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_last_messages, parent, false)
        )
    }

    override fun getItemCount() = latestMessagesMap.size

    override fun onBindViewHolder(holder: LatestMessagesAdapter.ViewHolder, position: Int) {
        holder.bind(
            latestMessagesMap.values.elementAt(position),
            userImageUrl,
            latestMessagesMap.keys.elementAt(position)
        )
    }

    inner class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        fun bind(chatMessage: ChatMessage, userImageUrl: String, username: String) {
            itemView.text_latest_messages_username.text = username
            itemView.text_latest_message.text = chatMessage.text
            Glide.with(itemView.context)
                .load(userImageUrl).apply(
                    RequestOptions().centerCrop().transform(RoundedCorners(64))
                    //     .placeholder(R.drawable.ic_image_place_holder)
                    //     .error(R.drawable.ic_broken_image)
                    //     .fallback(R.drawable.ic_no_image)
                )
                .into(itemView.image_latest_messages)
        }

        override fun onClick(view: View) {
        }
    }
}