package ge.mov.mobile.data.model.movie

data class MovieModel(
    val id: Int,
    val adjaraId: Int,
    val primaryName: String,
    val secondaryName: String,
    val originalName: String,
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
    val trailers: Trailers,
    val lastUpdatedSeries: LastUpdatedSeriesData?,
    val countries: Countries,
    val primaryDescription: String,
    val secondaryDescription: String,
    //  val tertiaryDescription: String,
    val type: String
) {
    fun getNameByLanguage(code: String?) = if (code != null) {
        if (code == "GEO") {
            if (primaryName != "") primaryName else originalName
        } else {
            if (secondaryName != "") secondaryName else originalName
        }
    } else originalName

    fun getDescriptionByLanguage(code: String?): String {
        if (code != null) {
            plots.data.forEach {
                if (it.language == code)
                    return it.description
            }
        }

        return "@{ vm.description }"
    }
}

data class Seasons(
    val data: List<Season>
)

data class Season(
    val episodesCount: Int,
    val movieId: Long,
    val name: String,
    val number: Int
) {
    override fun toString() = number.toString()
}

data class LastUpdatedSeriesData(
    val data: LastUpdatedSeries?
)

data class LastUpdatedSeries(
    val season: Int?,
    val episode: Int?
)