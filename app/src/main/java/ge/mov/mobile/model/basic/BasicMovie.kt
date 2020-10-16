package ge.mov.mobile.model.basic

import ge.mov.mobile.model.movie.MetaInfo
import ge.mov.mobile.model.movie.Posters

data class BasicMovie (
    val data: List<Data>,
    val meta: MetaInfo
)

data class Data (
    val id: Long,
    val adjaraId: Long,
    val primaryName: String,
    val secondaryName: String,
    val originalName: String,
    val posters: Posters
)

