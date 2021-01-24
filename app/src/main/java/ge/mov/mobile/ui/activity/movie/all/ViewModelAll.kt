package ge.mov.mobile.ui.activity.movie.all

import android.util.Log
import androidx.databinding.Bindable
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import ge.mov.mobile.data.model.movie.Genre
import ge.mov.mobile.data.model.movie.Language
import ge.mov.mobile.data.repository.AllMovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class ViewModelAll @ViewModelInject constructor(
    private val repository: AllMovieRepository
) : ViewModel() {
    private val currentRequest = MutableLiveData("movie")
    private val filteredGenres = MutableLiveData<List<Int>?>(null)
    private val filteredLanguage = MutableLiveData("GEO")

    val filterYearFrom = MutableLiveData("0")
    val filterYearTo = MutableLiveData(Calendar.getInstance()[Calendar.YEAR].toString())

    val result = currentRequest.switchMap { type ->
        filteredGenres.switchMap { genreFilters ->
            if (type == "popular")
                repository.loadPopular(type, genreFilters).cachedIn(viewModelScope)
            else {
                filterYearFrom.switchMap { yearFrom ->
                    filterYearTo.switchMap { yearTo ->
                        filteredLanguage.switchMap { language ->
                            repository.loadMovies(type, genreFilters, language, yearFrom.toInt(), yearTo.toInt()).cachedIn(viewModelScope)
                        }.cachedIn(viewModelScope)
                    }.cachedIn(viewModelScope)
                }.cachedIn(viewModelScope)
            }
        }
    }

    fun load(type: String, filtersGenre: List<Int>? = null, language: String = "GEO", yearFrom: Int? = null, yearTo: Int? = null) {
        currentRequest.value = type
        filteredGenres.value = filtersGenre
        filteredLanguage.value = language
        if (yearFrom != null)
            filterYearFrom.value = yearFrom.toString()

        if (yearTo != null)
            filterYearTo.value = yearTo.toString()
    }

    suspend fun loadGenres() = withContext(Dispatchers.IO) { repository.getGenres() }

    suspend fun getLanguages() = withContext(Dispatchers.IO) { repository.loadLanguages() }
}