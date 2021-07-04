package ge.mov.mobile.ui.fragment.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ge.mov.mobile.data.model.basic.BasicMovie
import ge.mov.mobile.data.network.APIService
import ge.mov.mobile.di.module.AppModule
import kotlinx.coroutines.runBlocking

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

       // val body = runBlocking {
       //     AppModule.getMoviesApi(AppModule.getRetrofit())
       //         .getMovies(page = page, genre = genre.toString(), yearsRange = yearFilters, per_page = perPage)
       //         .body()
       // }

      //  if (body != null) {
       //     movies.postValue(body)
     //   }
        isLoading.postValue(false)

        return movies
    }
}