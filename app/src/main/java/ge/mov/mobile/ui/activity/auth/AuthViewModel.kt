package ge.mov.mobile.ui.activity.auth

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ge.mov.mobile.data.repository.AuthRepository
import ge.mov.mobile.util.Role
import ge.mov.mobile.util.State
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val state: LiveData<State> = authRepository.state

    fun doLogin(email: String, password: String) = viewModelScope.launch {
        authRepository.login(email, password)
    }

    fun doRegister(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        birthDate: Long,
        role: Role = Role.USER
    ) = viewModelScope.launch {
        authRepository.register(email, password, firstName, lastName, birthDate, role)
    }

    fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
        authRepository.sendResetPassword(email)
    }
}