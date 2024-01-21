package com.example.posetrackervrc

interface DestinationProperties {
    val route: String
    val label: String
}

object MainDestinationProperties : DestinationProperties {
    override val route: String
        get() = "main"
    override val label: String
        get() = "Pose Tracker VRC"
}

object CameraPoseEstimationDestinationProperties : DestinationProperties {
    override val route: String
        get() = "camera_pose_estimation"
    override val label: String
        get() = "Camera Pose Estimation"
}

object VideoPoseEstimationDestinationProperties : DestinationProperties {
    override val route: String
        get() = "video_pose_estimation"
    override val label: String
        get() = "Video Pose Estimation"
}

val destinationPropertiesList = listOf(MainDestinationProperties, CameraPoseEstimationDestinationProperties, VideoPoseEstimationDestinationProperties)