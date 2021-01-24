package ge.mov.mobile.data.repository

import ge.mov.mobile.data.model.Series.EpisodeFiles
import ge.mov.mobile.data.network.APIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpisodeRepository @Inject constructor(private val api: APIService) {
    suspend fun getEpisodes(id: Int, season: Int): EpisodeFiles? = withContext(Dispatchers.IO) {
        val response = api.getMovieFile(id, season)
        val body = response.body() ?: return@withContext null
        val arr = ArrayList<String>()

        for (i in body.data)
            arr.add("${i.episode} - ${i.title}")

        return@withContext body
    }

    suspend fun getSeasons(id: Int): List<String> = withContext(Dispatchers.IO) {
        val response = api.getMovie(id)
        val body = response.body() ?: return@withContext emptyList()
        val seasons = body.data.seasons.data
        val temp = ArrayList<String>()

        for (i in seasons)
            temp.add(i.number.toString())

        return@withContext if (body.data.seasons.data.isNullOrEmpty()) emptyList() else temp
    }
}