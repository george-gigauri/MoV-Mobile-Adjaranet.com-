package ge.mov.mobile.util

import ge.mov.mobile.model.LocaleModel

object Constants {
    const val BASE_URL = "https://api.adjaranet.com/api/v1/"
    const val SHARED_PREFERENCES_USER = "UserPreferences"
    const val SHARED_PREFERENCES_LANGUAGE = "user-language"

    const val SHARED_PREFERENCES_LANG_LANGID = "ID"
    const val SHARED_PREFERENCES_LANG_LANGNAME = "NAME"
    const val SHARED_PREFERENCES_LANG_LANGCODE = "CODE"

    var PAGE = 1

    val AVAILABLE_LANGUAGES = arrayListOf(
        LocaleModel("English", "en", "US"),
        LocaleModel("ქართული", "ka", "GE")
    )

    var current_movie_left_at = 0
}