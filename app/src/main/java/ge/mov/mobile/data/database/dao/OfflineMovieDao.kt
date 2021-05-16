package ge.mov.mobile.data.database.dao

import androidx.room.*
import ge.mov.mobile.data.database.entity.OfflineMovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OfflineMovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(m: OfflineMovieEntity)

    @Query("SELECT * FROM offline_movies WHERE src != NULL OR src != '' ORDER BY savedAt DESC")
    fun getAll(): Flow<List<OfflineMovieEntity>>

    @Query("SELECT COUNT(*) FROM offline_movies")
    fun getCount(): Flow<Int>

    @Query("DELETE FROM offline_movies WHERE i = :ms")
    suspend fun deleteByIds(ms: Int)

    @Delete
    suspend fun delete(m: OfflineMovieEntity)

    @Delete
    suspend fun delete(ms: List<OfflineMovieEntity>)
}