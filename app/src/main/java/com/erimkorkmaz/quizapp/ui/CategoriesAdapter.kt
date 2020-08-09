package com.erimkorkmaz.quizapp.ui


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.utils.StringUtils
import com.erimkorkmaz.quizapp.model.Category
import kotlinx.android.synthetic.main.list_item_categories.view.*

class CategoriesAdapter(val categories : MutableList<Category>, val images : Array<Int>) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_categories,parent,false))
    }

    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position],images[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private lateinit var category : Category

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(category: Category, image: Int) {
            this.category = category
            itemView.text_category.text = StringUtils.formatCategoryNames(category.name)
            itemView.image_category.setImageResource(image)

        }

        override fun onClick(view: View) {
            val intent = QuizActivity.newIntent(view.context, category)
            view.context.startActivity(intent)
        }
    }
}