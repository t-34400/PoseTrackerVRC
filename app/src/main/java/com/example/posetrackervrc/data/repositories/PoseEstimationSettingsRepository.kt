package com.example.posetrackervrc.data.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class PoseEstimationSettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val TAG = PoseEstimationSettingsRepository::class.simpleName
        val SHOULDER_WIDTH_KEY = floatPreferencesKey("SHOULDER_WIDTH")
        val Z_ADJUSTMENT_FACTOR = floatPreferencesKey("Z_ADJUSTMENT_FACTOR")
    }

    val shoulderWidth: Flow<Float> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[SHOULDER_WIDTH_KEY] ?: 45.0f
        }
    val zAdjustmentFactor: Flow<Float> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[Z_ADJUSTMENT_FACTOR] ?: 0.2f
        }

    suspend fun savePoseEstimationSettings(shoulderWidth: Float, zAdjustmentFactor: Float) {
        dataStore.edit { preferences ->
            preferences[SHOULDER_WIDTH_KEY] = shoulderWidth
            preferences[Z_ADJUSTMENT_FACTOR] = zAdjustmentFactor
        }
    }
}