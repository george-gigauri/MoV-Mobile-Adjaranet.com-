package ge.mov.mobile.ui.activity.base

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import ge.mov.mobile.BuildConfig
import ge.mov.mobile.MovApplication
import ge.mov.mobile.R
import ge.mov.mobile.di.module.AppModule
import ge.mov.mobile.ui.activity.other.NoConnectionActivity
import ge.mov.mobile.ui.activity.setup.ApplicationSetupActivity
import ge.mov.mobile.ui.activity.setup.SetupBirthdayActivity
import ge.mov.mobile.util.*
import ge.mov.mobile.util.Constants.AVAILABLE_LANGUAGES
import ge.mov.mobile.util.Utils.loadLanguage
import ge.mov.mobile.util.Utils.saveLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    val binding: VB
        get() = _binding!!

    abstract val bindingFactory: (LayoutInflater) -> VB

    @Suppress("UNCHECKED_CAST")
    private var _binding: VB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        _binding = bindingFactory.invoke(layoutInflater)
        MovApplication.language = loadLanguage(this)
        setContentView(binding.root)
        setup(savedInstanceState)

        if (Utils.isBirthdayInfoProvided(this))
            if (Utils.isUserAdult(context = this))
                Constants.showAdultContent = true
    }

    abstract fun setup(savedInstanceState: Bundle?)

    private fun forwardUser() {
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
        val config = Configuration()
        applyOverrideConfiguration(config)
    }

    override fun applyOverrideConfiguration(newConfig: Configuration) {
        super.applyOverrideConfiguration(updateConfigurationIfSupported(newConfig))
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
}