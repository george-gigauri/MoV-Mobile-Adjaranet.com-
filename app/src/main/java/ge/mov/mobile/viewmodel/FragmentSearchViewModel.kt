package ge.mov.mobile.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.mov.mobile.data.repository.MainRepository
import javax.inject.Inject

@HiltViewModel
class FragmentSearchViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    private val query = MutableLiveData("")

    val result = query.switchMap {
        mainRepository.search(it).cachedIn(viewModelScope)
    }.cachedIn(viewModelScope)

    fun search(keyword: String) = query.postValue(keyword)
}