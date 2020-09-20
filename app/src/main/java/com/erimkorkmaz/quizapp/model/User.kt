package com.erimkorkmaz.quizapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val uid: String,
    val email: String,
    val userName: String,
    val profileImageUrl: String? = null
) : Parcelable {
}