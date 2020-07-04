package com.erimkorkmaz.quizapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.erimkorkmaz.quizapp.Injection
import com.erimkorkmaz.quizapp.model.Question

class QuizViewModel() : ViewModel() {
    private val repository = Injection.provideRepository()

    fun getQuestions(categoryId: String) : LiveData<List<Question>>{
        return repository.getQuestions(categoryId)
    }



}