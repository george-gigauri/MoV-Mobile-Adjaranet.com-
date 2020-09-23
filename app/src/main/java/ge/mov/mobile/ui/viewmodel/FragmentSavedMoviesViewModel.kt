package ge.mov.mobile.ui.viewmodel

import android.content.Context
import androidx.lifecycle.*
import ge.mov.mobile.database.DBService
import ge.mov.mobile.database.MovieEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FragmentSavedMoviesViewModel : ViewModel() {
    private val movies: MutableLiveData<List<MovieEntity>> = MutableLiveData()

    private var job = Job()
    private val scope = CoroutineScope(job + Dispatchers.Main)

    fun getAllSavedMovies(context: Context) : LiveData<List<MovieEntity>> {
        scope.launch {
            val m = DBService.getInstance(context)
                .movieDao()
                .getAllMovies()

            movies.postValue(m)
        }
        return movies
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}