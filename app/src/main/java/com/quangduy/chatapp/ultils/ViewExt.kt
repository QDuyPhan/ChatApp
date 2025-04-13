package com.quangduy.chatapp.ultils

import android.annotation.SuppressLint
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.quangduy.chatapp.listenner.OnSingleClickListener
import java.text.SimpleDateFormat
import java.util.Date

fun View.setOnSingClickListener(onClick: (View) -> Unit) {
    setOnClickListener(object : OnSingleClickListener() {
        override fun onSingleClick(view: View) {
            onClick.invoke(view)
        }
    })
}

