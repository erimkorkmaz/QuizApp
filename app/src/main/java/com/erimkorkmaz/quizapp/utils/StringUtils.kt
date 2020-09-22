package com.erimkorkmaz.quizapp.utils


object StringUtils {

    @JvmStatic
    fun formatCategoryNames(text: String): String {
        if (!text.contains(":")) {
            return text
        }
        val index = text.indexOf(":")
        return text.drop(index + 1)
    }
}