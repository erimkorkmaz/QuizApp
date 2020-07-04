package com.erimkorkmaz.quizapp.repository

import com.erimkorkmaz.quizapp.model.CategoryResponse
import com.erimkorkmaz.quizapp.model.QuestionsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Service {

    @GET("api_category.php")
    fun getCategories() : Call<CategoryResponse>

    @GET("api.php?")
    fun getQuestions(@Query("amount") amount : String, @Query("category") category : String,
                     @Query("type") type : String ) : Call<QuestionsResponse>
}