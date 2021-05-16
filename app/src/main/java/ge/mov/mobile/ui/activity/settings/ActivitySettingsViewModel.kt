package ge.mov.mobile.ui.activity.settings

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import ge.mov.mobile.data.database.DBService
import ge.mov.mobile.data.repository.OfflineRepository
import kotlinx.coroutines.runBlocking

class ActivitySettingsViewModel
@ViewModelInject constructor(private val repository: OfflineRepository) : ViewModel() {
    fun getMoviesCount(context: Context): String = runBlocking {
        DBService.getInstance(context)
            .movieDao()
            .getSavedMoviesCount()
            .toString()
    }

    fun getDownloadedMoviesCount() = repository.getCount()
}