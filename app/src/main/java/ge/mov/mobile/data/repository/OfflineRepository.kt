package ge.mov.mobile.data.repository

import androidx.lifecycle.asLiveData
import ge.mov.mobile.data.database.dao.OfflineMovieDao
import ge.mov.mobile.data.database.entity.OfflineMovieEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineRepository @Inject constructor(private val offlineDao: OfflineMovieDao) {

    suspend fun insert(offlineMovieEntity: OfflineMovieEntity) {
        offlineDao.save(offlineMovieEntity)
    }

    fun getAll() = offlineDao.getAll()

    fun getCount() = offlineDao.getCount().asLiveData()

    suspend fun delete(m: OfflineMovieEntity) {
        offlineDao.delete(m)
    }

    suspend fun delete(ms: List<OfflineMovieEntity>) = withContext(Dispatchers.IO) {
        ms.forEach {
            offlineDao.delete(it)
        }
    }

    suspend fun deleteByIds(ms: List<Int>) {
        ms.forEach {
            offlineDao.deleteByIds(it)
        }
    }
}