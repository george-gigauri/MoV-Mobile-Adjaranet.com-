package ge.mov.mobile.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.res.Configuration
import ge.mov.mobile.util.Constants.SHARED_PREFERENCES_LANGUAGE
import ge.mov.mobile.util.Constants.SHARED_PREFERENCES_USER
import java.util.*

class Utils {
    companion object {
        fun getCurrentLanguage(): String {
            return Locale.getDefault().displayLanguage
        }

        fun saveLanguage(context: Context, language: String) {
            val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_USER, MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putString(SHARED_PREFERENCES_LANGUAGE, language)
            editor.apply()

           // setLanguage(context, language)
        }

        fun loadLanguage(context: Context): String? {
            val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_USER, MODE_PRIVATE)
            return sharedPreferences.getString(SHARED_PREFERENCES_LANGUAGE, "en_US")
        }

        private fun setLanguage(context: Context, language: String) {
            val locale = Locale(language)
            Locale.setDefault(locale)

            val config = Configuration()
            config.locale = locale

            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }
}