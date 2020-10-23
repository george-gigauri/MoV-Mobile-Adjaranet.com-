package ge.mov.mobile.ui.activity

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import ge.mov.mobile.R
import ge.mov.mobile.util.NetworkUtils
import ge.mov.mobile.util.Utils
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*

class SplashActivity : AppCompatActivity() {
    private val mAuth = FirebaseAuth.getInstance()
    private val timer = Timer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        splash_title.startAnimation(
            AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.fade_in
            )
        )

        val myLang = Utils.loadLanguage(this)
        if (myLang != null) {
            Utils.saveLanguage(this, myLang)
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
}