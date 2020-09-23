package ge.mov.mobile.database

import androidx.room.*

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg movie: MovieEntity)

    @Delete
    suspend fun delete(movie: MovieEntity)

    @Query("SELECT * FROM saved_movies WHERE id = :id")
    suspend fun getMovie(id: Long) : MovieEntity

    @Query("SELECT EXISTS (SELECT * FROM SAVED_MOVIES WHERE id = :id)")
    suspend fun isMovieSaved(id: Long) : Boolean

    @Query("SELECT COUNT(*) FROM SAVED_MOVIES")
    suspend fun getSavedMoviesCount() : Int

    @Query("SELECT * FROM SAVED_MOVIES")
    suspend fun getAllMovies() : List<MovieEntity>
}