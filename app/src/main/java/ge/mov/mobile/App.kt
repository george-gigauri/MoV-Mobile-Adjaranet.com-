package ge.mov.mobile

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import ge.mov.mobile.util.Utils
import java.util.*

class App: Application() {
    private lateinit var locale: Locale
    private lateinit var context: Context

    override fun onCreate() {
        super.onCreate()

        context = this
        changeLanguage("ka-rGE")
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (this::locale.isInitialized)
        {
            Locale.setDefault(locale)
            val config = Configuration(newConfig)
            config.locale = (locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }

    fun changeLanguage(lang: String) {
        val config = applicationContext.resources.configuration
        Utils.saveLanguage(context, lang)

        locale = Locale(lang)
        Locale.setDefault(locale)

        config.locale = locale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}