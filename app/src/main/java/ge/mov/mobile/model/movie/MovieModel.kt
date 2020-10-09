package ge.mov.mobile.model.movie

import ge.mov.mobile.model.featured.Cover
import java.util.*

data class MovieModel (
    val id: Long,
    val adjaraId: Long,
    val primaryName: String,
    val secondaryName: String,
   // val tertiaryName: String,
  //  val originalName: String,
    val year: Int,
  //  val releaseDate: Date,
    val imdbUrl: String,
 //   val isTvShow: Boolean,
 //   val budget: String,
 //   val income: String,
 //   val duration: Int,
    val adult: Boolean,
    val watchCount: Int,
    val canBePlayed: Boolean,
    //val cover: Cover,
    val covers: Covers,
    //val poster: String,
    val posters: Posters,
    val rating: Rating,
    val languages: Languages,
    val plots: Plots,
    val seasons: Seasons,
    val genres: Genres,
    val regionAllowed: Boolean,
   // val trailers: Trailers,
    val countries: Countries,
    val primaryDescription: String,
    val secondaryDescription: String,
  //  val tertiaryDescription: String,
    val type: String
)

data class Seasons (
    val data: List<Season>
)

data class Season (
    val episodesCount: Int,
    val movieId: Long,
    val name: String,
    val number: Int
)