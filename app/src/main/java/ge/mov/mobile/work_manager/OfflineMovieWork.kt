package ge.mov.mobile.work_manager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import ge.mov.mobile.extension.toast
import ge.mov.mobile.util.Constants
import java.io.File

class OfflineMovieWork(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {

        val data = inputData

        copyFile(data.getString("destination_uri"))

        return Result.success()
    }

    private fun copyFile(destinationUri: String?) {

        try {

            val list = File(Constants.OFFLINE_DOWNLOAD_PATH, "MoVDownloads").listFiles()

            if (list == null) Log.i("ListFile", "List is NULL")

            list?.forEach {
                Log.i("ListFile", it.name)
                val destination = File(
                    File(destinationUri!!), "Offline/Movies/${it.name}.${it.extension}"
                ).absoluteFile

                it.copyTo(destination, true)
            }

            list?.forEach { it.deleteRecursively() }

        } catch (e: Exception) {
            e.printStackTrace()

            applicationContext.toast("Not enough memory.")
        }
    }
}
