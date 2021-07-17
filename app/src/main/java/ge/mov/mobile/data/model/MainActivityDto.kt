package ge.mov.mobile.data.model

import ge.mov.mobile.data.database.entity.MovieEntity
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.data.model.featured.FeaturedModel
import ge.mov.mobile.data.model.movie.Genre

data class MainActivityDto(
    var featured: List<FeaturedModel>,
    var categories: List<Genre>,
    var saved: List<MovieEntity>,
    var top: List<Data>,
    var movies: List<Data>,
    var series: List<Data>
)
