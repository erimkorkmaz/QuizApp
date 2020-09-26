package com.erimkorkmaz.quizapp.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashViewModel : ViewModel() {

    val isFinished = MutableLiveData<Boolean>()

    init {
        object : CountDownTimer(2500L, 1000) {

            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                isFinished.postValue(true)
            }
        }.start()
    }
}