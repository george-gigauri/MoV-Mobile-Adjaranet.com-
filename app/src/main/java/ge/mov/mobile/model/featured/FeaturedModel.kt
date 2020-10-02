package ge.mov.mobile.model.featured

import ge.mov.mobile.model.featured.Cover
import ge.mov.mobile.model.movie.Posters
import java.util.*

data class FeaturedModel (
    val id: Long,
    val adjaraId: Long,
 //   val primaryName: String,
   // val secondaryName: String,
   // val tertiaryName: String,
   // val originalName: String,
    val year: Int,
   // val releaseDate: Date,
   // val imdbUrl: String,
   // val isTvShow: Boolean,
  //  val budget: String,
  //  val income: String,
    val duration: Int,
    val adult: Boolean,
    val watchCount: Int,
  //  val canBePlayed: Boolean,
    val cover: Cover,
    val poster: String,
    val posters: Posters
)