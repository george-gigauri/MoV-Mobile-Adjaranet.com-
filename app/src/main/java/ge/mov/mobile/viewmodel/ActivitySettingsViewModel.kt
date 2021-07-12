package ge.mov.mobile.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.mov.mobile.data.database.DBService
import ge.mov.mobile.data.repository.OfflineRepository
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ActivitySettingsViewModel @Inject constructor(
    private val repository: OfflineRepository
) : ViewModel() {

    fun getMoviesCount(context: Context): String = runBlocking {
        DBService.getInstance(context)
            .movieDao()
            .getSavedMoviesCount()
            .toString()
    }

    fun getDownloadedMoviesCount() = repository.getCount()
}