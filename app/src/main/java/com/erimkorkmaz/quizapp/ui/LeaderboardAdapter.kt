package com.erimkorkmaz.quizapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.model.User
import com.erimkorkmaz.quizapp.utils.StringUtils
import com.erimkorkmaz.quizapp.utils.makeCircularAnonymousImage
import kotlinx.android.synthetic.main.item_categories.view.*
import kotlinx.android.synthetic.main.list_item_leaderboard.view.*

class LeaderboardAdapter(
    private val triple: ArrayList<Triple<User, String, Int>>,
    private val isCategory: Boolean,
    private val category: String? = "Animals"
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (isCategory) {
            CategoryViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_categories, parent, false)
            )
        } else {
            LeaderboardViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_leaderboard, parent, false)
            )
        }
    }

    override fun getItemCount(): Int {
        return if (isCategory) {
            getCategories().size
        } else {
            orderUsers().size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryViewHolder -> holder.bind(
                getCategories()[position],
                position,
                getCategories().size
            )
            is LeaderboardViewHolder -> holder.bind(orderUsers()[position])
        }
    }

    private fun getCategories(): MutableList<String> {
        val categories = arrayListOf<String>()
        for (value in triple) {
            categories.add(value.second)
        }
        return categories.distinct().sorted().toMutableList()
    }

    private fun orderUsers(): MutableList<Pair<User, Int>> {
        val scoreBoard = mutableListOf<Pair<User, Int>>()
        for (value in triple) {
            if (value.second == category) {
                scoreBoard.add(Pair(value.first, value.third))
            }
        }
        scoreBoard.distinct().sortedByDescending { it.second.toLong() }
        return scoreBoard
    }
}

class CategoryViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    private lateinit var category: String

    fun bind(category: String, position: Int, size: Int) {
        this.category = category
        itemView.text_category.text = StringUtils.formatCategoryNames(category)
        when (position) {
            0 -> {
                itemView.text_category.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    itemView.context.getDrawable(R.drawable.ic_arrow_forward_black_24dp),
                    null
                )
            }
            size - 1 -> {
                itemView.text_category.setCompoundDrawablesWithIntrinsicBounds(
                    itemView.context.getDrawable(R.drawable.ic_arrow_back_black_24dp),
                    null,
                    null,
                    null
                )
            }
            else -> {
                itemView.text_category.setCompoundDrawablesWithIntrinsicBounds(
                    itemView.context.getDrawable(R.drawable.ic_arrow_back_black_24dp),
                    null,
                    itemView.context.getDrawable(R.drawable.ic_arrow_forward_black_24dp),
                    null
                )
            }
        }

    }
}

class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(user: Pair<User, Int>) {
        Glide.with(itemView.context).load(user.first.profileImageUrl).apply(
            RequestOptions().circleCrop().placeholder(
                makeCircularAnonymousImage(
                    itemView.context,
                    R.drawable.ic_anonymous
                )
            )
                .error(makeCircularAnonymousImage(itemView.context, R.drawable.ic_anonymous))
        ).into(itemView.image_score_board_user)
        itemView.text_username.text = user.first.userName
        itemView.text_score.text = user.second.toString()
    }
}