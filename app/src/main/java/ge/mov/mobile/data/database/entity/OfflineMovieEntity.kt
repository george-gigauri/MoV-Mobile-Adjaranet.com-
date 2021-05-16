package ge.mov.mobile.data.database.entity

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_movies")
data class OfflineMovieEntity(
        @PrimaryKey(autoGenerate = true)
        val i: Int? = null,
        var id: Int,
        var title: String?,
        var image: Bitmap?,
        var season: Int? = 0,
        var episode: Int? = 0,
        var language: String?,
        var src: String?,
        var savedAt: Long?
) {
        var expiresAt: Long? = savedAt ?: System.currentTimeMillis() + 1209600000
        var isExpiringSoon: Boolean? =
                (System.currentTimeMillis() + 259200000) <= expiresAt ?: System.currentTimeMillis()
}
