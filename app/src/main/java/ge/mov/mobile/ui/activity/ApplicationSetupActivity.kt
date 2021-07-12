package ge.mov.mobile.ui.activity

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ge.mov.mobile.R
import ge.mov.mobile.data.model.LocaleModel
import ge.mov.mobile.util.Constants
import ge.mov.mobile.util.Utils
import kotlinx.android.synthetic.main.activity_application_setup.*

class ApplicationSetupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_setup)

        select_english.setOnClickListener {
            val localeModel = LocaleModel()
            Utils.saveLanguage(this, localeModel)
            startActivity()
        }

        select_georgian.setOnClickListener {
            val localeModel: LocaleModel = Constants.AVAILABLE_LANGUAGES.find { i -> i.id == "ka" }!!
            Utils.saveLanguage(this, localeModel)
            startActivity()
        }
    }

    private fun startActivity() {
        val intent: Intent = if (!Utils.isBirthdayInfoProvided(this)) { // Check if user has provided an age
            Intent(applicationContext, SetupBirthdayActivity::class.java)
        } else {
            Intent(applicationContext, MainActivity::class.java)
        }

        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}