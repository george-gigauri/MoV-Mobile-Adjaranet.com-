package ge.mov.mobile.analytics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseCustomAnalytics(private val context: Context) {

    private val analytics = FirebaseAnalytics.getInstance(context)

    fun setMoviesCountUserProperty(count: Int) {
        analytics.setUserProperty("saved_item_count", count.toString())
    }

    fun setPopupStyle(style: String) {
        analytics.setUserProperty("popup_style", style)
    }
}