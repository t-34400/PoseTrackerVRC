# Android Pose Tracker for VRChat

This Android application performs pose estimation using the device's camera and simulates full-body tracking in VRChat by sending OSC (Open Sound Control) messages.

## Prerequisites

Before using the app, follow these steps:

1. **Launch VRChat in VR Mode:**
    - Start VRChat in VR mode.

2. **Enable OSC in VRChat:**
    - Navigate to VRChat's in-game Action menu.
    - Go to OSC > Enabled and set it to On.

3. **Configure Firewall and Obtain IP Address:**
    - Identify the IP address of the device running VRChat.
    - Set up your firewall to allow UDP communication on port 9000.

## Usage

1. **Launch the App:**
    - Start the application on your Android device.

2. **Configure Remote Address:**
    - Press the settings button in the top right.
    - Enter the IP address obtained during the setup into the `UDP Remote Address` field in the dialog and press `OK`.

3. **Perform Camera Pose Estimation:**
    - Press the `Camera Pose Estimation` button.

## License
[MIT License](./LICENSE)