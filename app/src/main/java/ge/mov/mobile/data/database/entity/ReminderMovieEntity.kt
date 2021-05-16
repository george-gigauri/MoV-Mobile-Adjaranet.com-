package ge.mov.mobile.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderMovieEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int?,
    val adjaraId: Int?
)