package ge.mov.mobile.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_subscriptions")
data class MovieSubscriptionEntity (
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0,
    var season: Int = 0,
    var episode: Int = 0,
    var time: Long? = null,
    @ColumnInfo(name = "saved_on")
    var savedOn: String? = null
)