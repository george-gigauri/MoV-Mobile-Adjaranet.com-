package ge.mov.mobile.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ge.mov.mobile.model.Series.Person
import ge.mov.mobile.model.movie.MovieItemModel
import ge.mov.mobile.model.movie.MovieModel
import ge.mov.mobile.service.APIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDetailViewModel: ViewModel() {
    var movieDetails: MutableLiveData<MovieModel> = MutableLiveData()
    private var movieName = MutableLiveData<String>()

    fun getMovieDetails(id: Long) : LiveData<MovieModel>
    {
        val movie = APIService.invoke().getMovie(id)
        movie.enqueue(object : Callback<MovieItemModel>
        {
            override fun onResponse(
                call: Call<MovieItemModel>,
                response: Response<MovieItemModel>
            ) {
                movieDetails.value = response.body()?.data

                //Log.i("Movieresponse", response.body()?.data.toString())
                Log.i("Movieresponse", movieDetails.value.toString())
            }

            override fun onFailure(call: Call<MovieItemModel>, t: Throwable) {
                Log.i("Movieresponse", t.message.toString())
            }
        })
        return movieDetails
    }

    fun getMovieName() : LiveData<String>
    {
        val i = movieDetails.value
        movieName.value = if (i != null)
        {
            if (i.primaryName != "" && i.secondaryName != "")
                if (i.primaryName == "")
                    if (i.secondaryName == "")
                        if (i.tertiaryName  == "")
                            i.tertiaryName
                        else
                            i.tertiaryName
                    else
                        i.primaryName
                else
                    i.primaryName
            else
                i.primaryName
        } else
        {
            "NULL"
        }

        return movieName
    }

    fun getCast(id: Long) : LiveData<Person> {
        val castArr = MutableLiveData<Person>()

        APIService.invoke().getCast(id=id)
            .enqueue(object : Callback<Person>
            {
                override fun onResponse(call: Call<Person>, response: Response<Person>) {
                    castArr.value = response.body()
                }

                override fun onFailure(call: Call<Person>, t: Throwable) {
                    Log.i("Cast", t.cause!!.message.toString())
                }
            })

        return castArr
    }
}