package ge.mov.mobile.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseLogger(context: Context) {

    private val logger = FirebaseAnalytics.getInstance(context)

    fun logSubtitlesClicked() {
        logger.logEvent("turn_on_subtitles", null)
    }

    fun logSpeedClicked() {
        logger.logEvent("video_speed_clicked", null)
    }

    fun logNextEpisodeClicked() {
        logger.logEvent("next_episode_clicked", null)
    }

    fun logPreviousEpisodeClicked() {
        logger.logEvent("prev_episode_clicked", null)
    }

    fun logDeveloperProfileClicked() {
        logger.logEvent("developer_profile_clicked", null)
    }

    fun logDeveloperSocialInfoClicked(type: String?) {
        val bundle = Bundle()
        bundle.putString("type", type)

        logger.logEvent("social_info_clicked", bundle)
    }

    fun logSearchOpened() {
        logger.logEvent("open_search_event", null)
    }

    fun logThemeChanged(theme: String) {
        val bundle = Bundle()
        bundle.putString("color", theme)
        logger.logEvent("theme_changed", bundle)
    }

    fun logDownloadMovie(movieId: Int) {
        val bundle = Bundle()
        bundle.putInt("id", movieId)
        logger.logEvent("download_movie", bundle)
    }
}