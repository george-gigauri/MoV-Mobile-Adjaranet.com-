package ge.mov.mobile.model.movie

import com.google.gson.annotations.SerializedName

data class Covers (
    val data: CoversData
)

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
)