# Android VRChat OSC トラッキング アプリ

このAndroidアプリケーションは、デバイスのカメラを使用してポーズ推定を行い、OSC（Open Sound Control）メッセージを使用してVRChatでフルボディトラッキングをシミュレートします。

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![CodeFactor](https://www.codefactor.io/repository/github/t-34400/posetrackervrc/badge)](https://www.codefactor.io/repository/github/t-34400/posetrackervrc)

## 準備

アプリを使用する前に、以下の手順に従ってください：

1. **VRモードでVRChatを起動:**
    - VRChatをVRモードで起動してください。

2. **VRChat内でOSCを有効にする:**
    - VRChatのゲーム内アクションメニューに移動します。
    - OSC > Enabledに移動し、Onに設定します。

3. **ファイアウォールの設定とIPアドレスの取得:**
    - VRChatを実行しているデバイスのIPアドレスを特定します。
    - ファイアウォールを設定して、ポート9000でのUDP通信を許可します。

## 使用方法

1. **アプリを起動:**
    - Androidデバイスでアプリケーションを起動します。

2. **リモートアドレスを設定:**
    - 右上の設定ボタンを押します。
    - ダイアログの`UDP Remote Address`にセットアップ時に取得したIPアドレスを入力し、「OK」ボタンを押します。

3. **カメラポーズ推定を実行:**
    - `Camera Pose Estimation`ボタンを押します。

## License
[MIT License](./LICENSE)