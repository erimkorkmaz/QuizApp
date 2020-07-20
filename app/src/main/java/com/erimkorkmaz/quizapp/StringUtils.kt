package com.erimkorkmaz.quizapp

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

    fun String.htmlDecode() : String {
        return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString();
    }

    fun String.base64Decode(): String {
        return Base64.decode(this, Base64.DEFAULT).toString(charset("UTF-8"))
    }
}