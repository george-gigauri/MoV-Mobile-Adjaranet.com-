package ge.mov.mobile.ui.activity

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import ge.mov.mobile.BuildConfig
import ge.mov.mobile.MovApplication
import ge.mov.mobile.R
import ge.mov.mobile.analytics.FirebaseCustomAnalytics
import ge.mov.mobile.analytics.FirebaseLogger
import ge.mov.mobile.data.database.DBService
import ge.mov.mobile.di.module.AppModule
import ge.mov.mobile.ui.MyProgressDialog
import ge.mov.mobile.util.*
import ge.mov.mobile.util.Constants.AVAILABLE_LANGUAGES
import ge.mov.mobile.util.Utils.loadLanguage
import ge.mov.mobile.util.Utils.saveLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    lateinit var analytics: FirebaseCustomAnalytics

    lateinit var logger: FirebaseLogger

    private val fUser = Firebase.auth.currentUser

    open var isFullScreen: Boolean = false

    val binding: VB
        get() = _binding!!

    abstract val bindingFactory: (LayoutInflater) -> VB

    @Suppress("UNCHECKED_CAST")
    private var _binding: VB? = null

    private val dialogFragment by lazy { MyProgressDialog() }

    override fun onCreate(savedInstanceState: Bundle?) {
        loadLanguage()

        if (isFullScreen) FullScreen()
        else setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        analytics = FirebaseCustomAnalytics(this)
        logger = FirebaseLogger(this)
        this.sendPushToken()

        _binding = bindingFactory.invoke(layoutInflater)
        MovApplication.language = loadLanguage(this)
        setContentView(binding.root)

        setup(savedInstanceState)

        if (Utils.isBirthdayInfoProvided(this))
            if (Utils.isUserAdult(context = this))
                Constants.showAdultContent = true

        setAnalytics()
        /*     val periodicWorkRequest = PeriodicWorkRequestBuilder<ReminderWork>(1, TimeUnit.MINUTES)
                 .build()
             val reminderWorkManager = WorkManager.getInstance(this)
             reminderWorkManager.enqueue(periodicWorkRequest) */
    }

    abstract fun setup(savedInstanceState: Bundle?)

    private fun sendPushToken() {
        val isPushTokenSent = getSharedPreferences("FirebasePreferences", MODE_PRIVATE)
            .getBoolean("pushTokenSent", false)

        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
                try {
                    if (fUser != null && !isPushTokenSent) {
                        val token = FirebaseMessaging.getInstance().token.await()
                        Firebase.firestore.collection("users").document(fUser.uid)
                            .update(mapOf("fcmToken" to token))

                        val editor = getSharedPreferences("FirebasePreferences", MODE_PRIVATE)
                            .edit()
                        editor.putBoolean("pushTokenSent", true)
                        editor.apply()
                    }
                } catch (exception: Exception) {
                    Log.d("BaseActivity", "Token error: ${exception.message}")
                    exception.printStackTrace()
                }
            }
        }
    }

    private fun setAnalytics() {
        lifecycleScope.launchWhenCreated {
            val db = DBService.getInstance(this@BaseActivity)
                .movieDao()
            val count = db.getSavedMoviesCount()
            analytics.setMoviesCountUserProperty(count)
        }

        val popupStyle =
            getSharedPreferences("AppPreferences", MODE_PRIVATE).getInt("popup_style", 1)
        analytics.setPopupStyle(if (popupStyle == 1) "bottom" else "window")
    }

    fun forwardUser() {
        if (!NetworkUtils.isNetworkConnected(this)) {
            val intent = Intent(applicationContext, NoConnectionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else if (Utils.isFirstUse(this)) {
            val intent = Intent(applicationContext, ApplicationSetupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else if (!Utils.isBirthdayInfoProvided(this)) { // Check if user has provided an age
            val intent = Intent(applicationContext, SetupBirthdayActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun checkUpdate() {
        lifecycleScope.launchWhenStarted {
            val currentBuildCode = BuildConfig.VERSION_CODE
            withContext(Dispatchers.IO) {
                val retrofit = withContext(Dispatchers.IO) { AppModule.getRemoteRetrofit() }
                val config = withContext(Dispatchers.IO) { AppModule.getRemoteApi(retrofit) }
                val response = config.getRemoteSettings().body() ?: return@withContext

                if (currentBuildCode < response.remoteVersionCode)
                    notifyUpdate()
            }
        }.start()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        applyOverrideConfiguration(Configuration())
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
//        applyOverrideConfiguration(newConfig)
    }

//    override fun applyOverrideConfiguration(newConfig: Configuration) {
//        val uiMode = newConfig.uiMode
//        newConfig.setTo(baseContext.resources.configuration)
//        newConfig.uiMode = uiMode
//        super.applyOverrideConfiguration(updateConfigurationIfSupported(newConfig))
//    }


    open fun showProgress() {
        if (!dialogFragment.isAdded)
            dialogFragment.show(supportFragmentManager, "ProgressDialog")
    }

    open fun hideProgress() {
        if (dialogFragment.isAdded)
            dialogFragment.dismiss()
    }

    open fun updateConfigurationIfSupported(config: Configuration): Configuration? {
        if (Build.VERSION.SDK_INT >= 24) {
            if (!config.locales.isEmpty) {
                return config
            }
        } else {
            if (config.locale != null) {
                return config
            }
        }
        val savedLanguage = loadLanguage(this)
        val locale = Locale(savedLanguage.id)
        config.setLocale(locale)

        MovApplication.language = savedLanguage
        saveLanguage(this, AVAILABLE_LANGUAGES.filter { it.id == savedLanguage.id }[0])
        return config
    }

    override fun onResume() {
        super.onResume()

        if (checkConnection())
            checkUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private suspend fun notifyUpdate() {
        withContext(Dispatchers.Main) {
            AlertDialog.Builder(this@BaseActivity)
                .setTitle(getString(R.string.update_available))
                .setMessage(getString(R.string.update_available_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.update_now)) { _, _ ->
                    if (!Const.isTV) {
                        val url = "https://play.google.com/store/apps/details?id=ge.mov.mobile"
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    } else {
                        try {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=$packageName")
                                )
                            )
                        } catch (anfe: ActivityNotFoundException) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                                )
                            )
                        }
                    }
                }.create().show()
        }
    }

    private fun checkConnection(): Boolean {
        var status = false

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm.activeNetwork != null && cm.getNetworkCapabilities(cm.activeNetwork) != null) {
                // connected to the internet
                status = true
            }
        } else {
            if (cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnectedOrConnecting) {
                // connected to the internet
                status = true
            }
        }

        return status
    }

    private fun loadLanguage() {
        val code = loadLanguage(this).id
        val locale = Locale(code)
        Locale.setDefault(locale)
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun FullScreen() {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    fun askPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    100019
                )

                // REQUEST_CODE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
}