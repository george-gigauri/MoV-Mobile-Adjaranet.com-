package ge.mov.mobile.util

import ge.mov.mobile.service.RemoteSettingsAPI
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RemoteSettingsUtil {
    private val retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://raw.githubusercontent.com/george-gigauri/MoV-Mobile-Adjaranet.com-/")
            .build()
            .create(RemoteSettingsAPI::class.java)
    }

    fun getRemoteSettings() = runBlocking { retrofit.getRemoteSettings() }

    fun getDeveloperInfo() = runBlocking { retrofit.getRemoteSettings()?.developer }

    fun isAppEnabled() = runBlocking { retrofit.getRemoteSettings()?.isAppEnabled }
}