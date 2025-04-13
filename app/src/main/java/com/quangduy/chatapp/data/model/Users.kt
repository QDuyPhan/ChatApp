package com.quangduy.chatapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Users(
    val userid: String? = "",
    val status: String? = "",
    val imageUrl: String? = "",
    val username: String? = "",
    val email: String? = "",
) : Parcelable