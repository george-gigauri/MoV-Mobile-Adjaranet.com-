package ge.mov.mobile.paging.movies

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.data.network.APIService

class MoviePagingSource(
    private val api: APIService,
    private val type: String,
    private val language: String,
    private val filterGenres: List<Int?>? = null,
    private val filterYearFrom: Int? = null,
    private val filterYearTo: Int? = null
) : PagingSource<Int, Data>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Data> {
        val position = params.key ?: 1

        return try {
            val genres: StringBuilder = StringBuilder()
            filterGenres?.let { g ->
                g.forEach {
                    genres.append("${it},")
                }
            }

            Log.i("RequestUrlMovie", "Genres: $genres")

            val response =
                if (type == "popular") {
                    api.getTop(
                        page = position,
                        perPage = params.loadSize,
                        genre = genres.toString()
                    )
                } else {
                    if (genres.isEmpty() || genres.toString() == "0,") {
                        Log.i("RequestUrlMovie", "getMovies")
                        api.getMovies(
                            page = position, type = type, per_page = params.loadSize,
                            yearsRange = if (filterYearTo != null && filterYearFrom != null) "$filterYearFrom,$filterYearTo" else null,
                            language = language
                        )
                    } else {
                        api.getMoviesWithGenre(
                            genre = genres.toString(),
                            page = position, type = type, per_page = params.loadSize,
                            yearsRange = if (filterYearTo != null && filterYearFrom != null) "$filterYearFrom,$filterYearTo" else null,
                            language = language
                        )
                    }
                }
            val result = response.body()

            LoadResult.Page(
                data = result?.data ?: emptyList(),
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (!result?.data.isNullOrEmpty()) position + 1 else null
            )
        } catch (exception: Exception) {
            Log.i("RequestUrlMovieErr", exception.message.toString())
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Data>) = state.anchorPosition
}