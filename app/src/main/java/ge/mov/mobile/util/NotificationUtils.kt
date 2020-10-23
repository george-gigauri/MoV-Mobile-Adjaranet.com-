package ge.mov.mobile.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import ge.mov.mobile.R


object NotificationUtils {
    fun push(
        context: Context,
        title: String,
        message: String,
        pendingIntent: PendingIntent?
    ) {
        try {
            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
            val mBuilder = NotificationCompat.Builder(context, "mov_main_channel_id")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val notificationChannel = NotificationChannel(
                    "mov_main_channel_id",
                    "mov_main_channel",
                    importance
                )

                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.parseColor("#FFC0CB")
                notificationChannel.enableVibration(true)
                notificationChannel.vibrationPattern = longArrayOf(1000)
                notificationManager?.createNotificationChannel(notificationChannel)
            } else {
               mBuilder.setContentTitle(title)
                   .setContentText(message)
                   .setSmallIcon(R.drawable.logo)
                   .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.logo))
                   .setVibrate(longArrayOf(1000))
                   .setAutoCancel(true)
                   .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                   .setLights(Color.argb(1, 255, 192, 203), 100, 100)
            }

            notificationManager!!.notify(
                1,
                mBuilder.build()
            )
        } catch (e: Exception) {
            Log.i("PushNotificationProblem", e.message!!)
        }
    }
}