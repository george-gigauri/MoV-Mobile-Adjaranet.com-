package ge.mov.mobile.data.model

import ge.mov.mobile.data.model.movie.MovieModel

data class MovieReminder(
    val data: List<ReminderMovieModel>?
)

data class ReminderMovieModel(
    val movies: List<MovieModel>?,
    val period: String?
)