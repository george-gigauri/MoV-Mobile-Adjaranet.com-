package ge.mov.mobile.util

import android.content.Context
import ge.mov.mobile.data.model.LocaleModel

class LanguageUtil(private val context: Context) {
    companion object {
        var language: LocaleModel? = null
    }

    init {
        language = Utils.loadLanguage(context)
    }
}