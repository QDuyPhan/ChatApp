package com.quangduy.chatapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecentChats(
    val friendId: String? = "",
    val friendsImage: String? = "",
    val time: String? = "",
    val name: String? = "",
    val sender: String? = "",
    val message: String? = "",
    val person: String? = "",
    val status: String? = "",
) : Parcelable