package com.erimkorkmaz.quizapp.ui.messaging

import com.erimkorkmaz.quizapp.model.User

interface NewMessageItemClickListener {
    fun userItemClicked(user: User)
}