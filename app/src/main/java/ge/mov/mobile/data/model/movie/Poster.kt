package ge.mov.mobile.data.model.movie

import com.google.gson.annotations.SerializedName

data class Posters (
    val data: Poster?
)

data class Poster (
    @SerializedName("240")
    val _240: String
)