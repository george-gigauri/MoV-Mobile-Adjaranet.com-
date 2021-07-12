package ge.mov.mobile.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ge.mov.mobile.data.database.DBService
import ge.mov.mobile.data.database.dao.MovieDao
import ge.mov.mobile.data.database.entity.MovieEntity
import ge.mov.mobile.data.model.basic.BasicMovie
import ge.mov.mobile.data.network.APIService
import ge.mov.mobile.paging.search.SearchMoviePagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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

    fun getSavedMovies(context: Context, limit: Int) = flow {
        if (fuser != null) {
            val result = Firebase.firestore.collection("users")
                .document(fuser.uid)
                .collection("saved")
                .limitToLast(limit.toLong())
                .orderBy("id")
                .get()
                .await()

            val list = result.toObjects(MovieEntity::class.java)
            emit(list)
        } else {
            val m = DBService.getInstance(context)
                .movieDao()
                .getMovies(limit)
            emit(m)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getSlides() = withContext(Dispatchers.IO) {
        val response = api.getFeatured()
        return@withContext response.body()
    }

    suspend fun getGenres() = withContext(Dispatchers.IO) {
        val genres = api.getGenres()
        return@withContext genres.body()
    }

    suspend fun getPopularMovies(): BasicMovie? = withContext(Dispatchers.IO) {
        val response = api.getTop()
        return@withContext response.body()
    }

    suspend fun getMovies(type: String = "movie") = withContext(Dispatchers.IO) {
        val response = api.getMovies(page = 1, type = type)
        return@withContext response.body()
    }

    fun search(keyword: String) =
        Pager(
            config = searchPagingConfig,
            pagingSourceFactory = { SearchMoviePagingSource(api, keyword) }
        ).liveData
}