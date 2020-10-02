package ge.mov.mobile.ui.viewmodel

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import ge.mov.mobile.database.DBService
import ge.mov.mobile.database.MovieEntity
import ge.mov.mobile.model.Series.Person
import ge.mov.mobile.model.movie.MovieItemModel
import ge.mov.mobile.model.movie.MovieModel
import ge.mov.mobile.service.APIService
import ge.mov.mobile.util.Utils
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDetailViewModel: ViewModel() {
    private val visible = View.VISIBLE
    private val gone = View.GONE

    var movieDetails: MutableLiveData<MovieModel> = MutableLiveData()
    var movieName = MutableLiveData<String>()
    var movieDescription = MutableLiveData<String>()
    var movieCountry = MutableLiveData<String>()

    var isLoading: MutableLiveData<Int> = MutableLiveData()

    fun getMovieDetails(context: Context, id: Long) : LiveData<MovieModel> {
        isLoading.postValue(visible)
        val movie = APIService.invoke().getMovie(id)
        movie.enqueue(object : Callback<MovieItemModel>
        {
            override fun onResponse(
                call: Call<MovieItemModel>,
                response: Response<MovieItemModel>
            ) {
                movieDetails.value = response.body()?.data
                getMovieName(context)
                getMovieDescription(context)
                getCountry(context)

                isLoading.postValue(gone)
            }

            override fun onFailure(call: Call<MovieItemModel>, t: Throwable) {
                isLoading.postValue(gone)
                Log.i("Movieresponse", t.message.toString())
            }
        })
        return movieDetails
    }

    private fun getMovieName(context: Context) : LiveData<String> {
        val i = movieDetails.value

        val language = Utils.loadLanguage(context)
        val lang_code = if (language?.id == "ka") "GEO" else "ENG"

        movieName.value = if (lang_code == "GEO")
            if (i?.primaryName != "")
                i?.primaryName
            else
                i.secondaryName
        else
            i?.secondaryName

        return movieName
    }

    private fun getCountry(context: Context) : LiveData<String> {
        val i = movieDetails.value?.countries?.data

        val language = Utils.loadLanguage(context)
        val lang_code = if (language?.id == "ka") "GEO" else "ENG"


        for (j in i!!) {
            if (lang_code == "GEO") {
                movieCountry.postValue(j.primaryName)
                break
            }

            if (lang_code == "ENG") {
                movieCountry.postValue(j.secondaryName)
                break
            }
        }

        return movieCountry
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

                }
            })

        return castArr
    }

    private fun getMovieDescription(context: Context) : LiveData<String> {
        val i = movieDetails.value
        val language = Utils.loadLanguage(context)
        val lang_code = if (language?.id == "ka") "GEO" else "ENG"

        var description: String = ""
        for (j in i!!.plots.data)
            if (j.language == "ENG") {
                description = j.description
                break
            }

        for (j in i.plots.data) {
            if (j.language == lang_code) {
                description = j.description
                break
            }
        }

        movieDescription.postValue(description)
        return movieDescription
    }

    fun insertMovieToDatabase(context: Context, movie: MovieEntity) {
        viewModelScope.launch {
            val db = DBService.getInstance(context)
            db.movieDao().insert(movie)
        }
    }

    fun deleteFromDatabase(context: Context, movie: MovieEntity) {
        viewModelScope.launch {
            val db = DBService.getInstance(context)
            db.movieDao()
                .delete(movie.id, movie.adjaraId)
        }
    }

    fun isMovieSaved(context: Context, id: Long) : Boolean = runBlocking {
        DBService.getInstance(context)
                .movieDao()
                .isMovieSaved(id)
    }
}