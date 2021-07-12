package ge.mov.mobile.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.mov.mobile.data.repository.AllMovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ViewModelAll @Inject constructor(
    private val repository: AllMovieRepository
) : ViewModel() {

    private val currentRequest = MutableLiveData("movie")
    private val filteredGenres = MutableLiveData<List<Int?>?>(null)
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
                            repository.loadMovies(
                                type,
                                genreFilters,
                                language,
                                yearFrom.toInt(),
                                yearTo.toInt()
                            ).cachedIn(viewModelScope)
                        }.cachedIn(viewModelScope)
                    }.cachedIn(viewModelScope)
                }.cachedIn(viewModelScope)
            }
        }
    }

    fun load(
        type: String = "series",
        filtersGenre: List<Int>? = null,
        language: String = "GEO",
        yearFrom: Int? = null,
        yearTo: Int? = null
    ) {
        currentRequest.value = type
        filteredGenres.value = filtersGenre
        filteredLanguage.value = language
        if (yearFrom != null)
            filterYearFrom.value = yearFrom.toString()

        if (yearTo != null)
            filterYearTo.value = yearTo.toString()
    }

    fun setGenre(id: Int?) {
        id?.let {
            filteredGenres.value = listOf(id)
        }
    }

    suspend fun loadGenres() = withContext(Dispatchers.IO) { repository.getGenres() }

    suspend fun getLanguages() = withContext(Dispatchers.IO) { repository.loadLanguages() }
}