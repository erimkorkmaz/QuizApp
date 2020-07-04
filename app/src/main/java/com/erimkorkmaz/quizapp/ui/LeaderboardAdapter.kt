package com.erimkorkmaz.quizapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.StringUtils
import kotlinx.android.synthetic.main.item_categories.view.*
import kotlinx.android.synthetic.main.list_item_leaderboard.view.*
import kotlin.collections.ArrayList

class LeaderboardAdapter(
    private val listener: CategoryClickListener,
    private val triple: ArrayList<Triple<String, String, Int>>,
    private val isCategory: Boolean,
    private val category: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val scoreBoard = hashMapOf<String, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (isCategory) {
            CategoryViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_categories, parent, false), listener
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
            getUserName().size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryViewHolder -> holder.bind(getCategories()[position], position)
            is LeaderboardViewHolder -> holder.bind(
                position + 1,
                getUserName()[position],
                getScores()[position]
            )
        }
    }

    private fun getCategories(): MutableList<String> {
        val categories = arrayListOf<String>()
        for (value in triple) {
            categories.add(value.second)
        }
        return categories.distinct().sorted().toMutableList()
    }

    private fun getUserName(): MutableList<String> {
        val userNames = arrayListOf<String>()
        for (value in triple) {
            if (value.second == category) {
                scoreBoard[value.first] = value.third
            }
        }
        val sortedMap = scoreBoard.toSortedMap(compareByDescending { it })
        for (name in sortedMap) {
            userNames.add(name.key)
        }
        return userNames
    }

    private fun getScores(): MutableList<Int> {
        val scores = arrayListOf<Int>()
        val sortedMap = scoreBoard.toSortedMap(compareByDescending { it })
        for (score in sortedMap) {
            scores.add(score.value)
        }
        return scores
    }
}

class CategoryViewHolder(itemView: View, private val listener: CategoryClickListener) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {
    private lateinit var category: String

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(category: String, position: Int) {
        this.category = category
        itemView.text_category.text = StringUtils.formatCategoryNames(category)
        if (position == 0) {
            itemView.text_category.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                itemView.context.getDrawable(R.drawable.ic_arrow_forward_black_24dp),
                null
            )
        } else if (position == 23) {
            itemView.text_category.setCompoundDrawablesWithIntrinsicBounds(
                itemView.context.getDrawable(R.drawable.ic_arrow_back_black_24dp),
                null,
                null,
                null
            )
        } else {
            itemView.text_category.setCompoundDrawablesWithIntrinsicBounds(
                itemView.context.getDrawable(R.drawable.ic_arrow_back_black_24dp),
                null,
                itemView.context.getDrawable(R.drawable.ic_arrow_forward_black_24dp),
                null
            )
        }

    }

    override fun onClick(view: View) {
        listener.onCategoryClicked(category)
    }
}

class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(index: Int, username: String, score: Int) {
        itemView.text_username.text = "$index. $username"
        itemView.text_score.text = score.toString()

    }
}