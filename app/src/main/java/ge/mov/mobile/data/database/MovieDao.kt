package ge.mov.mobile.data.database

import androidx.room.*

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg movie: MovieEntity)

    @Query("DELETE FROM SAVED_MOVIES WHERE id = :id AND adjaraId = :adjaraId")
    suspend fun delete(id: Int, adjaraId: Int)

    @Query("SELECT * FROM saved_movies WHERE id = :id")
    suspend fun getMovie(id: Long) : MovieEntity

    @Query("SELECT EXISTS (SELECT * FROM SAVED_MOVIES WHERE id = :id)")
    suspend fun isMovieSaved(id: Int) : Boolean

    @Query("SELECT COUNT(*) FROM SAVED_MOVIES")
    suspend fun getSavedMoviesCount() : Int

    @Query("SELECT * FROM SAVED_MOVIES ORDER BY uid DESC")
    suspend fun getAllMovies() : List<MovieEntity>
}