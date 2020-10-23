package ge.mov.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import ge.mov.mobile.util.RemoteSettingsUtil

class DeveloperViewModel: ViewModel() {
    private val remoteConfig = RemoteSettingsUtil.getRemoteSettings()

    fun getDeveloperInfo() = remoteConfig?.developer

    fun loadImage() = remoteConfig?.developer?.photo

    fun loadEmail() = remoteConfig?.developer?.email

    fun getSocial() = remoteConfig?.developer?.social
}