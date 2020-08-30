package ge.mov.mobile.ui.activity.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ge.mov.mobile.model.featured.Featured
import ge.mov.mobile.model.featured.FeaturedModel
import ge.mov.mobile.model.movie.Movie
import ge.mov.mobile.model.movie.MovieModel
import ge.mov.mobile.service.APIService
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityViewModel : ViewModel() {
    private var page: Int = 0
    private val movies: MutableLiveData<List<MovieModel>> = MutableLiveData()
    private val series = MutableLiveData<List<MovieModel>>()

    fun getMovies(): LiveData<List<MovieModel>> {
        this.page++
        val call = APIService.invoke().getMovies(page)
        call.enqueue(object : Callback<Movie> {
            override fun onFailure(call: Call<Movie>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<Movie>,
                response: Response<Movie>
            ) {
                if (movies.value != null)
                    movies.value = movies.value?.plus(response.body()?.data!!)
                else
                    movies.value = response.body()?.data

                Log.i("MainActivityViewModel", response.raw().request().url().toString())
            }
        })
        return movies
    }

    fun getSeries(): LiveData<List<MovieModel>> {
        this.page++
        APIService.invoke().getMovies(page = page, type = "series")
            .enqueue(object : Callback<Movie> {
                override fun onResponse(call: Call<Movie>, response: Response<Movie>) {

                    if (series.value == null) {
                        series.value = response.body()?.data
                    } else {
                        series.value = series.value?.plus(response.body()!!.data)
                    }

                    if (series.value != null)
                        Log.i("MainActivity2", series.value.toString())
                    else
                        Log.i("MainActivity2", "series.value.toString()")
                }

                override fun onFailure(call: Call<Movie>, t: Throwable) {
                    Log.i("MainActivity", t.message.toString())
                }
            })

        return series
    }

    fun getSlides(): LiveData<List<FeaturedModel>> {
        val slides = MutableLiveData<List<FeaturedModel>>()

        APIService.invoke().getFeatured()
            .enqueue(object : Callback<Featured>
            {
                override fun onResponse(call: Call<Featured>, response: Response<Featured>) {
                    if (response.body() != null)
                        if (slides.value.isNullOrEmpty())
                            slides.value = response.body()?.data
                }

                override fun onFailure(call: Call<Featured>, t: Throwable) {
                    Log.i("MainActivity", t.message.toString())
                }
            })

        return slides
    }
}