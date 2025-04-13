package com.quangduy.chatapp.ultils

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date

object Constants {
    const val NETWORK_TIMEOUT = 60L

    const val DEFAULT_TIMEOUT = 30L

    const val REQUEST_IMAGE_CAPTURE = 1
    const val REQUEST_IMAGE_PICK = 2
    const val MESSAGE_RIGHT = 1
    const val MESSAGE_LEFT = 2
    const val CHANNEL_ID = "com.quangduy.chatapp"


    private val auth = FirebaseAuth.getInstance()
    private var userid: String = ""

    fun getUidLoggedIn(): String {
        if (auth.currentUser != null)
            userid = auth.currentUser!!.uid
        return userid
    }

    @SuppressLint("SimpleDateFormat")
    fun getTime(): String {
        val formatter = SimpleDateFormat("HH:mm:ss")
        val date: Date = Date(System.currentTimeMillis())
        val stringdate = formatter.format(date)
        return stringdate
    }
}