package ge.mov.mobile.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import ge.mov.mobile.R

class SplashActivity : AppCompatActivity() {
    private val mAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val intent =
                if (mAuth.currentUser != null)
                {
                    Intent(this, MainActivity::class.java)
                } else {
                    Intent(this, MainActivity::class.java)
                }
            this.startActivity(intent)
            this.finish()
        }, 1500)
    }
}