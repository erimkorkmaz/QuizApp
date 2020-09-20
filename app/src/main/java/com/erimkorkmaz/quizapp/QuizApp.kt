package com.erimkorkmaz.quizapp

import android.app.Application

class QuizApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ModelPreferencesManager.with(this)
    }
}