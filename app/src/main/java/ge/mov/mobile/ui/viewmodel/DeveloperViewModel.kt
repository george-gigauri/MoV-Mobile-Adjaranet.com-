package ge.mov.mobile.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bumptech.glide.Glide
import ge.mov.mobile.R
import ge.mov.mobile.util.RemoteConfigUtils

class DeveloperViewModel: ViewModel() {
    private val remoteConfig = RemoteConfigUtils.getRemoteConfig()

    fun loadImage() = remoteConfig.getString("developer_profile_picture")

    fun loadEmail() = remoteConfig.getString("developer_email")

    fun getFacebook() = remoteConfig.getString("developer_facebook")

    fun getInstagram() = remoteConfig.getString("developer_instagram")

    fun getLinkedIn() = remoteConfig.getString("developer_linkedin")
}