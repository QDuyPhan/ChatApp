package com.quangduy.chatapp.ultils

import timber.log.Timber

object Logger {
    fun logE(message: String) {
        Timber.e(message)
    }

    fun logI(message: String) {
        Timber.i(message)
    }

    fun logD(message: String) {
        Timber.d(message)
    }

    fun logW(message: String) {
        Timber.w(message)
    }

    fun plant() {
        Timber.plant(Timber.DebugTree())
    }
}