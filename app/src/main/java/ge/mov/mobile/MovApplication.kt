package ge.mov.mobile

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import ge.mov.mobile.data.database.DBService
import ge.mov.mobile.ui.activity.main.MainActivity
import ge.mov.mobile.ui.activity.other.NoConnectionActivity
import ge.mov.mobile.ui.activity.setup.ApplicationSetupActivity
import ge.mov.mobile.ui.activity.setup.SetupBirthdayActivity
import ge.mov.mobile.util.LanguageUtil
import ge.mov.mobile.util.NetworkUtils
import ge.mov.mobile.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.ArrayList

@HiltAndroidApp
class MovApplication() : Application() {
    private lateinit var context: Context
    constructor(context: Context) : this() {
        this.context = context

        if (this::context.isInitialized)
            loadLanguage()
    }

    override fun onCreate() {
        super.onCreate()

        GlobalScope.launch(Dispatchers.IO) {
            DBService.getInstance(this@MovApplication)
                .subscriptionDao()
                .clearPositionStates()
        }.start()
    }

    private fun loadLanguage() {
        val lang = LanguageUtil(context)
        Utils.saveLanguage(context, LanguageUtil.language!!)
    }
}