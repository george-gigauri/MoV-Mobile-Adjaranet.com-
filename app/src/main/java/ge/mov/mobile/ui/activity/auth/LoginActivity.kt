package ge.mov.mobile.ui.activity.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.databinding.ActivityLoginBinding
import ge.mov.mobile.extension.toast
import ge.mov.mobile.ui.activity.base.BaseActivity
import ge.mov.mobile.ui.activity.main.MainActivity
import ge.mov.mobile.util.State

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    override val bindingFactory: (LayoutInflater) -> ActivityLoginBinding
        get() = { ActivityLoginBinding.inflate(it) }

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
                    if ((it.data as String) == "LOGIN_SUCCESS") {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                }

                else -> binding.progress.isVisible = false
            }
        }
    }

    private fun setOnClickListeners() {
        binding.skipLoginBtn.setOnClickListener { skip() }
        binding.loginBtn.setOnClickListener { login() }
        binding.btnResetPassword.setOnClickListener { sendPasswordResetEmail() }
        binding.btnSignUp.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }
    }

    private fun skip() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun login() {
        val email = binding.loginEmail.text.toString()
        val password = binding.loginPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            viewModel.doLogin(email, password)
        } else {
            toast("შეავსე ყველა მოცემული ველი!")
        }
    }

    private fun sendPasswordResetEmail() {
        val email = binding.loginEmail.text.toString()
        if (email.isNotEmpty()) {
            viewModel.sendPasswordResetEmail(email)
        } else {
            binding.loginEmail.error = "ეს ველი სავალდებულოა!"
            binding.loginEmail.requestFocus()
        }
    }
}