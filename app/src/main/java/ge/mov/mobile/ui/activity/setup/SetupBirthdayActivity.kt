package ge.mov.mobile.ui.activity.setup

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import ge.mov.mobile.R
import ge.mov.mobile.databinding.ActivitySetupBirthdayBinding
import ge.mov.mobile.ui.activity.main.MainActivity
import ge.mov.mobile.util.Constants
import ge.mov.mobile.util.Utils

class SetupBirthdayActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetupBirthdayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setup_birthday)
        binding.lifecycleOwner = this

        binding.btnContinue.setOnClickListener {
            val day = binding.birthdayPicker.dayOfMonth
            val month = binding.birthdayPicker.month + 1
            val year = binding.birthdayPicker.year

            saveBirthdayInfo(day, month, year)

            Constants.showAdultContent = Utils.isUserAdult(this)

            val intent = if (Utils.isFirstUse(this)) {
                Intent(applicationContext, IntroActivity::class.java)
            } else {
                Intent(applicationContext, MainActivity::class.java)
            }
            intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun saveBirthdayInfo(day: Int, month: Int, year: Int) {
        val preferences = getSharedPreferences("UserBirthdayPreference", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt("day", day)
        editor.putInt("month", month)
        editor.putInt("year", year)
        editor.apply()
    }
}