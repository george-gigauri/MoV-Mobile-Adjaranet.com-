package ge.mov.mobile.di.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import ge.mov.mobile.BuildConfig
import ge.mov.mobile.data.network.APIService
import ge.mov.mobile.data.network.CustomInterceptor
import ge.mov.mobile.data.network.RemoteSettingsAPI
import ge.mov.mobile.util.Constants
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    val client = okhttp3.OkHttpClient.Builder()
        .addInterceptor(CustomInterceptor())
        .also { client ->
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                client.addInterceptor(logging)
            }
        }

    @Singleton
    @Provides
    fun getRetrofit() : Retrofit = Retrofit.Builder()
        .client(client.build())
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    // For remote config
    fun getRemoteRetrofit() : Retrofit =
        Retrofit.Builder()
            .client(client.build())
            //.baseUrl("https://remoteconfigmanagement.herokuapp.com/")
            .baseUrl("https://raw.githubusercontent.com/george-gigauri/MoV-Mobile-Adjaranet.com-/")
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun getMoviesApi(retrofit: Retrofit) = retrofit.create(APIService::class.java)

    @Singleton
    @Provides
    fun getRemoteApi(retrofit: Retrofit) = retrofit.create(RemoteSettingsAPI::class.java)
}