package ge.mov.mobile.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ge.mov.mobile.model.movie.Movie
import ge.mov.mobile.service.APIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentSearchViewModel: ViewModel() {
    private val searchResults = MutableLiveData<Movie>()

    fun search(keywords: String, page: Int) : LiveData<Movie> {
        APIService.invoke().searchMovie(keywords = keywords, page = page)
            .enqueue(object : Callback<Movie> {
                override fun onResponse(
                    call: Call<Movie>,
                    response: Response<Movie>
                ) {
                    if (response.code() == 200)
                    {
                        response.body().let {
                                searchResults.value = it

                         //   searchResults.value?.data?.plus(it!!.data)
                        }
                    }
                }

                override fun onFailure(call: Call<Movie>, t: Throwable) {

                }
            })

        return searchResults
    }
}