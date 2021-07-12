package ge.mov.mobile.service.firebase

import android.app.PendingIntent
import android.content.Intent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ge.mov.mobile.ui.activity.MovieActivity
import ge.mov.mobile.util.Constants.Firebase.USERS_COLLECTION
import ge.mov.mobile.util.NotificationUtils

class MoVFirebaseMessagingService : FirebaseMessagingService() {

    private val fUser = Firebase.auth.currentUser

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        fUser?.let {
            Firebase.firestore.collection(USERS_COLLECTION)
                .document(it.uid)
                .set(hashMapOf("fcmToken" to p0))
        }
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
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }

            NotificationUtils.push(this, title!!, body!!, pendingIntent)
        }
    }
}