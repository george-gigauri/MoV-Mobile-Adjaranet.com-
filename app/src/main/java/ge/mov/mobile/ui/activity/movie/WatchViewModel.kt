package ge.mov.mobile.ui.activity.movie

import android.annotation.SuppressLint
import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ge.mov.mobile.data.database.DBService
import ge.mov.mobile.data.database.entity.MovieSubscriptionEntity
import ge.mov.mobile.data.model.IPAddress
import ge.mov.mobile.data.network.APIService
import ge.mov.mobile.data.network.IPAddressService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class WatchViewModel @ViewModelInject constructor(
    private val api: APIService,
    private val ipApi: IPAddressService
) : ViewModel() {

    private val _fileUrl = MutableLiveData<String?>()
    val fileUrl: LiveData<String?> = _fileUrl

    private val _isRegionAllowed = MutableLiveData<IPAddress>()
    val isRegionAllowed: LiveData<IPAddress> = _isRegionAllowed

    init {
        getRegionAllowed()
    }

    @SuppressLint("SimpleDateFormat")
    fun saveVideoState(context: Context, movie: MovieSubscriptionEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = Date()
            val dateTime = dateFormat.format(date)
            movie.savedOn = dateTime

            DBService.getInstance(context)
                .subscriptionDao()
                .save(movie)
        }

    fun loadState(context: Context, id: Int): MovieSubscriptionEntity? = runBlocking {
        DBService.getInstance(context)
            .subscriptionDao()
            .get(id)
    }

    private fun getRegionAllowed() = viewModelScope.launch {
        val response = withContext(Dispatchers.IO) { ipApi.getMyIp() }
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                _isRegionAllowed.value = body
            }
        }
    }

    fun getFileUrl(id: Int, adjaraId: Int, fileId: Long) = viewModelScope.launch {
        val response = withContext(Dispatchers.IO) {
            api.getMoviePlayUrl(id, fileId)
        }

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                _fileUrl.value = body.url
            }
        } else {
            _fileUrl.value = null
        }
    }
}