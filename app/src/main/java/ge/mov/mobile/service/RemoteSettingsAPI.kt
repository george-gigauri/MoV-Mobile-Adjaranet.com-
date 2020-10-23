package ge.mov.mobile.service

import ge.mov.mobile.model.RemoteSettings
import retrofit2.http.GET

interface RemoteSettingsAPI {
    @GET("master/remote_settings")
    suspend fun getRemoteSettings() : RemoteSettings?
}