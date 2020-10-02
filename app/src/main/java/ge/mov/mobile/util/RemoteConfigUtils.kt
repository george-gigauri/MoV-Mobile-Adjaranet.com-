package ge.mov.mobile.util

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object RemoteConfigUtils {
    fun getRemoteConfig() : FirebaseRemoteConfig {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.fetch(0)
        remoteConfig.activate()
        return remoteConfig
    }
}