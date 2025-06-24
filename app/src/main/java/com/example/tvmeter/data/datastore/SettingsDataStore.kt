package com.example.tvmeter.data.datastore

// SettingsDataStore.kt
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        val PIN_KEY = stringPreferencesKey("pin")
        val AUTO_LOCK_ENABLED_KEY = booleanPreferencesKey("auto_lock_enabled")
        val AUTO_LOCK_TIME_KEY = intPreferencesKey("auto_lock_time")
    }

    val pin: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PIN_KEY] ?: ""
    }

    val autoLockEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AUTO_LOCK_ENABLED_KEY] ?: false
    }

    val autoLockTime: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[AUTO_LOCK_TIME_KEY] ?: 60
    }

    suspend fun savePin(pin: String) {
        context.dataStore.edit { preferences ->
            preferences[PIN_KEY] = pin
        }
    }

    suspend fun saveAutoLockEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_LOCK_ENABLED_KEY] = enabled
        }
    }

    suspend fun saveAutoLockTime(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_LOCK_TIME_KEY] = minutes
        }
    }
}
