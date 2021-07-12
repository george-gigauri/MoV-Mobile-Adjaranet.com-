package ge.mov.mobile.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.mov.mobile.data.database.DBService
import ge.mov.mobile.data.database.entity.MovieEntity
import ge.mov.mobile.data.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FragmentSavedMoviesViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val fuser = Firebase.auth.currentUser

    private val _movies: MutableLiveData<List<MovieEntity>> = MutableLiveData()
    val movies: LiveData<List<MovieEntity>> = _movies

    fun getAllSavedMovies(context: Context) = viewModelScope.launch {
        withContext(Dispatchers.IO) {

            if (fuser != null) {

                val m = DBService.getInstance(context)
                    .movieDao()
                    .getAllMovies()

                m.forEach {
                    movieRepository.saveMovie(it.id, it.adjaraId)
                }

                val result = Firebase.firestore.collection("users")
                    .document(fuser.uid)
                    .collection("saved")
                    .get()
                    .await()

                val list = result.toObjects(MovieEntity::class.java)
                _movies.postValue(list)
            } else {
                val m = DBService.getInstance(context)
                    .movieDao()
                    .getAllMovies()

                _movies.postValue(m)
            }
        }
    }
}