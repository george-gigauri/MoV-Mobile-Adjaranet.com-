package ge.mov.mobile

import android.content.Context
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider

class CastOptionsProvider : OptionsProvider {
    override fun getCastOptions(p0: Context?): CastOptions = CastOptions.Builder()
        .setReceiverApplicationId(BuildConfig.CAST_APP_ID)
        .build()

    override fun getAdditionalSessionProviders(p0: Context?): MutableList<SessionProvider>? = null
}