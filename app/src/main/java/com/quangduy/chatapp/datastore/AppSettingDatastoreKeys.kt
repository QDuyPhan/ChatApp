package com.quangduy.chatapp.datastore

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object AppSettingDatastoreKeys {
    val STRING_KEY = stringPreferencesKey("string_key")
    val INT_KEY = intPreferencesKey("int_key")
}