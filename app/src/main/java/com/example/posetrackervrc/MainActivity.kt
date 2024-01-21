package com.example.posetrackervrc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.posetrackervrc.ui.theme.PoseTrackerVRCTheme
import com.example.posetrackervrc.ui.topbar.MainTopBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PoseTrackerVRC()
        }
    }

    @Composable
    private fun PoseTrackerVRC() {
        PoseTrackerVRCTheme {
            val navController = rememberNavController()
            val currentBackStack by navController.currentBackStackEntryAsState()
            val currentDestination = currentBackStack?.destination
            val destinationProperties =
                destinationPropertiesList.find { it.route == currentDestination?.route } ?: MainDestinationProperties
            Scaffold(
                topBar = {
                        MainTopBar(
                            label = destinationProperties.label,
                            showBackButton = destinationProperties != MainDestinationProperties,
                            onBackButtonClicked = { navController.popBackStack() }
                        )
                }
            ) { innerPadding ->
                MainNavHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }

    @Preview
    @Composable
    private fun PoseTrackerVRCPreview() {
        PoseTrackerVRC()
    }
}