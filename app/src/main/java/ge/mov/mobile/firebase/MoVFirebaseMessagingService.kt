package ge.mov.mobile.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
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

            NotificationUtils.push(this, title!!, body!!, null)
        }
    }
}