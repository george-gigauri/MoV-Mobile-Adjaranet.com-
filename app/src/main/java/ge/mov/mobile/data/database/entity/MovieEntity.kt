package ge.mov.mobile.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_movies")
data class MovieEntity (
    @PrimaryKey(autoGenerate = true)
    val uid: Int? = null,
    val id: Int = 0,
    val adjaraId: Int = 0
)