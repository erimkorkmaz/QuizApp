package com.erimkorkmaz.quizapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.erimkorkmaz.quizapp.Injection
import com.erimkorkmaz.quizapp.model.Category

class CategoriesViewModel: ViewModel() {

    private val repository = Injection.provideRepository()
    var categoryLiveData: LiveData<List<Category>>

    init {
        categoryLiveData = repository.getCategories()
    }
}