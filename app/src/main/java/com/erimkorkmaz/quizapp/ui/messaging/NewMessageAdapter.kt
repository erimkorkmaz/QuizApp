package com.erimkorkmaz.quizapp.ui.messaging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.model.User
import com.erimkorkmaz.quizapp.utils.makeCircularAnonymousImage
import kotlinx.android.synthetic.main.item_new_message_users.view.*


class NewMessageAdapter(
    private val listener: NewMessageItemClickListener,
    val users: MutableList<User>
) :
    RecyclerView.Adapter<NewMessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewMessageAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_new_message_users, parent, false), listener
        )
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: NewMessageAdapter.ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    inner class ViewHolder(
        itemView: View, private val listener: NewMessageItemClickListener
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private lateinit var user: User

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(user: User) {
            this.user = user
            itemView.text_new_message.text = user.userName
            Glide.with(itemView.context)
                .load(user.profileImageUrl).apply(
                    RequestOptions().circleCrop()
                        .placeholder(
                            makeCircularAnonymousImage(
                                itemView.context,
                                R.drawable.ic_anonymous
                            )
                        )
                        .error(
                            makeCircularAnonymousImage(
                                itemView.context,
                                R.drawable.ic_anonymous
                            )
                        )
                )
                .into(itemView.image_new_message)
        }

        override fun onClick(view: View) {
            listener.userItemClicked(user)
        }
    }
}