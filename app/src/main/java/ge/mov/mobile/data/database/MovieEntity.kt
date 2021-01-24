package ge.mov.mobile.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_movies")
data class MovieEntity (
    @PrimaryKey(autoGenerate = true)
    val uid: Int? = null,
    val id: Int,
    val adjaraId: Int
)