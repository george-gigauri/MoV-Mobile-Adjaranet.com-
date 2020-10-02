package ge.mov.mobile.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MovieEntity::class, MovieSubscriptionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun subscriptionDao(): SubscriptionDao
}