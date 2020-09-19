package ge.mov.mobile.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ge.mov.mobile.model.movie.Movie
import ge.mov.mobile.model.movie.MovieModel
import ge.mov.mobile.service.APIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentMoviesViewModel : ViewModel() {
    private val movies: MutableLiveData<List<MovieModel>> = MutableLiveData()
   // private var page = 0

    fun getMovies(genre: Int? = null, filtersFromYear: Int? = null, filtersToYear: Int? = null, perPage: Int = 10, page: Int) : LiveData<List<MovieModel>> {
       // page++

        var yearFilters: String? = null
        if (filtersFromYear != null && filtersToYear != null) {
            yearFilters = "$filtersFromYear,$filtersToYear"
        }

        APIService.invoke().getMovies(page, genre = genre, yearsRange = yearFilters, per_page = perPage)
            .enqueue(object : Callback<Movie> {
                override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                    if (response.code() == 200) {
                        response.body().let {
                            if (it != null) {
                                    if (movies.value != null)
                                        movies.value = movies.value?.plus(it.data)
                                    else
                                        movies.value = it.data
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<Movie>, t: Throwable) {
                    Log.i("FragmentMoviesViewModel", t.message.toString())
                }
            })

        return movies
    }
}