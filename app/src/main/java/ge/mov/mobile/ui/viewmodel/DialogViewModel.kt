package ge.mov.mobile.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import ge.mov.mobile.model.Series.EpisodeFiles
import ge.mov.mobile.model.movie.MovieItemModel
import ge.mov.mobile.model.movie.Seasons
import ge.mov.mobile.service.APIService
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DialogViewModel : ViewModel() {
    private val files = MutableLiveData<EpisodeFiles>()
    private val seasons = MutableLiveData<Seasons>()

    fun loadFiles(id: Long, season: Int) : LiveData<EpisodeFiles>  = runBlocking {
        APIService.invoke().getMovieFile(id, season)
            .enqueue(object : Callback<EpisodeFiles>
            {
                override fun onResponse(
                    call: Call<EpisodeFiles>,
                    response: Response<EpisodeFiles>
                ) {
                    if (response.code() == 200)
                    {
                        Log.i("DialogRequestUrl", response.raw().request().url().toString())
                        files.value = response.body()
                    }
                }

                override fun onFailure(call: Call<EpisodeFiles>, t: Throwable) {
                    Log.i("DialogViewModel", t.message.toString())
                }
            })
        files
    }

    fun loadSeasons(id: Long) : LiveData<Seasons> = runBlocking {
        APIService.invoke().getMovie(id).enqueue(object : Callback<MovieItemModel>
        {
            override fun onResponse(
                call: Call<MovieItemModel>,
                response: Response<MovieItemModel>
            ) {
                if (response.code() == 200) {
                    seasons.value = response.body()?.data?.seasons
                }
            }

            override fun onFailure(call: Call<MovieItemModel>, t: Throwable) {
                Log.i("DialogViewModel", t.message.toString())
            }

        })
        seasons
    }
}