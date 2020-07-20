package com.erimkorkmaz.quizapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.erimkorkmaz.quizapp.Injection
import com.erimkorkmaz.quizapp.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RemoteRepository : Repository {


    private const val AMOUNT = "10"
    private const val TYPE = "multiple"
    private const val ENCODING_TYPE = "base64"

    private val api = Injection.provideService()

    override fun getCategories(): LiveData<List<Category>> {
        val liveData = MutableLiveData<List<Category>>()

        api.getCategories().enqueue(object : Callback<CategoryResponse> {
            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<CategoryResponse>,
                response: Response<CategoryResponse>?
            ) {
                if (response != null) {
                    liveData.value = response.body()?.trivia_categories
                }
            }
        })
        return liveData
    }

    override fun getQuestions(categoryId: String): LiveData<List<Question>> {
        val liveData = MutableLiveData<List<Question>>()

        api.getQuestions(AMOUNT, categoryId, TYPE)
            .enqueue(object : Callback<QuestionsResponse> {
                override fun onFailure(call: Call<QuestionsResponse>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<QuestionsResponse>,
                    response: Response<QuestionsResponse>
                ) {
                    if (response != null) {
                        liveData.value = response.body()?.results
                    }
                }
            })
        return liveData
    }
}