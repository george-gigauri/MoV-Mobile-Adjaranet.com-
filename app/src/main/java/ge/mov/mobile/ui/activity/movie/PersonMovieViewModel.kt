package ge.mov.mobile.ui.activity.movie

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ge.mov.mobile.data.model.basic.BasicMovie
import ge.mov.mobile.data.network.APIService
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class PersonMovieViewModel @ViewModelInject constructor(private val apiService: APIService) : ViewModel() {
    fun getMoviesByActor(id: Long) = runBlocking {
        apiService.getMoviesByPerson(id = id).body()?.data
    }
}