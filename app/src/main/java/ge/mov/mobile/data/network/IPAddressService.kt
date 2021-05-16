package ge.mov.mobile.data.network

import ge.mov.mobile.data.model.IPAddress
import retrofit2.Response
import retrofit2.http.GET

interface IPAddressService {

    @GET("json")
    suspend fun getMyIp(): Response<IPAddress>
}