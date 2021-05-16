package ge.mov.mobile.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ge.mov.mobile.data.database.dao.MovieDao
import ge.mov.mobile.data.database.dao.OfflineMovieDao
import ge.mov.mobile.data.database.dao.ReminderDao
import ge.mov.mobile.data.database.dao.SubscriptionDao
import ge.mov.mobile.data.database.entity.MovieEntity
import ge.mov.mobile.data.database.entity.MovieSubscriptionEntity
import ge.mov.mobile.data.database.entity.OfflineMovieEntity
import ge.mov.mobile.data.database.entity.ReminderMovieEntity

@Database(
    entities = [
        MovieEntity::class,
        MovieSubscriptionEntity::class,
        OfflineMovieEntity::class,
        ReminderMovieEntity::class], version = 6, exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun offlineMovieDao(): OfflineMovieDao
    abstract fun reminderDao(): ReminderDao
}