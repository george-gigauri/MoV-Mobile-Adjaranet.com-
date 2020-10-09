package ge.mov.mobile.util

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import ge.mov.mobile.R
import kotlinx.coroutines.runBlocking
import java.util.*

object RemoteConfigUtils {
    fun getRemoteConfig() : FirebaseRemoteConfig = runBlocking {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.fetch(0)
        remoteConfig.activate()
        remoteConfig
    }

    fun isEnabled() : Boolean = runBlocking {
        getRemoteConfig().getString("isAppEnabled").toUpperCase(Locale.ROOT) == "YES"
    }
}