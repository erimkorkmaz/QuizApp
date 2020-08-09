package com.erimkorkmaz.quizapp.utils

import android.util.Base64
import androidx.core.text.HtmlCompat


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