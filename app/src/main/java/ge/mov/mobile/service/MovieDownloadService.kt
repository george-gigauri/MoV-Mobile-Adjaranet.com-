package ge.mov.mobile.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import ge.mov.mobile.R
import ge.mov.mobile.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileFilter

class MovieDownloadService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val _id = intent?.getIntExtra("id", 1) ?: -1
        val _title = intent?.getStringExtra("title") ?: "UNTITLED"
        val season = intent?.getIntExtra("season", 0) ?: 0
        val episode = intent?.getIntExtra("episode", 0) ?: 0
        val lang = intent?.getStringExtra("lang") ?: "GEO"
        val url = intent?.getStringExtra("url")

        if (url != null) {

            val destination = "/MoVDownloads/${_id}_${season}_${episode}_$lang.mp4"

            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(url))
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, destination)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE or DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setTitle("Downloading $_title...")

            dm.enqueue(request)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(onDownloadCompleted())
    }

    private fun copyFile() {

        GlobalScope.launch(Dispatchers.IO) {
            try {

                val list = File(
                    Constants.OFFLINE_DOWNLOAD_PATH,
                    "MoVDownloads"
                ).listFiles(FileFilter { it.extension == "mp4" })

                if (list == null) Log.i("ListFile", "List is NULL")

                list?.forEach {
                    Log.i("ListFile", it.name)
                    val destination = File(
                        filesDir, "Offline/Movies/${it.name}"
                    ).absoluteFile

                    it.copyTo(destination, true)
                    it.delete()
                }

                stopForeground(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("001", "001", NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.description = "sample description"

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

            val builder = Notification.Builder(this, "001")
            builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Running")
                .setContentText("Downloading Movie...")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setGroupSummary(true)

            startForeground(10, builder.build())
        }

        registerReceiver(
            onDownloadCompleted(),
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    private fun onDownloadCompleted() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            copyFile()
        }
    }
}