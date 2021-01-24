package ge.mov.mobile.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MovieEntity::class, MovieSubscriptionEntity::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun subscriptionDao(): SubscriptionDao
}