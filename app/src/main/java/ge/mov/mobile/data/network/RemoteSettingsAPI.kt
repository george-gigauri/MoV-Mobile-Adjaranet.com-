package ge.mov.mobile.data.network

import ge.mov.mobile.BuildConfig
import ge.mov.mobile.BuildConfig.APP_ID
import ge.mov.mobile.data.model.DeveloperInfo
import ge.mov.mobile.data.model.RemoteSettings
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RemoteSettingsAPI {
    @GET("master/remote_settings.json")
    //@GET("apps/${APP_ID}")
    suspend fun getRemoteSettings() : Response<RemoteSettings?>

    @GET("/developer/{id}")
    suspend fun getDeveloper(@Path("id") id: Int) : Response<DeveloperInfo>
}