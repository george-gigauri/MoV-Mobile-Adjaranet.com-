package ge.mov.mobile.database

import androidx.room.*

@Dao
interface SubscriptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(movie: MovieSubscriptionEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSubscription(movie: MovieSubscriptionEntity)

    @Query("SELECT EXISTS (SELECT * FROM USER_SUBSCRIPTIONS WHERE id = :id)")
    suspend fun isSaved(id: Long) : Boolean

    @Query("SELECT * FROM USER_SUBSCRIPTIONS WHERE id = :id")
    suspend fun get(id: Long) : MovieSubscriptionEntity?
}