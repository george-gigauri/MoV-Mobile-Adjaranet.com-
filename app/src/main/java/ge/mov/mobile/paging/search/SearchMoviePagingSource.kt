package ge.mov.mobile.paging.search

import android.util.Log
import androidx.paging.PagingSource
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.data.network.APIService

class SearchMoviePagingSource(
    private val api: APIService,
    private val keyWord: String
): PagingSource<Int, Data>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Data> {
        val position = params.key ?: 1

        return try {
            val response =
                    api.searchMovie(keywords = keyWord, page = position,  perPage = params.loadSize)

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