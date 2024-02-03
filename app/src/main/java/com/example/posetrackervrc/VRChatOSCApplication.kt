package com.example.posetrackervrc

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.posetrackervrc.data.repositories.PoseEstimationSettingsRepository
import com.example.posetrackervrc.data.repositories.UDPSettingsRepository

class VRChatOSCApplication: Application() {
    lateinit var udpSettingsRepository: UDPSettingsRepository
    lateinit var poseEstimationSettingsRepository: PoseEstimationSettingsRepository

    override fun onCreate() {
        super.onCreate()
        udpSettingsRepository = UDPSettingsRepository(dataStore)
        poseEstimationSettingsRepository = PoseEstimationSettingsRepository(dataStore)
    }
}

private const val SETTINGS_DATASTORE_NAME = "settings"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = SETTINGS_DATASTORE_NAME
)