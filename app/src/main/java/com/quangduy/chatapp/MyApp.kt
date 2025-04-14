package com.quangduy.chatapp

import android.app.Application
import com.cloudinary.android.MediaManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        val config: HashMap<String, String> = HashMap()
        config["cloud_name"] = BuildConfig.YOUR_CLOUD_NAME
        config["api_key"] = BuildConfig.YOUR_API_KEY
        config["api_secret"] = BuildConfig.YOUR_API_SECRET

        MediaManager.init(this, config)
    }
}