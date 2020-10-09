package ge.mov.mobile.ui.viewmodel

import android.util.Log
import android.view.View.VISIBLE
import android.view.View.GONE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ge.mov.mobile.model.featured.Featured
import ge.mov.mobile.model.featured.FeaturedModel
import ge.mov.mobile.model.movie.Genres
import ge.mov.mobile.model.movie.Movie
import ge.mov.mobile.model.movie.MovieModel
import ge.mov.mobile.service.APIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityViewModel : ViewModel() {
    var isLoading = MutableLiveData<Int>()
    private var page: Int = 0
    private val movies: MutableLiveData<List<MovieModel>> = MutableLiveData()
    private val series = MutableLiveData<List<MovieModel>>()
    private val genres = MutableLiveData<Genres>()

    fun getMovies(): LiveData<List<MovieModel>> {
        this.isLoading.value = VISIBLE
        this.page++

        val call = APIService.invoke().getMovies(page)
        call.enqueue(object : Callback<Movie> {
            override fun onFailure(call: Call<Movie>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<Movie>,
                response: Response<Movie>
            ) {
                isLoading.value = GONE
                movies.value = response.body()?.data
            }
        })
        return movies
    }

    fun getSeries(): LiveData<List<MovieModel>> {
        isLoading.value = VISIBLE
        this.page++

        APIService.invoke().getMovies(page = page, type = "series")
            .enqueue(object : Callback<Movie> {
                override fun onResponse(call: Call<Movie>, response: Response<Movie>) {

                    series.value = response.body()?.data
                    isLoading.value = GONE
                }

                override fun onFailure(call: Call<Movie>, t: Throwable) {
                    isLoading.value = GONE
                    Log.i("MainActivity", t.message.toString())
                }
            })

        return series
    }

    fun getSlides(): LiveData<List<FeaturedModel>> {
        isLoading.value = VISIBLE
        val slides = MutableLiveData<List<FeaturedModel>>()

        APIService.invoke().getFeatured()
            .enqueue(object : Callback<Featured>
            {
                override fun onResponse(call: Call<Featured>, response: Response<Featured>) {
                    if (response.body() != null)
                        if (slides.value.isNullOrEmpty())
                            slides.value = response.body()?.data
                    isLoading.value = GONE
                }

                override fun onFailure(call: Call<Featured>, t: Throwable) {
                    isLoading.value = GONE
                    Log.i("MainActivity", t.message.toString())
                }
            })
        return slides
    }

    fun getGenresFull(): LiveData<Genres> {
        APIService.invoke().getGenres()
            .enqueue(object : Callback<Genres> {
                override fun onResponse(call: Call<Genres>, response: Response<Genres>) {
                    if (response.code() == 200)
                    {
                        response.body().let {
                            genres.value = it
                        }
                    }
                }

                override fun onFailure(call: Call<Genres>, t: Throwable) {
                    Log.i("MainActivityViewModel", t.message.toString())
                }
            })

        return genres
    }
}