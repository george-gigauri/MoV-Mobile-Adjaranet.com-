package ge.mov.mobile.ui.activity.main

import android.view.View.VISIBLE
import android.view.View.GONE
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ge.mov.mobile.data.model.basic.BasicMovie
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.data.model.featured.FeaturedModel
import ge.mov.mobile.data.model.movie.Genres
import ge.mov.mobile.data.network.APIService
import ge.mov.mobile.data.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainActivityViewModel @ViewModelInject
constructor(private val mainRepository: MainRepository) : ViewModel() {
    var isLoading = MutableLiveData<Int>()

    suspend fun getMovies(): List<Data>? = withContext(Dispatchers.IO) { mainRepository.getMovies()?.data }

    suspend fun getSeries(): List<Data>? = withContext(Dispatchers.IO) {
        mainRepository.getMovies(type = "series")?.data
    }

    fun getSlides(): LiveData<List<FeaturedModel>> {
        isLoading.postValue(VISIBLE)
        val slides = MutableLiveData<List<FeaturedModel>>()

        slides.value = runBlocking { mainRepository.getSlides()?.data }

        isLoading.postValue(GONE)
        return slides
    }

    fun getGenresFull(): Genres? {
        return runBlocking { mainRepository.getGenres() }
    }

    fun getTopMovies(page: Int = 1, period: String = "day") : BasicMovie?  {
        return runBlocking { mainRepository.getPopularMovies() }
    }
}