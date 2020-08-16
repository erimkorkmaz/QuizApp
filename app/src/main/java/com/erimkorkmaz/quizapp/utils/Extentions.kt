package com.erimkorkmaz.quizapp.utils

import android.app.Activity
import android.util.Base64
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentActivity
import com.erimkorkmaz.quizapp.R

fun String.htmlDecode() : String {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString();
}

fun String.base64Decode(): String {
    return Base64.decode(this, Base64.DEFAULT).toString(charset("UTF-8"))
}

fun Activity.toolbarTitle(title: String) {
    val toolbarTitle =
        this.findViewById<AppCompatTextView>(R.id.text_toolbar_title_common)
    toolbarTitle.text = title
}

fun Activity.toolbarIcon(drawableId: Int) {
    val toolbarCommon: Toolbar = this.findViewById(R.id.toolbar_common)
    val drawable = ContextCompat.getDrawable(applicationContext, drawableId)
    toolbarCommon.navigationIcon = drawable
}

fun FragmentActivity.toolbarRightIcon(drawableId: Int) {
    val toolbarRightIcon: AppCompatImageView = findViewById(R.id.image_toolbar_right_common)
    toolbarRightIcon.visibility = View.VISIBLE
    val drawable = ContextCompat.getDrawable(this, drawableId)
    toolbarRightIcon.setImageDrawable(drawable)
}