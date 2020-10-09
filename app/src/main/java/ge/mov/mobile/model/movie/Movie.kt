package ge.mov.mobile.model.movie

import com.google.gson.annotations.SerializedName

data class Movie (
    val data: ArrayList<MovieModel>,
    val meta: MetaInfo
)

data class MetaInfo (
    val pagination: PaginationInfo
)

data class PaginationInfo (
    val count: Int,

    @SerializedName("current_page")
    val currentPage: Int,
    val links: Link,

    @SerializedName("per_page")
    val perPage: Int,
    val total: Int,

    @SerializedName("total_pages")
    val totalPages: Int
)

data class Link (
    val next: String
)