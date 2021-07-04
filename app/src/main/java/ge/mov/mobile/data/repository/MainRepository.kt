package ge.mov.mobile.data.repository

import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import ge.mov.mobile.data.database.dao.MovieDao
import ge.mov.mobile.data.model.basic.BasicMovie
import ge.mov.mobile.data.network.APIService
import ge.mov.mobile.paging.search.SearchMoviePagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val api: APIService,
    private val db: MovieDao
) {

    companion object {
        val searchPagingConfig = PagingConfig(
            initialLoadSize = 8,
            pageSize = 8,
            maxSize = 40,
            enablePlaceholders = true
        )
    }

    fun getSavedMovies(limit: Int) = flow {
        emit(db.getMovies(limit))
    }

    fun getSavedMovies() = flow {
        emit(db.getAllMovies())
    }.asLiveData()

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