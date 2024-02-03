package com.example.posetrackervrc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.posetrackervrc.ui.camera.PoseViewModel
import com.example.posetrackervrc.ui.theme.PoseTrackerVRCTheme
import com.example.posetrackervrc.viewmodel.UDPViewModel

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
            val snackbarHostState = remember { SnackbarHostState() }
            val udpViewModel: UDPViewModel = viewModel(
                factory = UDPViewModel.Factory
            )
            val poseViewModel: PoseViewModel = viewModel(
                factory = PoseViewModel.Factory
            )

            Scaffold(
                topBar = {
                         MainTopBar(
                             properties = destinationProperties,
                             onBackButtonClicked = { navController.popBackStack() },
                             udpViewModel = udpViewModel,
                             poseViewModel = poseViewModel
                         )
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                }
            ) { innerPadding ->
                MainNavHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                    snackbarHostState = snackbarHostState,
                    udpViewModel = udpViewModel,
                    poseViewModel = poseViewModel
                )
            }
        }
    }
}