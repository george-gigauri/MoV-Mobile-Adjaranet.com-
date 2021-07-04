package ge.mov.mobile.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.res.Configuration
import ge.mov.mobile.data.model.LocaleModel
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.util.Constants.SHARED_PREFERENCES_LANG_LANGCODE
import ge.mov.mobile.util.Constants.SHARED_PREFERENCES_LANG_LANGID
import ge.mov.mobile.util.Constants.SHARED_PREFERENCES_LANG_LANGNAME
import ge.mov.mobile.util.Constants.SHARED_PREFERENCES_USER
import java.util.*

object Utils {
    fun saveLanguage(context: Context, language: LocaleModel) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_USER, MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString(SHARED_PREFERENCES_LANG_LANGID, language.id)
        editor.putString(SHARED_PREFERENCES_LANG_LANGCODE, language.code)
        editor.putString(SHARED_PREFERENCES_LANG_LANGNAME, language.name)
        editor.apply()

        setLanguage(context, language)
        saveSetup(context)
    }

    fun loadLanguage(context: Context): LocaleModel {
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

    fun saveSetup(context: Context) {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("first_use", false)
        editor.apply()
    }

    fun isFirstUse(context: Context) : Boolean {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", MODE_PRIVATE)
        return sharedPreferences.getBoolean("first_use", true)
    }

    fun isBirthdayInfoProvided(context: Context) : Boolean {
        val preferences = context.getSharedPreferences("UserBirthdayPreference", MODE_PRIVATE)
        return  if (preferences != null) {
            preferences.getInt("day", 0) != 0 ||
                    preferences.getInt("month", 0) != 0 ||
                    preferences.getInt("year", 0) != 0
        } else {
            false
        }
    }

    fun getBirthdayInfoFull(context: Context) : String {
        val preferences = context.getSharedPreferences("UserBirthdayPreference", MODE_PRIVATE)
        val day = preferences.getInt("day", 0)
        val month = preferences.getInt("month", 0)
        val year = preferences.getInt("year", 0)
        return "$day.$month.$year"
    }

    fun getBirthDay(context: Context) =
        context.getSharedPreferences("UserBirthdayPreference", MODE_PRIVATE).getInt("day", 0)

    fun getBirthMonth(context: Context) =
        context.getSharedPreferences("UserBirthdayPreference", MODE_PRIVATE).getInt("month", 0)

    fun getBirthYear(context: Context) =
        context.getSharedPreferences("UserBirthdayPreference", MODE_PRIVATE).getInt("year", 0)

    fun isUserAdult(context: Context): Boolean {
        return Calendar.getInstance()[Calendar.YEAR] - getBirthYear(context) >= 18
    }

    fun getNameByLanguage(data: Data, lang: String?) = if (lang != null) {
        if (lang == "GEO") {
            if (data.primaryName != "") {
                data.primaryName
            } else {
                data.secondaryName
            }
        } else {
            data.secondaryName
        }
    } else data.originalName
}