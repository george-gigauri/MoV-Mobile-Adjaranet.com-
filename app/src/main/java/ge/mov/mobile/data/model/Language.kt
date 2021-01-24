package ge.mov.mobile.data.model

import java.io.Serializable

data class LanguageData(
    val data: List<Language>?
): Serializable

data class Language(
    val code: String,
    val primaryName: String,
    val primaryNameTurned: String,
    val secondaryName: String
): Serializable {
    var selected = false
}