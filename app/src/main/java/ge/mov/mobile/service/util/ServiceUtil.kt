package ge.mov.mobile.service.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import ge.mov.mobile.R
import kotlin.random.Random

object ServiceUtil {

    fun displayNotification(context: Service, title: String, msg: String) {
        val randId = Random.nextInt(92)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    randId.toString(),
                    randId.toString(),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            notificationChannel.description = "sample description"

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

            val builder = Notification.Builder(context, "001")
            builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Running")
                .setContentText("Downloading Movie...")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setGroupSummary(true)

            context.startForeground(10, builder.build())
        }
    }
}