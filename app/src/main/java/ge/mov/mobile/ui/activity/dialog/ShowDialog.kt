package ge.mov.mobile.ui.activity.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.Window
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import ge.mov.mobile.R
import ge.mov.mobile.database.MovieSubscriptionEntity
import ge.mov.mobile.model.Series.EpisodeFiles
import ge.mov.mobile.model.movie.Seasons
import ge.mov.mobile.ui.viewmodel.DialogViewModel
import ge.mov.mobile.util.toast
import kotlinx.coroutines.runBlocking


fun Activity.showMovieDialog(activity: FragmentActivity, id: Long) {
    var s: Int = 0
    var e: Int = 0
    var lang: String = "ENG"
    var qual: String = "HIGH"

    val vm = ViewModelProviders.of(activity).get(DialogViewModel::class.java)

    val subscribed = DialogHelper.load(applicationContext, id)

    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.movie_settings_dialog)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent);

    val season: Spinner = dialog.findViewById(R.id.season_spinner)
    val episode: Spinner = dialog.findViewById(R.id.episode_spinner)
    val language: Spinner = dialog.findViewById(R.id.language_spinner)
    val quality: Spinner = dialog.findViewById(R.id.quality_spinner)
    val goBack: Button = dialog.findViewById(R.id.go_back)
    val playMovie: Button = dialog.findViewById(R.id.play_movie)
    val progressbar: ProgressBar = dialog.findViewById(R.id.progressbar_settings)
    val episodesContainer: LinearLayout = dialog.findViewById(R.id.episodes_spinners)

    goBack.setOnClickListener {
        season.onItemSelectedListener = null
        language.onItemSelectedListener = null
        quality.onItemSelectedListener = null
        progressbar.visibility = View.GONE
        dialog.cancel()
        dialog.dismiss()
    }

    var seasons: List<String>
    var episodesArray: List<String>
    vm.loadSeasons(id).observe(activity, { loadedSeasons ->
        seasons = getSeasonsArray(loadedSeasons)
        season.adapter = ArrayAdapter(applicationContext, R.layout.spinner_item_view, seasons)

        season.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                progressbar.visibility = View.VISIBLE
                vm.loadFiles(id, seasons[position].toInt())
                    .observe(activity, { episodeFiles ->
                        s = season.selectedItemPosition + 1

                        episodesArray = runBlocking {  getEpisodesArray(episodeFiles) }
                        val languages = getLanguagesArray(episodeFiles)
                        val qualities = getQualityArray(episodeFiles)

                        episode.adapter = ArrayAdapter(
                            activity,
                            R.layout.spinner_item_view,
                            episodesArray
                        )

                        if (seasons[0] == "0")
                            episodesContainer.visibility = View.GONE
                        else
                            episodesContainer.visibility = View.VISIBLE

                        language.adapter = ArrayAdapter(
                            activity,
                            R.layout.spinner_item_view,
                            languages
                        )
                        
                        quality.adapter = ArrayAdapter(
                            activity,
                            R.layout.spinner_item_view,
                            qualities
                        )

                            if (subscribed != null) {
                                if (subscribed.season > 0 && subscribed.episode > 0) {
                                  //  season.setSelection(subscribed.season - 1)
                                   // episode.setSelection(subscribed.episode - 1)
                                }
                            }
                        progressbar.visibility = View.GONE
                    })
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    })

    var url = ""
    playMovie.setOnClickListener {
        s = season.selectedItemPosition + 1
        e = episode.selectedItemPosition
        qual = quality.selectedItem as String
        lang = language.selectedItem as String

        var l1 = true
        var l2 = true
        vm.loadFiles(id, s).observe(activity, { files ->
            for (i in files.data[e].files) {
                if (i.lang == lang) {
                    l1 = false

                    for (j in i.files) {
                        if (qual == j.quality) {
                            l2 = false
                            url = j.src
                        }
                    }
                }
                if (!l1 and !l2)
                    break
            }
        })

        if (season.adapter.count > 0 && season.adapter.getItem(0).toString() != "0") {
            val movie = MovieSubscriptionEntity()
            movie.id = id
            movie.season = season.selectedItemPosition + 1
            movie.episode = episode.selectedItemPosition + 1

            DialogHelper.subscribe(applicationContext, movie)
        }

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(url), "video/*")
        startActivity(intent)

        season.onItemSelectedListener = null
        language.onItemSelectedListener = null
        quality.onItemSelectedListener = null
        dialog.cancel()
        dialog.dismiss()
    }

    dialog.show()
}

private fun getSeasonsArray(seasons: Seasons): List<String> {
    val temparr = ArrayList<String>()
    for (i in seasons.data)
        temparr.add("${i.number}")
    return temparr
}

private fun getEpisodesArray(episodes: EpisodeFiles): List<String> {
    val episodesArray = ArrayList<String>()
    for (i in episodes.data)
        episodesArray.add("${i.episode} - ${i.title}")
    return episodesArray
}

private fun getLanguagesArray(episodeFiles: EpisodeFiles): List<String> {
    val languages = ArrayList<String>()
    for (i in episodeFiles.data)
        for (j in i.files)
            if (!languages.contains(j.lang))
                languages.add(j.lang)
    return languages
}

private fun getQualityArray(episodeFiles: EpisodeFiles): List<String> {
    val qualities = ArrayList<String>()
    for (i in episodeFiles.data)
        for (j in i.files)
            for (k in j.files)
                if (!qualities.contains(k.quality))
                    qualities.add(k.quality)
    return qualities
}