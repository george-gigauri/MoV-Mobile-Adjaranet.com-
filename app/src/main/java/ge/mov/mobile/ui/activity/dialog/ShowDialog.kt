package ge.mov.mobile.ui.activity.dialog

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import ge.mov.mobile.R
import ge.mov.mobile.model.EpisodeFiles
import ge.mov.mobile.model.movie.Movie
import ge.mov.mobile.model.movie.MovieModel
import kotlin.jvm.internal.SpreadBuilder

fun Activity.showMovieDialog(primaryName: String, files: EpisodeFiles)
{
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.movie_settings_dialog)

    val movieName: TextView = dialog.findViewById(R.id.movie_name_dialog)
    val seasonsContainer: LinearLayout = dialog.findViewById(R.id.episodes_spinners)
    val season: Spinner = dialog.findViewById(R.id.season_spinner)
    val episode: Spinner = dialog.findViewById(R.id.episode_spinner)
    val langugae: Spinner = dialog.findViewById(R.id.language_spinner)
    val quality: Spinner = dialog.findViewById(R.id.quality_spinner)
    val goBack: Button = dialog.findViewById(R.id.go_back)
    val playMovie: Button = dialog.findViewById(R.id.play_movie)

    movieName.text = primaryName
    if (files.data.size >= 0)
        seasonsContainer.visibility = View.GONE
    else {
        seasonsContainer.visibility = View.VISIBLE
    }
    dialog.show()
}