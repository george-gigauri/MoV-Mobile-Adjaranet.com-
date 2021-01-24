package ge.mov.mobile.ui.activity.movie

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ge.mov.mobile.data.database.DBService
import ge.mov.mobile.data.database.MovieEntity
import ge.mov.mobile.data.model.movie.MovieModel
import ge.mov.mobile.data.repository.MovieRepository
import ge.mov.mobile.util.d
import kotlinx.coroutines.*

class MovieDetailViewModel @ViewModelInject
constructor(private val movieRepository: MovieRepository): ViewModel() {
    var movieDetails: MutableLiveData<MovieModel> = MutableLiveData()
    var movieName = MutableLiveData<String>()
    var movieDescription = MutableLiveData<String>()
    var movieCountry = MutableLiveData<String>()

    var isLoading: MutableLiveData<Int> = MutableLiveData()

    suspend fun getMovieDetails(id: Int): MovieModel? = withContext(Dispatchers.Main) {
            movieDetails.value = movieRepository.getDetails(id)
            movieName.postValue(movieRepository.getName(movieDetails.value))
            movieCountry.postValue(movieRepository.getCountry(movieDetails.value))
            movieDescription.postValue(movieRepository.getDescription(movieDetails.value))
        d("MovieDetailsResponse", movieDetails.value.toString())
        return@withContext movieDetails.value
    }

    fun getCast(id: Int) = runBlocking {
        movieRepository.getCast(id)
    }

    suspend fun insertMovieToDatabase(context: Context, movie: MovieEntity) {
        viewModelScope.launch {
            val db = DBService.getInstance(context)
            db.movieDao().insert(movie)
        }
    }

    suspend fun deleteFromDatabase(context: Context, movie: MovieEntity) {
        viewModelScope.launch {
            val db = DBService.getInstance(context)
            db.movieDao()
                .delete(movie.id, movie.adjaraId)
        }
    }

    suspend fun isMovieSaved(context: Context, id: Int) : Boolean = withContext(Dispatchers.IO) {
        DBService.getInstance(context)
                .movieDao()
                .isMovieSaved(id)
    }
}