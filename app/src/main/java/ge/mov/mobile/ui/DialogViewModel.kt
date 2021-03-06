package ge.mov.mobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.mov.mobile.data.database.entity.OfflineMovieEntity
import ge.mov.mobile.data.model.Series.EpisodeFiles
import ge.mov.mobile.data.repository.EpisodeRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class DialogViewModel @Inject constructor(
    private val repository: EpisodeRepository
) : ViewModel() {
    fun loadFiles(id: Int, season: Int): EpisodeFiles? = runBlocking {
        val response = runBlocking { repository.getEpisodes(id, season) }
        response
    }

    fun loadSeasons(id: Int): List<String> = runBlocking {
        val response = runBlocking { repository.getSeasons(id) }
        response
    }

    fun save(m: OfflineMovieEntity) = viewModelScope.launch {
        repository.save(m)
    }
}