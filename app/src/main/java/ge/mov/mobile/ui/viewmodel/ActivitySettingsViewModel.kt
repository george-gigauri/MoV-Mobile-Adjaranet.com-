package ge.mov.mobile.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import ge.mov.mobile.database.DBService
import kotlinx.coroutines.runBlocking

class ActivitySettingsViewModel : ViewModel() {
    fun getMoviesCount(context: Context) : String = runBlocking {
        DBService.getInstance(context)
            .movieDao()
            .getSavedMoviesCount()
            .toString()
    }
}