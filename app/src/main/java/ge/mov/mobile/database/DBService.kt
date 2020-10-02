package ge.mov.mobile.database

import android.content.Context
import androidx.room.Room

object DBService {
    private var db: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        if (db == null) {
            synchronized(AppDatabase::class) {
                db = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "saved_movies"
                ).allowMainThreadQueries().build()
            }
        }

        return db!!
    }
}