package ge.mov.mobile.ui.activity.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.ycuwq.datepicker.date.DatePickerDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.databinding.ActivityRegisterBinding
import ge.mov.mobile.extension.toBdayDate
import ge.mov.mobile.extension.toast
import ge.mov.mobile.ui.activity.base.BaseActivity
import ge.mov.mobile.util.State

@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {

    override val bindingFactory: (LayoutInflater) -> ActivityRegisterBinding
        get() = { ActivityRegisterBinding.inflate(it) }

    private val viewModel: AuthViewModel by viewModels()

    override fun setup(savedInstanceState: Bundle?) {
        setOnClickListeners()

        viewModel.state.observe(this) {
            when (it.status) {
                State.Status.LOADING -> {
                    binding.progress.isVisible = true
                }

                State.Status.FAILURE -> {
                    binding.progress.isVisible = false
                    toast(it.message ?: "")
                }

                State.Status.SUCCESS -> {
                    binding.progress.isVisible = false
                    val data = (it.data as String?)
                    if (data == "SIGN_UP_SUCCESS") {
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                }

                else -> binding.progress.isVisible = false
            }
        }
    }

    private fun setOnClickListeners() {
        binding.apply {
            bday.setOnClickListener { openDatePicker() }
            btnRegister.setOnClickListener { signUp() }
        }
    }

    private fun openDatePicker() {
        val datePickerDialogFragment = DatePickerDialogFragment()
        datePickerDialogFragment.setOnDateChooseListener { year, month, day ->
            val format = "$day-$month-$year"
            binding.bday.text = format
        }
        datePickerDialogFragment.show(fragmentManager, null)
    }

    private fun signUp() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val rePassword = binding.etRepeatPassword.text.toString()
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val bday = binding.bday.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty() && rePassword.isNotEmpty() &&
            firstName.isNotEmpty() && lastName.isNotEmpty()
        ) {
            if (password == rePassword) {
                viewModel.doRegister(email, password, firstName, lastName, bday.toBdayDate())
            } else toast("პაროლები არ ემთხვევა ერთმანეთს.")
        } else toast("შეავსე ყველა ველი.")
    }
}