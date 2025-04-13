package com.quangduy.chatapp.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppSettingImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AppSetting {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

    private val dataStore by lazy { context.dataStore }

    override suspend fun setValue(key: String, value: String) {
        val dataKey = stringPreferencesKey(key)
        dataStore.edit { prefs ->
            prefs[dataKey] = value
        }
    }

    override fun getValue(key: String): Flow<String?> {
        val dataKey = stringPreferencesKey(key)
        return dataStore.data.map { prefs ->
            prefs[dataKey]
        }
    }

    override suspend fun setIntValue(key: String, value: Int) {
        val dataKey = intPreferencesKey(key)
        dataStore.edit { prefs ->
            prefs[dataKey] = value
        }
    }

    override fun getIntValue(key: String, defaultValue: Int): Flow<Int> {
        val dataKey = intPreferencesKey(key)
        return dataStore.data.map { prefs ->
            prefs[dataKey] ?: defaultValue
        }
    }
}