package ge.mov.mobile.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import ge.mov.mobile.data.network.APIService
import ge.mov.mobile.paging.movies.MoviePagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AllMovieRepository @Inject constructor(private val api: APIService) {
    companion object {
        val pagingConfig = PagingConfig(
            initialLoadSize = 24,
            pageSize = 12,
            maxSize = 80,
            enablePlaceholders = true
        )
    }

    fun loadMovies(type: String, filterGenres: List<Int>? = null, language: String = "GEO", filterYearFrom: Int? = null, filterYearTo: Int? = null) = Pager(
        config = pagingConfig,
        pagingSourceFactory = { MoviePagingSource(api, type, language, filterGenres, filterYearFrom, filterYearTo) }
    ).liveData

    fun loadPopular(type: String, filterGenres: List<Int>? = null) = Pager(
        config = pagingConfig,
        pagingSourceFactory = { MoviePagingSource(api, type, "GEO", filterGenres) }
    ).liveData

    suspend fun getGenres() = withContext(Dispatchers.IO) {
        api.getGenres(perPage = 100, page = 1)
    }

    suspend fun loadLanguages() = withContext(Dispatchers.IO) {
        val response = api.getLanguages()

        if (response.isSuccessful && response.body() != null) {
            return@withContext response.body()!!.data
        } else emptyList()
    }
}