package ge.mov.mobile.ui.activity.offline

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import ge.mov.mobile.data.database.entity.OfflineMovieEntity
import ge.mov.mobile.data.repository.OfflineRepository
import kotlinx.coroutines.launch

class OfflineViewModel @ViewModelInject constructor(
    private val offlineRepository: OfflineRepository
) : ViewModel() {

    val allMovies: LiveData<List<OfflineMovieEntity>> = offlineRepository.getAll().asLiveData()

    fun insert(m: OfflineMovieEntity) = viewModelScope.launch {
        offlineRepository.insert(m)
    }

    fun delete(m: OfflineMovieEntity) = viewModelScope.launch {
        offlineRepository.delete(m)
    }

    fun delete(ms: List<OfflineMovieEntity>) = viewModelScope.launch {
        offlineRepository.delete(ms)
    }

    fun deleteByIds(ids: List<Int>) = viewModelScope.launch {
        offlineRepository.deleteByIds(ids)
    }
}