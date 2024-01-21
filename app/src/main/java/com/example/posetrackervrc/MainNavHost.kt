package com.example.posetrackervrc

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.posetrackervrc.ui.camera.CameraPoseEstimationScreen
import com.example.posetrackervrc.ui.main.MainScreen
import com.example.posetrackervrc.ui.video.VideoPoseEstimationScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainDestinationProperties.route,
        modifier = modifier
    ) {
        composable(route = MainDestinationProperties.route) {
            MainScreen(
                onCameraButtonClick = {
                    navController.navigateSingleTopTo(CameraPoseEstimationDestinationProperties.route)
                },
                onVideoButtonClick = {
                    navController.navigateSingleTopTo(VideoPoseEstimationDestinationProperties.route)
                }
            )
        }
        composable(route = CameraPoseEstimationDestinationProperties.route) {
            CameraPoseEstimationScreen()
        }
        composable(route = VideoPoseEstimationDestinationProperties.route) {
            VideoPoseEstimationScreen()
        }
    }
}

private fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }