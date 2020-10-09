package ge.mov.mobile.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import ge.mov.mobile.R
import ge.mov.mobile.util.Utils

class SplashActivity : AppCompatActivity() {
    private val mAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

            Handler().postDelayed({
          //      if (runBlocking { RemoteConfigUtils.isEnabled() }) {
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
     /*           } else {
                    val alertDialog = AlertDialog.Builder(this@SplashActivity)
                            .setTitle(getString(R.string.warning))
                            .setCancelable(false)
                            .setMessage(getString(R.string.app_temporarily_disabled))
                            .setPositiveButton("OK") { _, _ ->
                                finish()
                            }.create()
                    alertDialog.show()
                } */
            }, 1500)
    }
}