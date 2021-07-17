package ge.mov.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.mov.mobile.data.repository.MainRepository
import ge.mov.mobile.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    private val _data: MutableStateFlow<State> = MutableStateFlow(State.empty())
    val data: StateFlow<State> = _data.asStateFlow()

    init {
        getData()
    }

    fun getData() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            mainRepository.retrieveAll().collect {
                _data.value = it
            }
        }
    }
}