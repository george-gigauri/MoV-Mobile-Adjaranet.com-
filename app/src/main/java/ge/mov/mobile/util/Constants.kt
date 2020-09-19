package ge.mov.mobile.util

object Constants {
    const val BASE_URL = "https://api.adjaranet.com/api/v1/"
    const val SHARED_PREFERENCES_USER = "UserPreferences"
    const val SHARED_PREFERENCES_LANGUAGE = "user-language"

    val AVAILABLE_LANGUAGES = arrayOf(
        "English",
        "ქართული"
    )

    var current_movie_left_at = 0
}