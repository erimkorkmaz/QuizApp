package com.erimkorkmaz.quizapp.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.erimkorkmaz.quizapp.R
import com.google.gson.Gson

fun String.htmlDecode(): String {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString();
}

fun String.base64Decode(): String {
    return Base64.decode(this, Base64.DEFAULT).toString(charset("UTF-8"))
}

fun FragmentActivity.toolbarTitle(title: String) {
    val toolbarTitle =
        this.findViewById<AppCompatTextView>(R.id.text_toolbar_title_common)
    toolbarTitle.text = title
}

fun FragmentActivity.toolbarIcon(drawableId: Int) {
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

fun Fragment.toolbarTitle(title: String) {
    val toolbarTitle =
        requireActivity().findViewById<AppCompatTextView>(R.id.text_toolbar_title_common)
    toolbarTitle.text = title
}

fun Fragment.toolbarIcon(drawableId: Int) {
    val toolbarCommon: Toolbar = requireActivity().findViewById(R.id.toolbar_common)
    val drawable = ContextCompat.getDrawable(requireContext(), drawableId)
    toolbarCommon.navigationIcon = drawable
}

fun Fragment.toolbarRightIcon(drawableId: Int) {
    val toolbarRightIcon: AppCompatImageView =
        requireActivity().findViewById(R.id.image_toolbar_right_common)
    toolbarRightIcon.visibility = View.VISIBLE
    val drawable = ContextCompat.getDrawable(requireContext(), drawableId)
    toolbarRightIcon.setImageDrawable(drawable)
}

fun Fragment.hideToolbarRightIcon() {
    val toolbarRightIcon: AppCompatImageView =
        requireActivity().findViewById(R.id.image_toolbar_right_common)
    toolbarRightIcon.visibility = View.GONE
}

fun convertMapToPOJO(data: Map<String, Any>, anyClass: Class<out Any>): Any {
    val jsonElement = Gson().toJsonTree(data)
    return Gson().fromJson(jsonElement, anyClass)
}

fun makeCircularAnonymousImage(context: Context, placeHolderResourceId: Int): Drawable {
    val placeholder =
        BitmapFactory.decodeResource(context.resources, placeHolderResourceId)
    val circularBitmapDrawable =
        RoundedBitmapDrawableFactory.create(context.resources, placeholder)
    circularBitmapDrawable.isCircular = true
    return circularBitmapDrawable
}