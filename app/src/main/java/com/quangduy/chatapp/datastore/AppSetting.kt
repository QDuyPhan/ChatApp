package com.quangduy.chatapp.datastore

import kotlinx.coroutines.flow.Flow

interface AppSetting {
    suspend fun setValue(key: String, value: String)
    fun getValue(key: String): Flow<String?>
    suspend fun setIntValue(key: String, value: Int)
    fun getIntValue(key: String, defaultValue: Int): Flow<Int>
}