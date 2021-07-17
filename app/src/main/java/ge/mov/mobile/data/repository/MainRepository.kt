package ge.mov.mobile.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ge.mov.mobile.data.database.dao.MovieDao
import ge.mov.mobile.data.database.entity.MovieEntity
import ge.mov.mobile.data.model.MainActivityDto
import ge.mov.mobile.data.network.APIService
import ge.mov.mobile.paging.search.SearchMoviePagingSource
import ge.mov.mobile.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val api: APIService,
    private val db: MovieDao
) {

    private val fuser = Firebase.auth.currentUser

    companion object {
        val searchPagingConfig = PagingConfig(
            initialLoadSize = 8,
            pageSize = 8,
            maxSize = 40,
            enablePlaceholders = true
        )
    }

    fun retrieveAll() = flow {
        emit(State.loading())

        val slides = getSlides()
        val saved = getSavedMovies()
        val popular = getPopularMovies()
        val genres = getGenres()
        val movies = getMovies()
        val series = getMovies("series")

        emit(
            State.success(
                MainActivityDto(
                    slides!!,
                    genres!!,
                    saved,
                    popular!!,
                    movies!!,
                    series!!
                )
            )
        )
    }.catch {
        emit(State.failure(it.message))
    }.flowOn(Dispatchers.IO)

    private suspend fun getSavedMovies(limit: Int = 10): List<MovieEntity> {
        return if (fuser != null) {
            val result = Firebase.firestore.collection("users")
                .document(fuser.uid)
                .collection("saved")
                .limitToLast(limit.toLong())
                .orderBy("id")
                .get()
                .await()

            result.toObjects(MovieEntity::class.java)
        } else {
            db.getMovies(limit)
        }
    }

    private suspend fun getSlides() = api.getFeatured().body()?.data

    private suspend fun getGenres() = api.getGenres().body()?.data

    private suspend fun getPopularMovies() = api.getTop().body()?.data

    private suspend fun getMovies(type: String = "movie") =
        api.getMovies(page = 1, type = type).body()?.data

    fun search(keyword: String) =
        Pager(
            config = searchPagingConfig,
            pagingSourceFactory = { SearchMoviePagingSource(api, keyword) }
        ).liveData
}