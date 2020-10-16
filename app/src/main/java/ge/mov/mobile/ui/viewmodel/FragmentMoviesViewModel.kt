package ge.mov.mobile.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ge.mov.mobile.model.basic.BasicMovie
import ge.mov.mobile.model.movie.Movie
import ge.mov.mobile.service.APIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentMoviesViewModel : ViewModel() {
    private val movies: MutableLiveData<BasicMovie> = MutableLiveData()
    private var isLoading = MutableLiveData<Boolean>()
    var page = 0

    fun isLoading() : LiveData<Boolean> {
        return isLoading
    }

    fun getMovies(genre: Int? = null, filtersFromYear: Int? = null, filtersToYear: Int? = null, perPage: Int = 10) : LiveData<BasicMovie> {
        page++
        isLoading.postValue(true)

        var yearFilters: String? = null
        if (filtersFromYear != null && filtersToYear != null) {
            yearFilters = "$filtersFromYear,$filtersToYear"
        }

        APIService.invoke().getMovies(page = page, genre = genre, yearsRange = yearFilters, per_page = perPage)
            .enqueue(object : Callback<BasicMovie> {
                override fun onResponse(call: Call<BasicMovie>, response: Response<BasicMovie>) {
                    if (response.code() == 200) {
                        response.body().let {
                            if (it != null) {
                                movies.postValue(it)
                                isLoading.postValue(false)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BasicMovie>, t: Throwable) {
                    Log.i("FragmentMoviesViewModel", t.message.toString())
                    isLoading.postValue(false)
                }
            })

        return movies
    }
}