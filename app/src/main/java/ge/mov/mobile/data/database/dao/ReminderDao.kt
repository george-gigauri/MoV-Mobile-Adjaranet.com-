package ge.mov.mobile.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ge.mov.mobile.data.database.entity.ReminderMovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Insert
    suspend fun saveReminder(reminderMovieEntity: ReminderMovieEntity)

    @Delete
    suspend fun deleteReminder(reminderMovieEntity: ReminderMovieEntity)

    @Query("SELECT * FROM reminders")
    fun getAll(): Flow<List<ReminderMovieEntity>>
}