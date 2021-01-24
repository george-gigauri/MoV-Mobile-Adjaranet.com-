package ge.mov.mobile.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DBService {
    private var db: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        if (db == null) {
            synchronized(AppDatabase::class) {
                db = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "saved_movies"
                ).addMigrations(object : Migration(1, 2) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE user_subscriptions ADD COLUMN saved_on TEXT")
                    }
                }).allowMainThreadQueries().build()
            }
        }

        return db!!
    }
}