package ge.mov.mobile.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.res.Configuration
import ge.mov.mobile.model.LocaleModel
import ge.mov.mobile.util.Constants.SHARED_PREFERENCES_LANG_LANGCODE
import ge.mov.mobile.util.Constants.SHARED_PREFERENCES_LANG_LANGID
import ge.mov.mobile.util.Constants.SHARED_PREFERENCES_LANG_LANGNAME
import ge.mov.mobile.util.Constants.SHARED_PREFERENCES_USER
import java.util.*

class Utils {
    companion object {
        fun getCurrentLanguage(): String {
            return Locale.getDefault().displayLanguage
        }

        fun saveLanguage(context: Context, language: LocaleModel) {
            val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_USER, MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putString(SHARED_PREFERENCES_LANG_LANGID, language.id)
            editor.putString(SHARED_PREFERENCES_LANG_LANGCODE, language.code)
            editor.putString(SHARED_PREFERENCES_LANG_LANGNAME, language.name)
            editor.apply()

            setLanguage(context, language)
        }

        fun loadLanguage(context: Context): LocaleModel? {
            val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_USER, MODE_PRIVATE)

            val localeModel = LocaleModel()
            localeModel.id = sharedPreferences.getString(SHARED_PREFERENCES_LANG_LANGID, "en")!!
            localeModel.code = sharedPreferences.getString(SHARED_PREFERENCES_LANG_LANGCODE, "US")!!
            localeModel.name = sharedPreferences.getString(SHARED_PREFERENCES_LANG_LANGNAME, "English")!!

            return localeModel
        }

        private fun setLanguage(context: Context, language: LocaleModel) {
            val locale = Locale(language.id, language.code)
            Locale.setDefault(locale)

            val res = context.resources
            val config = Configuration(res.configuration)

            config.setLocale(locale)
            context.createConfigurationContext(config)

            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }
}