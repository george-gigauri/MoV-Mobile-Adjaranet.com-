package ge.mov.mobile.ui.activity.movie

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ge.mov.mobile.data.database.DBService
import ge.mov.mobile.data.database.MovieSubscriptionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date
import java.text.DateFormat
import java.text.SimpleDateFormat

class WatchViewModel : ViewModel() {
    @SuppressLint("SimpleDateFormat")
    fun saveVideoState(context: Context, movie: MovieSubscriptionEntity) = viewModelScope.launch(Dispatchers.IO) {
        val dateFormat : DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = Date()
        val dateTime = dateFormat.format(date)
        movie.savedOn = dateTime

        DBService.getInstance(context)
            .subscriptionDao()
            .save(movie)
    }

    fun loadState(context: Context, id: Int) : MovieSubscriptionEntity? = runBlocking {
        DBService.getInstance(context)
            .subscriptionDao()
            .get(id)
    }
}