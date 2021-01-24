package ge.mov.mobile.data.model.movie

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class Covers (
    val data: CoversData
) : Serializable

data class CoversData (
    @SerializedName("145")
    val _145: String,

    @SerializedName("367")
    val _367: String,

    @SerializedName("510")
    val _510: String,

    @SerializedName("1050")
    val _1050: String,

    @SerializedName("1920")
    val _1920: String
): Serializable