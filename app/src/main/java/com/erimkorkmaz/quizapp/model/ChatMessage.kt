package com.erimkorkmaz.quizapp.model

data class ChatMessage(
    val text: String,
    val fromId: String,
    val toId: String,
    val timeStamp: String,
    val fromUsername: String,
    val toUsername: String
) {
}