package ge.mov.mobile.data.repository

import ge.mov.mobile.data.database.dao.OfflineMovieDao
import ge.mov.mobile.data.database.entity.OfflineMovieEntity
import ge.mov.mobile.data.model.Series.EpisodeFiles
import ge.mov.mobile.data.network.APIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpisodeRepository @Inject constructor(
    private val api: APIService,
    private val offlineDao: OfflineMovieDao
) {

    suspend fun getEpisodes(id: Int, season: Int): EpisodeFiles? = withContext(Dispatchers.IO) {
        val response = api.getMovieFile(id, season)
        val body = response.body() ?: return@withContext null
        val arr = body.data.map { "${it.episode} - ${it.title}" }

        return@withContext body
    }

    suspend fun getSeasons(id: Int): List<String> = withContext(Dispatchers.IO) {
        val response = api.getMovie(id)
        val body = response.body() ?: return@withContext emptyList()
        val seasons = body.data.seasons.data
        val temp = seasons.map { it.number.toString() }

        return@withContext if (body.data.seasons.data.isNullOrEmpty()) emptyList() else temp
    }

    suspend fun save(model: OfflineMovieEntity) {
        offlineDao.save(model)
    }
}