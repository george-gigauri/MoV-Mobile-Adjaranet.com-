package ge.mov.mobile.ui.activity.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.databinding.ObservableList
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ge.mov.mobile.R
import ge.mov.mobile.database.MovieSubscriptionEntity
import ge.mov.mobile.model.Series.EpisodeFiles
import ge.mov.mobile.model.movie.Seasons
import ge.mov.mobile.ui.viewmodel.DialogViewModel
import ge.mov.mobile.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


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

    var click = 0

    goBack.setOnClickListener {
        season.onItemSelectedListener = null
        language.onItemSelectedListener = null
        quality.onItemSelectedListener = null
        click = 0
        dialog.cancel()
        dialog.dismiss()
    }

    vm.loadSeasons(id).observe(activity, Observer {
        click = 0
        if (it != null) {
            val seasons = getSeasonsArray(it)
            var episodesArray: List<String>?
            season.adapter = ArrayAdapter(applicationContext, R.layout.spinner_item_view, seasons)

            season.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    click++
                    vm.loadFiles(id, p2 + 1).observe(activity, Observer { episodeFiles ->
                        s = p2 + 1

                        episodesArray = getEpisodesArray(episodeFiles)
                        episode.adapter =
                            ArrayAdapter(activity, R.layout.spinner_item_view, episodesArray!!)

                        episode.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    p0: AdapterView<*>?,
                                    p1: View?,
                                    p2: Int,
                                    p3: Long
                                ) {
                                    e = p2 + 1
                                }

                                override fun onNothingSelected(p0: AdapterView<*>?) {}
                            }

                        val languages = getLanguagesArray(episodeFiles)
                        val qualities = getQualityArray(episodeFiles)

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

                        language.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    p0: AdapterView<*>?,
                                    p1: View?,
                                    p2: Int,
                                    p3: Long
                                ) {
                                    lang = language.getItemAtPosition(p2) as String
                                }

                                override fun onNothingSelected(p0: AdapterView<*>?) {
                                }
                            }

                        quality.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    p0: AdapterView<*>?,
                                    p1: View?,
                                    p2: Int,
                                    p3: Long
                                ) {
                                    qual = quality.getItemAtPosition(p2) as String
                                }

                                override fun onNothingSelected(p0: AdapterView<*>?) {
                                }
                            }

                        if (click == 1) {
                            if (subscribed != null) {
                                if (!seasons.isNullOrEmpty() && !episodesArray.isNullOrEmpty()) {
                                    if (subscribed.season != 0 && subscribed.episode != 0) {
                                        season.setSelection(subscribed.season - 1)
                                        episode.setSelection(subscribed.episode - 1)
                                    }
                                }
                            }
                        }
                    })
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    })

    var url = ""
    playMovie.setOnClickListener {
        if (subscribed != null) {
            if (subscribed.season != 0 && subscribed.episode != 0) {
                s = subscribed.season - 1
                e = subscribed.episode - 1
            }
        } else {
            s -= 1
            e -= 1
        }

        var l1 = true
        var l2 = true
        vm.loadFiles(id, s).observe(activity, Observer {
            for (i in it.data[e].files) {
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

        click = 0
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