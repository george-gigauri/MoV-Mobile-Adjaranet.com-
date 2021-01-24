package ge.mov.mobile.ui.activity.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import ge.mov.mobile.data.model.basic.BasicMovie
import ge.mov.mobile.data.network.APIService
import ge.mov.mobile.data.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FragmentSearchViewModel @ViewModelInject constructor(private val mainRepository: MainRepository): ViewModel() {
    private val query = MutableLiveData("")

    val result = query.switchMap {
        mainRepository.search(it).cachedIn(viewModelScope)
    }.cachedIn(viewModelScope)

    fun search(keyword: String) = query.postValue(keyword)
}