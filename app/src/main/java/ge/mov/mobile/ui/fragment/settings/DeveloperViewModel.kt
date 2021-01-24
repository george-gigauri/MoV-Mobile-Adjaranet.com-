package ge.mov.mobile.ui.fragment.settings

import androidx.lifecycle.ViewModel
import ge.mov.mobile.util.RemoteSettingsUtil

class DeveloperViewModel: ViewModel() {
    private val remoteConfig = RemoteSettingsUtil.getRemoteSettings()

    fun getDeveloperInfo() = remoteConfig.body()?.developer

    fun loadImage() = remoteConfig.body()?.developer?.photo

    fun loadEmail() = remoteConfig.body()?.developer?.email

    fun getSocial() = remoteConfig.body()?.developer?.social
}