package ge.mov.mobile.ui.activity

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.R
import ge.mov.mobile.util.Constants
import ge.mov.mobile.util.LanguageUtil
import ge.mov.mobile.util.NetworkUtils
import ge.mov.mobile.util.Utils
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val mAuth = FirebaseAuth.getInstance()
    private val timer = Timer()
    private lateinit var languageUtil: LanguageUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        languageUtil = LanguageUtil(this)

        splash_title.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.fade_in
            )
        )

        val myLang = LanguageUtil.language

        if (Utils.isBirthdayInfoProvided(this))
            if (Utils.isUserAdult(context = this))
                Constants.showAdultContent = true

        val externalData = getWebData()
        if (externalData != null) {
            val intent = Intent(applicationContext, MovieActivity::class.java)
            intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("id", externalData)
            intent.putExtra("adjaraId", externalData)
            startActivity(intent)
            finish()
            return
        }

        timer.schedule(object : TimerTask() {
            override fun run() {
                val intent =
                    if (!NetworkUtils.isNetworkConnected(applicationContext)) { // Check if internet is available
                        Intent(
                            applicationContext,
                            NoConnectionActivity::class.java
                        ) // if internet IS NOT available, then redirecting to NoInternetActivity
                    } else { // Else checking if user is or is not logged in
                        if (mAuth.currentUser != null) { // if user is logged in it will be diverted to the MainActivity
                            Intent(applicationContext, MainActivity::class.java)
                        } else { // else if user first opened an app, it will redirect them to the Setup Activity
                            if (Utils.isFirstUse(this@SplashActivity)) {
                                Intent(applicationContext, ApplicationSetupActivity::class.java)
                            } else if (!Utils.isBirthdayInfoProvided(this@SplashActivity)) { // Check if user has provided an age
                                Intent(applicationContext, SetupBirthdayActivity::class.java)
                            } else {
                                Intent(applicationContext, MainActivity::class.java)
                            }
                        }
                    }

                intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }, 1000)
    }

    private fun getWebData() : Long? {
        val uri = intent.data
        val path = uri?.path
        val id: Long
        if (path != null) {
            if (path.contains("movies")) {
                return try {
                    val tempUrl = path
                    val s = tempUrl.split('/') as ArrayList<String>
                    s.removeAt(0)
                    id = s[1].toLong()
                    id
                } catch (e: Exception) {
                    Log.i("SplashActivity", e.message.toString())
                    null
                }
            }
        }
        return null
    }
}