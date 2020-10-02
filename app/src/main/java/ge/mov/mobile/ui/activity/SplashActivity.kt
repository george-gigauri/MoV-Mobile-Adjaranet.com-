package ge.mov.mobile.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import ge.mov.mobile.R
import ge.mov.mobile.util.RemoteConfigUtils
import ge.mov.mobile.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private val mAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

            Handler().postDelayed({
                if (RemoteConfigUtils.getRemoteConfig().getString("isAppEnabled") == "yes") {
                    val intent =
                        if (mAuth.currentUser != null) {
                            Intent(this, MainActivity::class.java)
                        } else {
                            if (Utils.isFirstUse(this)) {
                                Intent(this, ApplicationSetupActivity::class.java)
                            } else {
                                Intent(this, MainActivity::class.java)
                            }
                        }
                    this.startActivity(intent)
                    this.finish()
                } else {
                        AlertDialog.Builder(this@SplashActivity)
                            .setTitle("შეცდომა")
                            .setCancelable(false)
                            .setMessage("აპლიკაცია დროებით გამორთულია.")
                            .setPositiveButton("გასვლა") { _, _ ->
                                finish()
                            }.create()
                            .show()
                }
            }, 1500)

    }
}