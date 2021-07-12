package ge.mov.mobile.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.mov.mobile.data.network.APIService
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class PersonMovieViewModel @Inject constructor(private val apiService: APIService) : ViewModel() {
    fun getMoviesByActor(id: Long) = runBlocking {
        apiService.getMoviesByPerson(id = id).body()?.data
    }
}