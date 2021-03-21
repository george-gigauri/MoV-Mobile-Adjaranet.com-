package ge.mov.mobile.service.firebase

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ge.mov.mobile.ui.activity.movie.MovieActivity
import ge.mov.mobile.util.NotificationUtils
import ge.mov.mobile.util.toast

class MoVFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        if (p0.notification != null) {
            val notification = p0.notification
            val title = notification?.title
            val body = notification?.body

            val idRaw: String? = p0.data["id"]
            val adjaraIdRaw: String? = p0.data["adjaraId"]

            var pendingIntent: PendingIntent? = null
            var id: Int? = null
            var adjaraId: Int? = null

            if (idRaw != null)
                id = idRaw.toInt()
            if (adjaraIdRaw != null)
                adjaraId = adjaraIdRaw.toInt()

            if (id != null || adjaraId != null) {
                val intent = Intent(applicationContext, MovieActivity::class.java)
                intent.putExtra("id", id)
                intent.putExtra("adjaraId", adjaraId)
                pendingIntent =
                    PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }

            NotificationUtils.push(this, title!!, body!!, pendingIntent)
        }
    }
}