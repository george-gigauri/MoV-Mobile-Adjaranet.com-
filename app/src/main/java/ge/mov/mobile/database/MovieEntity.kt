package ge.mov.mobile.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_movies")
data class MovieEntity (
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val adjaraId: Long
)