package ge.mov.mobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.mov.mobile.data.repository.AuthRepository
import ge.mov.mobile.util.Role
import ge.mov.mobile.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state: MutableLiveData<State> = MutableLiveData(State.empty())
    val state: LiveData<State> = _state

    fun doLogin(email: String, password: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            authRepository.login(email, password).collect {
                _state.postValue(it)
            }
        }
    }

    fun doRegister(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        birthDate: Long,
        role: Role = Role.USER
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            authRepository.register(email, password, firstName, lastName, birthDate, role)
                .collect {
                    _state.postValue(it)
                }
        }
    }

    fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            authRepository.sendResetPassword(email).collect {
                _state.postValue(it)
            }
        }
    }
}