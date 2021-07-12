package ge.mov.mobile.ui.fragment

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.R
import ge.mov.mobile.analytics.FirebaseLogger
import ge.mov.mobile.data.database.entity.MovieSubscriptionEntity
import ge.mov.mobile.data.database.entity.OfflineMovieEntity
import ge.mov.mobile.data.model.Series.EpisodeFiles
import ge.mov.mobile.databinding.BottomSheetViewBinding
import ge.mov.mobile.extension.adapt
import ge.mov.mobile.extension.toBitmap
import ge.mov.mobile.extension.toast
import ge.mov.mobile.service.MovieDownloadService
import ge.mov.mobile.ui.DialogHelper
import ge.mov.mobile.ui.DialogViewModel
import ge.mov.mobile.ui.activity.WatchActivity
import ge.mov.mobile.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class BottomFragment: BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetViewBinding
    private val viewModel by viewModels<DialogViewModel>()
    private var _id: Int? = null
    private var _poster: String? = null
    private var _title: String? = null
    private var subscribtion: MovieSubscriptionEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _id = arguments?.getInt("id")
        _poster = arguments?.getString("poster")
        _title = arguments?.getString("title")
        subscribtion = DialogHelper.load(requireContext(), _id!!)

        return inflater.inflate(R.layout.bottom_sheet_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = BottomSheetViewBinding.bind(view)

        dialog?.setOnShowListener {
            val ds =
                (dialog as BottomSheetDialog?)?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(ds!!)
            behavior.skipCollapsed = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

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
                    val episodes = viewModel.loadFiles(
                        _id!!,
                        (spinnerSeasons.selectedItem as String).toInt()
                    )
                    if (episodes != null) {
                        spinnerEpisodes.adapt(episodes.data.map { "${it.episode} - ${it.title}" })

                        if (subscribtion != null)
                            if (subscribtion?.season == (spinnerSeasons.selectedItem as String).toInt() && subscribtion?.episode != null && subscribtion?.episode!! > 0)
                                spinnerEpisodes.setSelection(subscribtion!!.episode - 1)

                        spinnerEpisodes.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                                ) {
                                    binding.episodeThumbnail.load(episodes.data[spinnerEpisodes.selectedItemPosition].poster) {
                                        placeholder(R.color.colorAccent)
                                        error(Constants.getErrorImage())
                                    }

                                    val languages = getLanguages(
                                        episodes,
                                        if (seasons[0] == "0") -1 else position
                                    )
                                    binding.spinnerLanguages.adapt(languages)

                                    binding.spinnerLanguages.onItemSelectedListener =
                                        object : AdapterView.OnItemSelectedListener {
                                            override fun onItemSelected(
                                                parent: AdapterView<*>?,
                                                view: View?,
                                                p0: Int,
                                                id: Long
                                            ) {
                                                val qualities = getQualities(
                                                    episodes,
                                                    if (seasons[0] == "0") -1 else position
                                                ).sortedBy { it }
                                                binding.qualitySpinner.adapt(qualities)
                                            }

                                            override fun onNothingSelected(parent: AdapterView<*>?) =
                                                Unit
                                        }
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {}
                            }
                    }

                    binding.playButton.setOnClickListener {
                        play(episodes)
                    }

                    binding.downloadButton.setOnClickListener {
                        downloadMovie(episodes)
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

        val url = getUrl(season, episode, language, quality, files)

        if (binding.spinnerSeasons.adapter.count > 0 && binding.spinnerSeasons.adapter.getItem(0)
                .toString() != "0"
        ) {
            val movie = MovieSubscriptionEntity()
            movie.id = _id!!
            movie.season = binding.spinnerSeasons.selectedItemPosition + 1
            movie.episode = binding.spinnerEpisodes.selectedItemPosition + 1

            DialogHelper.subscribe(requireContext(), movie)
        }

        val intent = Intent(requireContext(), WatchActivity::class.java)
        intent.putExtra("s", season + 1)
        intent.putExtra("e", episode)
        intent.putExtra("id", _id)
        intent.putExtra("src", url.url)
        intent.putExtra("subtitle", url.captions ?: "")
        intent.putExtra("def_lang", language)
        intent.putExtra("def_quality", quality)
        intent.putExtra("files", files)
        intent.putExtra("file_id", url.fileId)
        intent.putExtra(
            "movie_title",
            "S${binding.spinnerSeasons.selectedItem}, E${binding.spinnerEpisodes.selectedItem}"
        )
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun downloadMovie(files: EpisodeFiles?) {
        val season = binding.spinnerSeasons.selectedItemPosition
        val episode = binding.spinnerEpisodes.selectedItemPosition
        val language = binding.spinnerLanguages.selectedItem as String
        val quality = binding.qualitySpinner.selectedItem as String

        val isMovieType =
            (binding.spinnerSeasons.selectedItem as String) == "0"

        val url = getUrl(season, episode, language, quality, files)

        download(season + 1, episode + 1, language, url.url, isMovieType)
    }

    private fun getUrl(
        season: Int,
        episode: Int,
        language: String,
        quality: String,
        files: EpisodeFiles?
    ): Urls {
        var l1 = true
        var l2 = true
        var url = ""
        var captions: String? = null
        var fileId: Long? = null

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
                        fileId = j.id
                    }
                }
            }
            if (!l1 and !l2)
                break
        }

        return Urls(url, captions, fileId)
    }

    data class Urls(val url: String?, val captions: String?, val fileId: Long?)

    private fun getQualities(files: EpisodeFiles?, position: Int): List<String> {
        return try {
            files?.data?.find { it.episode == position + 1 }
                ?.files!![0]
                .files.map { it.quality }
        } catch (e: java.lang.Exception) {
            emptyList()
        }
    }

    private fun getLanguages(files: EpisodeFiles?, position: Int): List<String> {
        return try {
            files?.data?.filter { it.episode == position + 1 }!![0]
                .files.map { it.lang }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun download(
        season: Int,
        episode: Int,
        lang: String,
        url: String?,
        isMovieType: Boolean
    ) {
        FirebaseLogger(requireContext())
            .logDownloadMovie(_id!!)

        requireContext().toast("Download has started...")

        val service = Intent(requireContext(), MovieDownloadService::class.java)
        service.putExtra("id", _id)
        service.putExtra("title", _title)
        service.putExtra("season", season)
        service.putExtra("episode", episode)
        service.putExtra("lang", lang)
        service.putExtra("url", url)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            requireContext().startForegroundService(service)
        else requireActivity().startService(service)

        lifecycleScope.launch(Dispatchers.IO) {
            val m = OfflineMovieEntity(
                id = _id ?: -1,
                title = _title,
                image = requireActivity().toBitmap(_poster),
                season = if (!isMovieType) season else 0,
                episode = if (!isMovieType) episode else 0,
                language = lang,
                src = requireActivity().filesDir.absolutePath +
                        "/Offline/Movies/${_id}_${season}_${episode}_$lang.mp4",
                savedAt = System.currentTimeMillis()
            )

            viewModel.save(m)

            withContext(Dispatchers.Main) {
                dialog?.cancel()
            }
        }
    }
}