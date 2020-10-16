package ge.mov.mobile.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ge.mov.mobile.model.basic.BasicMovie
import ge.mov.mobile.model.movie.Movie
import ge.mov.mobile.service.APIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentSearchViewModel: ViewModel() {
    private val searchResults = MutableLiveData<BasicMovie>()

    fun search(keywords: String, page: Int) : LiveData<BasicMovie> {
        APIService.invoke().searchMovie(keywords = keywords, page = page)
            .enqueue(object : Callback<BasicMovie> {
                override fun onResponse(
                    call: Call<BasicMovie>,
                    response: Response<BasicMovie>
                ) {
                    if (response.code() == 200)
                    {
                        response.body().let {
                                searchResults.value = it

                         //   searchResults.value?.data?.plus(it!!.data)
                        }
                    }
                }

                override fun onFailure(call: Call<BasicMovie>, t: Throwable) {

                }
            })

        return searchResults
    }
}