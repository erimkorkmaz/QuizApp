package com.erimkorkmaz.quizapp.repository

import androidx.lifecycle.LiveData
import com.erimkorkmaz.quizapp.model.Category
import com.erimkorkmaz.quizapp.model.Question
import com.erimkorkmaz.quizapp.model.User

interface Repository {
    fun getCategories() : LiveData<List<Category>>

    fun getQuestions(categoryId : String) : LiveData<List<Question>>

}