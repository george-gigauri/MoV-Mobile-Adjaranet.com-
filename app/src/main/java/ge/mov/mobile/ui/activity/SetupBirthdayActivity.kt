package ge.mov.mobile.ui.activity

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.LayoutInflater
import ge.mov.mobile.databinding.ActivitySetupBirthdayBinding
import ge.mov.mobile.ui.fragment.DatePickerBottomSheet
import ge.mov.mobile.util.Constants
import ge.mov.mobile.util.Utils

class SetupBirthdayActivity : BaseActivity<ActivitySetupBirthdayBinding>(),
    DatePickerBottomSheet.OnDateSelectedListener {

    override val bindingFactory: (LayoutInflater) -> ActivitySetupBirthdayBinding
        get() = { ActivitySetupBirthdayBinding.inflate(it) }

    override fun setup(savedInstanceState: Bundle?) {

        openDatePickerDialog()

        binding.tvBirthday.setOnClickListener {
            openDatePickerDialog()
        }

        binding.btnContinue.setOnClickListener {
            open()
            Constants.showAdultContent = Utils.isUserAdult(this)
        }
    }

    private fun open() {
        val intent = if (Utils.isFirstUse(this)) {
            Intent(applicationContext, IntroActivity::class.java)
        } else {
            Intent(applicationContext, MainActivity::class.java)
        }
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun openDatePickerDialog() {
        val initialDateString = binding.tvBirthday.text.toString()
        val datePicker = if (initialDateString.isNotEmpty()) {
            val initialDate = initialDateString.split("/")
            val day = initialDate[0].trim().toInt().or(0)
            val month = initialDate[1].trim().toInt().or(0)
            val year = initialDate[2].trim().toInt().or(0)

            DatePickerBottomSheet(day, month, year)
        } else DatePickerBottomSheet()

        datePicker.listener = this
        datePicker.show(supportFragmentManager, null)
    }

    private fun saveBirthdayInfo(day: Int, month: Int, year: Int) {
        val preferences = getSharedPreferences("UserBirthdayPreference", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt("day", day)
        editor.putInt("month", month)
        editor.putInt("year", year)
        editor.apply()
    }

    override fun onDateSelected(day: Int, month: Int, year: Int) {
        saveBirthdayInfo(day, month, year)
        binding.tvBirthday.text = "$day / $month / $year"
    }
}