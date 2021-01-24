package ge.mov.mobile.util

import ge.mov.mobile.data.network.RemoteSettingsAPI
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RemoteSettingsUtil {
    private val retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://raw.githubusercontent.com/george-gigauri/MoV-Mobile-Adjaranet.com-/")
            .build()
            .create(RemoteSettingsAPI::class.java)


    val remoteAppVersion = runBlocking { retrofit.getRemoteSettings().body()?.remoteVersionCode }

    fun getRemoteSettings() = runBlocking { retrofit.getRemoteSettings() }

    fun getDeveloperInfo() = runBlocking { retrofit.getRemoteSettings().body()?.developer }

    fun isAppEnabled() = runBlocking { retrofit.getRemoteSettings().body()?.isAppEnabled }
}