package ge.mov.mobile.ui.activity.dialog

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import ge.mov.mobile.data.model.Series.EpisodeFiles
import ge.mov.mobile.data.repository.EpisodeRepository
import kotlinx.coroutines.runBlocking

class DialogViewModel @ViewModelInject constructor(private val repository: EpisodeRepository) : ViewModel() {
    fun loadFiles(id: Int, season: Int) : EpisodeFiles? = runBlocking {
        val response = runBlocking { repository.getEpisodes(id, season) }
        response
    }

    fun loadSeasons(id: Int) : List<String> = runBlocking {
        val response = runBlocking { repository.getSeasons(id) }
        response
    }
}