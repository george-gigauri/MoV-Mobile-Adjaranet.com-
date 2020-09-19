package ge.mov.mobile.ui.activity.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.Window
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ge.mov.mobile.R
import ge.mov.mobile.ui.viewmodel.DialogViewModel


fun Activity.showMovieDialog(activity: FragmentActivity, id: Long)
{
    var s: Int = 0
    var e: Int = 0
    var lang: String = "ENG"
    var qual: String = "HIGH"

    val vm = ViewModelProviders.of(activity).get(DialogViewModel::class.java)

    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.movie_settings_dialog)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent);

    val seasonsContainer: LinearLayout = dialog.findViewById(R.id.episodes_spinners)
    val season: Spinner = dialog.findViewById(R.id.season_spinner)
    val episode: Spinner = dialog.findViewById(R.id.episode_spinner)
    val language: Spinner = dialog.findViewById(R.id.language_spinner)
    val quality: Spinner = dialog.findViewById(R.id.quality_spinner)
    val goBack: Button = dialog.findViewById(R.id.go_back)
    val playMovie: Button = dialog.findViewById(R.id.play_movie)

    goBack.setOnClickListener {
        dialog.cancel()
        dialog.dismiss()
    }

    vm.loadSeasons(id).observe(activity, Observer {
        if (it != null) {
            val temparr = ArrayList<String>()

            for (i in it.data)
                temparr.add("${i.number}")

            season.adapter = ArrayAdapter(this, R.layout.spinner_item_view, temparr)

            season.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    vm.loadFiles(id, p2 + 1).observe(activity, Observer { episodeFiles ->
                        s = p2 + 1

                        val episodesArray = ArrayList<String>()

                        for (i in episodeFiles.data)
                            episodesArray.add("${i.episode} - ${i.title}")

                        episode.adapter = ArrayAdapter(
                            activity,
                            R.layout.spinner_item_view,
                            episodesArray
                        )
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

                                override fun onNothingSelected(p0: AdapterView<*>?) {
                                }
                            }

                        val languages = ArrayList<String>()
                        for (i in episodeFiles.data)
                            for (j in i.files)
                                if (!languages.contains(j.lang))
                                    languages.add(j.lang)

                        val qualities = ArrayList<String>()
                        for (i in episodeFiles.data)
                            for (j in i.files)
                                for (k in j.files)
                                    if (!qualities.contains(k.quality))
                                        qualities.add(k.quality)

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
                    })
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    })

    var url = ""
    playMovie.setOnClickListener {
        var l1 = true
        var l2 = true
        vm.loadFiles(id, s).observe(activity, Observer {
            for (i in it.data[e - 1].files) {
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

//        val intent = Intent(this, WatchActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        intent.putExtra("src", url)
//        startActivity(intent)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(url), "video/*")
        startActivity(intent)

        dialog.cancel()
        dialog.dismiss()
    }

    dialog.show()
}