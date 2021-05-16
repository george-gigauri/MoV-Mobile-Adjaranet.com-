package ge.mov.mobile.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import ge.mov.mobile.service.util.ServiceUtil
import ge.mov.mobile.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class DeleteMovieService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        ServiceUtil.displayNotification(this, "MoV", "Cleaning Up...")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val names =
            intent?.getStringArrayListExtra("names") as ArrayList<String>

        Log.i("FileName", names.toString())

        deleteFiles(names)

        return START_STICKY
    }

    private fun deleteFiles(names: ArrayList<String>) {
        if (!names.isNullOrEmpty()) {

            GlobalScope.launch(Dispatchers.IO) {
                names.forEach { m ->
                    val file = File(
                        filesDir,
                        Constants.OFFLINE_LOCAL_PATH_SUFFIX
                    ).listFiles()

                    file?.forEach {
                        Log.i("FileNameIt", it.name)
                        Log.i("FileNameM", m)
                        if (it.name == m)
                            it.deleteRecursively()
                    }
                }
                stopForeground(true)
            }
        } else stopForeground(true)
    }
}