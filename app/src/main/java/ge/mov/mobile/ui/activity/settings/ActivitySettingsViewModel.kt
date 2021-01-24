package ge.mov.mobile.ui.activity.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import ge.mov.mobile.data.database.DBService
import kotlinx.coroutines.runBlocking

class ActivitySettingsViewModel : ViewModel() {
    fun getMoviesCount(context: Context) : String = runBlocking {
        DBService.getInstance(context)
            .movieDao()
            .getSavedMoviesCount()
            .toString()
    }
}