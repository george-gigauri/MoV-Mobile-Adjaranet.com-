package ge.mov.mobile.data.model.movie

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class Genre (
    val id: Int,
    val primaryName: String,
    val secondaryName: String,
 //   val tertiaryName: String,
 //   val backgroundImage: String
): Serializable {
    var isSelected: Boolean = false
}