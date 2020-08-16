package ge.mov.mobile.ui.activity.viewmodel

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

class MainActivityViewModel : ViewModel() {
    private val arr: MutableLiveData<List<MovieModel>> = MutableLiveData<List<MovieModel>>()

    fun getMovies(): LiveData<List<MovieModel>>
    {
        val call = APIService.invoke().getMovies(1)
        call.enqueue(object : Callback<Movie>
        {
            override fun onFailure(call: Call<Movie>, t: Throwable) {
                Log.i("OKMyValue", t.message!!)
            }

            override fun onResponse(
                call: Call<Movie>,
                response: Response<Movie>
            ) {
                arr.value = response.body()?.data

                //Log.i("OKMyValue", response.body()!!)
            }

        })

        return arr
    }
}