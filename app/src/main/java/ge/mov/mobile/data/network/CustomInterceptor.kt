package ge.mov.mobile.data.network

import okhttp3.Interceptor
import okhttp3.Response
import kotlin.jvm.Throws

class CustomInterceptor : Interceptor {
    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11")
            .build()
        return chain.proceed(request)
    }
}