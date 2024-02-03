package com.example.posetrackervrc.data.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UDPSettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val TAG = UDPSettingsRepository::class.simpleName
        val REMOTE_ADDRESS_KEY = stringPreferencesKey("REMOTE_ADDRESS")
        val REMOTE_PORT_KEY = intPreferencesKey("REMOTE_PORT")
    }

    val remoteAddress: Flow<String> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[REMOTE_ADDRESS_KEY] ?: "192.168.1.1"
        }
    val remotePort: Flow<Int> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[REMOTE_PORT_KEY] ?: 9000
        }

    suspend fun saveUDPSettings(remoteAddress: String, remotePort: Int) {
        Log.d(TAG, "Save UDP Settings(Address: $remoteAddress, Port: $remotePort)")
        dataStore.edit { preferences ->
            preferences[REMOTE_ADDRESS_KEY] = remoteAddress
            preferences[REMOTE_PORT_KEY] = remotePort
        }
    }
}