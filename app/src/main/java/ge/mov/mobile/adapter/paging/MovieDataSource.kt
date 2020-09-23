package ge.mov.mobile.adapter.paging

import android.util.Log
import androidx.paging.PageKeyedDataSource
import ge.mov.mobile.model.movie.Movie
import ge.mov.mobile.model.movie.MovieModel
import ge.mov.mobile.service.APIService
import ge.mov.mobile.util.Constants.PAGE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDataSource(private val genre: Int?) : PageKeyedDataSource<Int, MovieModel>() {
    private var page = 1

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, MovieModel>
    ) {
        APIService.invoke().getMovies(genre=genre, page = page).enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                val resp = response.body()
                callback.onResult(resp!!.data, null,
                    resp.meta.pagination.currentPage + 1
                )
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                println(t.message)
            }
        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, MovieModel>) {
        APIService.invoke().getMovies(page = params.key, genre = genre)
            .enqueue(object : Callback<Movie> {
                override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                    response.body().let {
                        val key = if (params.key > 1) params.key - 1 else 0
                        callback.onResult(it!!.data, key)
                    }
                }

                override fun onFailure(call: Call<Movie>, t: Throwable) {
                }
            })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, MovieModel>) {
        APIService.invoke().getMovies(genre = genre, page = params.key)
            .enqueue(object : Callback<Movie> {
                override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                    val body = response.body()

                    if (body?.meta?.pagination?.totalPages!! >= params.key)
                    {
                        callback.onResult(body.data, params.key + 1)
                    }
                }

                override fun onFailure(call: Call<Movie>, t: Throwable) {
                }
            })
    }

}