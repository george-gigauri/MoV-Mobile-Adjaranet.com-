package ge.mov.mobile.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.mov.mobile.data.database.DBService
import ge.mov.mobile.data.database.entity.MovieEntity
import ge.mov.mobile.data.database.entity.OfflineMovieEntity
import ge.mov.mobile.data.model.movie.MovieModel
import ge.mov.mobile.data.repository.MovieRepository
import ge.mov.mobile.data.repository.OfflineRepository
import ge.mov.mobile.extension.d
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val offlineRepository: OfflineRepository
) : ViewModel() {

    var movieDetails: MutableLiveData<MovieModel> = MutableLiveData()
    var movieName = MutableLiveData<String>()
    var movieDescription = MutableLiveData<String>()
    var movieCountry = MutableLiveData<String>()

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
            withContext(Dispatchers.IO) {
                val db = DBService.getInstance(context)
                db.movieDao().insert(movie)

                movieRepository.saveMovie(movie.id, movie.adjaraId)
            }
        }
    }

    suspend fun deleteFromDatabase(context: Context, movie: MovieEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val db = DBService.getInstance(context)
                db.movieDao()
                    .delete(movie.id, movie.adjaraId)

                movieRepository.unsaveMovie(movie.id, movie.adjaraId)
            }
        }
    }

    suspend fun isMovieSaved(context: Context, id: Int): Boolean = withContext(Dispatchers.IO) {
        DBService.getInstance(context)
            .movieDao()
            .isMovieSaved(id)

                ||

                movieRepository.isMovieSavedToFirebase(id, id)
    }

    fun insertDownloadMovie(m: OfflineMovieEntity) = viewModelScope.launch {
        offlineRepository.insert(m)
    }
}