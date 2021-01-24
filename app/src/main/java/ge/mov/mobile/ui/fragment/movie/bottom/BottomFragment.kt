package ge.mov.mobile.ui.fragment.movie.bottom

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.R
import ge.mov.mobile.data.database.MovieSubscriptionEntity
import ge.mov.mobile.data.model.Series.EpisodeFiles
import ge.mov.mobile.databinding.BottomSheetViewBinding
import ge.mov.mobile.ui.activity.dialog.DialogHelper
import ge.mov.mobile.ui.activity.dialog.DialogViewModel
import ge.mov.mobile.ui.activity.movie.WatchActivity
import ge.mov.mobile.util.Constants
import ge.mov.mobile.util.adapt

@AndroidEntryPoint
class BottomFragment: BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetViewBinding
    private val viewModel by viewModels<DialogViewModel>()
    private var _id: Int? = null
    private var subscribtion: MovieSubscriptionEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _id = arguments?.getInt("id")
        subscribtion = DialogHelper.load(requireContext(), _id!!)
        return inflater.inflate(R.layout.bottom_sheet_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = BottomSheetViewBinding.bind(view)

        val seasons = viewModel.loadSeasons(_id!!)
        binding.apply {
            spinnerSeasons.adapt(seasons)
            if (subscribtion != null && subscribtion?.season != null && subscribtion?.season!! > 0)
                spinnerSeasons.setSelection(subscribtion!!.season - 1)

            spinnerSeasons.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val episodes = viewModel.loadFiles(_id!!, spinnerSeasons.selectedItemPosition + 1)
                    if (episodes != null) {
                        spinnerEpisodes.adapt(episodes)

                        if (subscribtion != null)
                            if (subscribtion?.season == (spinnerSeasons.selectedItem as String).toInt() && subscribtion?.episode != null && subscribtion?.episode!! > 0)
                                spinnerEpisodes.setSelection(subscribtion!!.episode - 1)

                        spinnerEpisodes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long
                            ) {
                                binding.episodeThumbnail.load(episodes.data[spinnerEpisodes.selectedItemPosition].poster) {
                                    placeholder(R.color.colorAccent)
                                    error(Constants.getErrorImage())
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                    }

                    val qualities = getQualities(episodes)
                    binding.qualitySpinner.adapt(qualities)

                    val languages = getLanguages(episodes)
                    binding.spinnerLanguages.adapt(languages)

                    binding.playButton.setOnClickListener {
                        play(episodes)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    override fun getTheme() = R.style.BottomSheetDialogTheme

    private fun play(files: EpisodeFiles?) {
        val season = binding.spinnerSeasons.selectedItemPosition
        val episode = binding.spinnerEpisodes.selectedItemPosition
        val language = binding.spinnerLanguages.selectedItem as String
        val quality = binding.qualitySpinner.selectedItem as String

        var l1 = true
        var l2 = true
        var url = ""
        var captions: String? = null
        for (i in files!!.data[episode].files) {
            if (i.lang == language) {
                l1 = false
                if (!i.subtitles.isNullOrEmpty()) {
                    captions = i.subtitles.filter { it.lang == "eng" }[0].url
                }
                for (j in i.files) {
                    if (quality == j.quality) {
                        l2 = false
                        url = j.src
                    }
                }
            }
            if (!l1 and !l2)
                break
        }

        if (binding.spinnerSeasons.adapter.count > 0 && binding.spinnerSeasons.adapter.getItem(0).toString() != "0") {
            val movie = MovieSubscriptionEntity()
            movie.id = _id!!
            movie.season = binding.spinnerSeasons.selectedItemPosition + 1
            movie.episode = binding.spinnerEpisodes.selectedItemPosition + 1

            DialogHelper.subscribe(requireContext(), movie)
        }

        val intent = Intent(requireContext(), WatchActivity::class.java)
        intent.putExtra("s", season + 1)
        intent.putExtra("e", episode + 1)
        intent.putExtra("id", _id)
        intent.putExtra("src", url)
        intent.putExtra("subtitle", captions ?: "")
        intent.putExtra("def_lang", language)
        intent.putExtra("def_quality", quality)
        intent.putExtra("files", files)
        intent.putExtra("movie_title", "S${binding.spinnerSeasons.selectedItem as String}, E${binding.spinnerEpisodes.selectedItem as String}")
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun getQualities(files: EpisodeFiles?) : List<String> {
        if (files == null) return emptyList()

        val temp = ArrayList<String>()
        for (i in files.data)
            for (j in i.files)
                for (k in j.files)
                    if (!temp.contains(k.quality))
                        temp.add(k.quality)
        return temp.sorted()
    }

    private fun getLanguages(files: EpisodeFiles?) : List<String> {
        return if (files != null) {
            val temp = ArrayList<String>()
            for (i in files.data)
                for (j in i.files)
                    if (!temp.contains(j.lang))
                        temp.add(j.lang)
            temp
        } else emptyList()
    }
}