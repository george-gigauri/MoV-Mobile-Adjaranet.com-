package ge.mov.mobile.data.database

import androidx.room.*

@Dao
interface SubscriptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(movie: MovieSubscriptionEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSubscription(movie: MovieSubscriptionEntity)

    @Query("SELECT EXISTS (SELECT * FROM USER_SUBSCRIPTIONS WHERE id = :id)")
    suspend fun isSaved(id: Int) : Boolean

    @Query("SELECT * FROM USER_SUBSCRIPTIONS WHERE id = :id")
    suspend fun get(id: Int) : MovieSubscriptionEntity?

    @Query("DELETE FROM USER_SUBSCRIPTIONS WHERE (season = 0 AND episode = 0) AND saved_on IS NOT NULL AND DATE(saved_on) <= DATE('now', '-7 days')")
    suspend fun clearPositionStates()  // Deletes only movies from the subscription table
}