package ge.mov.mobile.paging.movies

import android.util.Log
import androidx.paging.PagingSource
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.data.network.APIService

class MoviePagingSource(
    private val api: APIService,
    private val type: String,
    private val language: String,
    private val filterGenres: List<Int>? = null,
    private val filterYearFrom: Int? = null,
    private val filterYearTo: Int? = null
) : PagingSource<Int, Data>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Data> {
        val position = params.key ?: 1

        return try {
            var genres: String? = ""
            filterGenres?.forEach {
                genres += "${it},"
            }

            if (genres?.length == 0 || genres.isNullOrEmpty())
                genres = null

            val response =
                if (type == "popular") {
                    api.getTop(page = position, perPage = params.loadSize, genre = genres)
                } else {
                    api.getMovies(page = position, type = type, per_page = params.loadSize,
                        genre = genres,
                        yearsRange = if (filterYearTo != null && filterYearFrom != null) "$filterYearFrom,$filterYearTo" else null,
                        language = language
                    )
                }

            Log.i("RequestUrlMovie", response.raw().request.url.toString())
            val result = response.body()

            LoadResult.Page(
                data = result!!.data,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (!result.data.isNullOrEmpty()) position + 1 else null
            )
        } catch (exception: Exception) {
            Log.i("RequestUrlMovie", exception.message.toString())
            LoadResult.Error(exception)
        }
    }
}