package ge.mov.mobile.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ge.mov.mobile.model.basic.BasicMovie
import ge.mov.mobile.service.APIService
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonMovieViewModel : ViewModel() {
    private val movies: MutableLiveData<BasicMovie> = MutableLiveData()

    fun getMoviesByActor(actorId: Long) : LiveData<BasicMovie> {
        APIService.invoke()
            .getMoviesByPerson(actorId)
            .enqueue(object : Callback<BasicMovie> {
                override fun onResponse(call: Call<BasicMovie>, response: Response<BasicMovie>) {
                    val body = response.body()
                    if (body != null) {
                        movies.value = body
                    } else {
                        Log.i("PersonMovies", "null body")
                        Log.i("PersonMovies", response.raw().request.url.toString())
                    }
                }

                override fun onFailure(call: Call<BasicMovie>, t: Throwable) {
                    Log.i("PersonMovies", t.message.toString())
                }
            })
        return movies
    }
}