package ge.mov.mobile

import android.app.Application
import android.content.res.Configuration
import ge.mov.mobile.util.Utils

class MovApplication : Application() {
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val myLang = Utils.loadLanguage(this)
        if (myLang != null) {
            Utils.saveLanguage(this, myLang)
        }
    }
}