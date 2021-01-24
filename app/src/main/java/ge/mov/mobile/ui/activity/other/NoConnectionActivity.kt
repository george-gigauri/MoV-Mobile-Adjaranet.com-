package ge.mov.mobile.ui.activity.other

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ge.mov.mobile.R
import ge.mov.mobile.ui.activity.main.MainActivity
import ge.mov.mobile.util.NetworkUtils
import kotlinx.android.synthetic.main.activity_no_connection.*
import kotlin.system.exitProcess

class NoConnectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_connection)

        retry_connect.setOnClickListener {
            retry_connect.isEnabled = false
            if (NetworkUtils.isNetworkConnected(applicationContext)) {
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            retry_connect.isEnabled = true
        }

        exit.setOnClickListener {
            finish()
            exitProcess(0)
        }
    }
}