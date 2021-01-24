package ge.mov.mobile.data.model.basic

import ge.mov.mobile.data.model.movie.MetaInfo
import ge.mov.mobile.data.model.movie.Posters

data class BasicMovie (
    val data: List<Data>,
    val meta: MetaInfo
)

data class Data (
    val id: Int,
    val adjaraId: Int,
    val primaryName: String,
    val secondaryName: String,
    val originalName: String,
    val posters: Posters?,
    val adult: Boolean
)

