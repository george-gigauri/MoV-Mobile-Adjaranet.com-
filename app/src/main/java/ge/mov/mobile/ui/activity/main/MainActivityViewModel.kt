package ge.mov.mobile.ui.activity.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ge.mov.mobile.data.database.entity.MovieEntity
import ge.mov.mobile.data.model.basic.BasicMovie
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.data.model.featured.FeaturedModel
import ge.mov.mobile.data.model.movie.Genres
import ge.mov.mobile.data.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainActivityViewModel @ViewModelInject
constructor(private val mainRepository: MainRepository) : ViewModel() {
    var isLoading = MutableLiveData<Int>()

    private val _savedMovies = MutableLiveData<List<MovieEntity>>()
    val savedMovies: LiveData<List<MovieEntity>> = _savedMovies

    init {
        viewModelScope.launch {
            mainRepository.getSavedMovies(7).collect {
                _savedMovies.postValue(it)
            }
        }
    }

    fun updateSavedMovies() = viewModelScope.launch {
        mainRepository.getSavedMovies(7).collect {
            _savedMovies.postValue(it)
        }
    }

    suspend fun getMovies(): List<Data>? =
        withContext(Dispatchers.IO) { mainRepository.getMovies()?.data }

    suspend fun getSeries(): List<Data>? = withContext(Dispatchers.IO) {
        mainRepository.getMovies(type = "series")?.data
    }

    suspend fun getSlides(): List<FeaturedModel>? =
        withContext(Dispatchers.IO) { mainRepository.getSlides()?.data }

    suspend fun getGenresFull(): Genres? {
        return mainRepository.getGenres()
    }

    fun getTopMovies(page: Int = 1, period: String = "day"): BasicMovie? {
        return runBlocking { mainRepository.getPopularMovies() }
    }
}