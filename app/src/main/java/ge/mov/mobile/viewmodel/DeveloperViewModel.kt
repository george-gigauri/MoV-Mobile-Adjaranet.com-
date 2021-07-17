package ge.mov.mobile.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.mov.mobile.util.RemoteSettingsUtil
import javax.inject.Inject

@HiltViewModel
class DeveloperViewModel @Inject constructor(): ViewModel() {

    private val remoteConfig = RemoteSettingsUtil.getRemoteSettings()

    fun getDeveloperInfo() = remoteConfig.body()?.developer

    fun loadImage() = remoteConfig.body()?.developer?.photo

    fun loadEmail() = remoteConfig.body()?.developer?.email

    fun getSocial() = remoteConfig.body()?.developer?.social
}