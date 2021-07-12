package ge.mov.mobile

import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import ge.mov.mobile.data.database.DBService
import ge.mov.mobile.data.model.LocaleModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@HiltAndroidApp
class MovApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        GlobalScope.launch(Dispatchers.IO) {
            DBService.getInstance(this@MovApplication)
                .subscriptionDao()
                .clearPositionStates()
        }.start()
    }

    companion object {
        var language: LocaleModel? = null
    }
}