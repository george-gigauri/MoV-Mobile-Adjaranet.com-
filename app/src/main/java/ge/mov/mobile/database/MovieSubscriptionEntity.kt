package ge.mov.mobile.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_subscriptions")
data class MovieSubscriptionEntity (
    @PrimaryKey(autoGenerate = false)
    var id: Long = 0L,
    var season: Int = 0,
    var episode: Int = 0,
    val time: Long? = null
)