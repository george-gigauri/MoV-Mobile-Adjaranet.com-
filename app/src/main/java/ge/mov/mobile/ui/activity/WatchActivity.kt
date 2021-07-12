package ge.mov.mobile.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout.*
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.R
import ge.mov.mobile.analytics.FirebaseLogger
import ge.mov.mobile.data.database.entity.MovieSubscriptionEntity
import ge.mov.mobile.data.model.Series.EpisodeFiles
import ge.mov.mobile.databinding.ActivityWatchBinding
import ge.mov.mobile.extension.toast
import ge.mov.mobile.extension.visible
import ge.mov.mobile.viewmodel.WatchViewModel
import ge.mov.mobile.util.*
import ge.mov.mobile.util.Constants.playInstead
import kotlinx.android.synthetic.main.fragment_saved_movies.*
import kotlinx.coroutines.*
import java.util.*

@AndroidEntryPoint
class WatchActivity : AppCompatActivity(), Player.EventListener {

    private var isScreenLocked = false
    private val TIME_INTERVAL = 2000
    private var mBackPressed: Long = 0

    private var _binding: ActivityWatchBinding? = null
    private val binding: ActivityWatchBinding
        get() = _binding!!

    private lateinit var logger: FirebaseLogger

    companion object {
        private var movie: MovieSubscriptionEntity = MovieSubscriptionEntity()
        private var movieSrc: String = ""
        private var subtitlesSrc: String = ""
        private var fileId: Long = 0L
        private var series: EpisodeFiles? = null
        private var defaultLanguage: String = "GEO"
        private var defaultQuality: String = "HIGH"
        private var type: Int = 1
    }

    private val vm: WatchViewModel by viewModels()

    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var lockButton: ImageView

    private var subscribeOnPause = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = uiOptions

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        _binding = ActivityWatchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        logger = FirebaseLogger(this)

        showSeekBackward(false)
        showSeekForward(false)

        init()

        val isLocal = intent.getBooleanExtra("isLocal", false)
        if (isLocal) {
            movieSrc = intent.getStringExtra("src").orEmpty()
            initPlayer()
            return
        }

        vm.isRegionAllowed.observe(this) {
            if (it.countryCode != "GE") {
                movieSrc = playInstead
                binding.vnaiyc.isVisible = true
                initPlayer()
            } else {
                vm.fileUrl.observe(this) { url ->
                    movieSrc = url ?: ""

                    if (movieSrc != "") {
                        initPlayer()
                    } else {
                        Snackbar.make(
                            binding.root,
                            "ფილმი არ არის გახმოვანებული.",
                            Snackbar.LENGTH_INDEFINITE
                        )
                            .setAction("გასვლა") { finish() }.setTextColor(Color.WHITE).show()
                    }
                }
            }
        }

        binding.movieView.findViewById<ImageView>(R.id.next_episode).setOnClickListener {
            getNextEpisodeIfExists()
        }

        binding.movieView.findViewById<ImageView>(R.id.prev_episode).setOnClickListener {
            getPreviousEpisodeIfExists()
        }
    }

    private fun init() {
        subtitlesSrc = intent.getStringExtra("subtitle") ?: ""
        defaultLanguage = intent.getStringExtra("def_lang") ?: "ENG"
        defaultQuality = intent.getStringExtra("def_quality") ?: "HIGH"
        movie.id = intent.getIntExtra("id", 0)
        movie.season = intent.getIntExtra("s", 0)
        movie.episode = intent.getIntExtra("e", 0)
        series = intent.extras?.getSerializable("files") as EpisodeFiles?
        type = intent.getIntExtra("type", 1)
        fileId = intent.getLongExtra("file_id", 0L)

        vm.getFileUrl(movie.id, 0, fileId)

        binding.movieView.findViewById<ImageView>(R.id.player_exit)
            .setOnClickListener { finish() }

        if (series != null) {
            binding.movieView.findViewById<ImageView>(R.id.next_episode).apply {
                isVisible = series!!.data.size > 1
                isEnabled = series!!.data.size > 1
            }

            binding.movieView.findViewById<ImageView>(R.id.prev_episode).apply {
                isVisible = series!!.data.size > 1
                isEnabled = series!!.data.size > 1
            }
        }

        lifecycleScope.launchWhenStarted {
            val state = withContext(Dispatchers.IO) { vm.loadState(this@WatchActivity, movie.id) }
            if (state != null) {
                movie.time = if (state.season == movie.season && state.episode == movie.episode) {
                    state.time
                } else {
                    0
                }
            }
        }

        binding.movieView.findViewById<TextView>(R.id.movie_title).text = intent.extras?.getString(
            "movie_title",
            "Loading..."
        )

        lockButton = binding.movieView.findViewById(R.id.lock_screen)

        lockButton.setOnClickListener {
            val resizeMode = binding.movieView.resizeMode
            binding.movieView.resizeMode = if (resizeMode == RESIZE_MODE_FIT)
                RESIZE_MODE_ZOOM else RESIZE_MODE_FIT
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initPlayer() {
        exoPlayer = SimpleExoPlayer.Builder(this).build()

        if (subtitlesSrc != "")
            loadSubtitles()
        else loadNormalMovie()

        subtitlesListener()
        binding.movieView.subtitleView?.visible(false)

        if (movie.time != null) {
            val savedState = runBlocking { vm.loadState(this@WatchActivity, movie.id) }
            if (savedState?.time != null) {
                if (savedState.season == movie.season && savedState.episode == movie.episode + 1)
                    exoPlayer.seekTo(savedState.time!!)
            }
        }

        exoPlayer.addListener(this)
        positionControls()
        binding.movieView.player = exoPlayer
        binding.movieView.hideController()

        val seekBySeconds = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val seconds = seekBySeconds.getLong("seek_interval", 5000L)
        binding.movieView.setOnTouchListener(object : View.OnTouchListener {
            private val screenWidth = windowManager.defaultDisplay.width
            private val detector = GestureDetector(
                this@WatchActivity,
                object : GestureDetector.SimpleOnGestureListener() {
                    @SuppressLint("SetTextI18n")
                    override fun onDoubleTap(e: MotionEvent?): Boolean {
                        if (e!!.x < screenWidth * 0.5) {
                            showSeekBackward(true)
                            exoPlayer.seekTo(exoPlayer.currentPosition - seconds)
                            binding.movieView.hideController()
                        } else {
                            showSeekForward(true)
                            exoPlayer.seekTo(exoPlayer.currentPosition + seconds)
                            binding.movieView.hideController()
                        }
                        return true
                    }

                    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                        val isShown = binding.movieView.isControllerVisible
                        binding.movieView.apply {
                            if (isShown)
                                hideController()
                            else
                                showController()
                        }

                        return true
                    }
                })

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                detector.onTouchEvent(event)
                return true
            }
        })
    }

    private fun positionControls() {
        val speed = binding.movieView.findViewById<TextView>(R.id.playback_speed)
        speed.setOnClickListener {
            logger.logSpeedClicked()
            when (speed.text) {
                "1X" -> {
                    speed.text = "1.15X"
                    val params = PlaybackParameters(1.15f)
                    exoPlayer.setPlaybackParameters(params)
                }

                "1.15X" -> {
                    speed.text = "1.25X"
                    val params = PlaybackParameters(1.25f)
                    exoPlayer.setPlaybackParameters(params)
                }

                "1.25X" -> {
                    speed.text = "1.5X"
                    val params = PlaybackParameters(1.5f)
                    exoPlayer.setPlaybackParameters(params)
                }

                "1.5X" -> {
                    speed.text = "1.75X"
                    val params = PlaybackParameters(1.75f)
                    exoPlayer.setPlaybackParameters(params)
                }

                "1.75X" -> {
                    speed.text = "2X"
                    val params = PlaybackParameters(2f)
                    exoPlayer.setPlaybackParameters(params)
                }

                else -> {
                    speed.text = "1X"
                    val params = PlaybackParameters(1f)
                    exoPlayer.setPlaybackParameters(params)
                }
            }
        }
    }

    override fun onPause() {
        if (this::exoPlayer.isInitialized) {
            movie.time = exoPlayer.currentPosition
            vm.saveVideoState(this, movie)

            exoPlayer.pause()
        }
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::exoPlayer.isInitialized)
            exoPlayer.stop()
        _binding = null
    }

    override fun onBackPressed() {
        if (!isScreenLocked && !Const.isTV) {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed()
                finish()
                return
            } else {
                toast(getString(R.string.press_again_to_exit))
            }
            mBackPressed = System.currentTimeMillis()
        } else {
            if (binding.movieView.isControllerVisible) {
                binding.movieView.hideController()
            } else binding.movieView.showController()
        }
    }

    override fun onPlaybackStateChanged(state: Int) {
        super.onPlaybackStateChanged(state)

        when (state) {
            ExoPlayer.STATE_IDLE -> binding.progress.visible(true)
            ExoPlayer.STATE_BUFFERING -> binding.progress.visible(true)
            ExoPlayer.STATE_READY -> {
                exoPlayer.play()
                binding.progress.visible(false)
            }
            ExoPlayer.STATE_ENDED -> {
                binding.progress.visible(false); getNextEpisodeIfExists()
            }
            else -> binding.progress.visible(true)
        }
    }

    private fun showSeekForward(isVisible: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.apply {
                seekIndicator.setImageResource(R.drawable.ic_forward_first)
                if (!isVisible) {
                    seekIndicator.animate()
                        .scaleX(0f)
                        .scaleY(0f)
                        .start()
                } else {
                    seekIndicator.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .start()
                    delay(500)
                    showSeekForward(false)
                }
            }
        }
    }

    private fun showSeekBackward(isVisible: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.apply {
                seekIndicator.setImageResource(R.drawable.ic_backward)
                if (!isVisible) {
                    seekIndicator.animate()
                        .scaleX(0f)
                        .scaleY(0f)
                        .start()
                } else {
                    seekIndicator.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .start()
                    delay(500)
                    showSeekBackward(false)
                }
            }
        }
    }

    private fun loadNormalMovie() {
        val mediaItem = MediaItem.Builder()
            .setUri(movieSrc.toUri())
            .build()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    private fun loadSubtitles() {
        val httpDataSourceFactory = DefaultHttpDataSourceFactory(
            Util.getUserAgent(this, "MoVMob"), null,
            DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
            DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
            true
        )
        val dataSourceFactory = DefaultDataSourceFactory(this, null, httpDataSourceFactory)
        val textFormat = Format.createTextSampleFormat(
            null,
            MimeTypes.TEXT_VTT,
            Format.NO_VALUE,
            null
        )

        val subtitleSource = SingleSampleMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(subtitlesSrc), textFormat, C.TIME_UNSET)
        val mediaSource =
            ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(movieSrc))
        exoPlayer.prepare(MergingMediaSource(mediaSource, subtitleSource))

        binding.movieView.subtitleView?.visible(true)
    }

    private fun subtitlesListener() {
        val subtitlesAvailable = subtitlesSrc != ""
        val btnSubtitles: ImageView = binding.movieView.findViewById(R.id.exo_subtitle)
        val subtitlesOnSrc = ContextCompat.getDrawable(this, R.drawable.ic_subtitles_on)
        val subtitlesOffSrc = ContextCompat.getDrawable(this, R.drawable.ic_subtitles_off)

        if (!subtitlesAvailable)
            btnSubtitles.visible(false)

        btnSubtitles.setOnClickListener {
            val isSubtitlesOn = btnSubtitles.drawable == subtitlesOnSrc

            if (subtitlesAvailable) {
                logger.logSubtitlesClicked()
                if (isSubtitlesOn) {
                    btnSubtitles.setImageDrawable(subtitlesOffSrc)
                    binding.movieView.subtitleView?.visible(false)
                } else {
                    btnSubtitles.setImageDrawable(subtitlesOnSrc)
                    binding.movieView.subtitleView?.visible(true)
                }
            }
        }
    }

    private fun getNextEpisodeIfExists() {
        subscribeOnPause = false
            val season = movie.season
            val episode = movie.episode + 1
            val language = defaultLanguage
            val quality = defaultQuality

                if (series != null) {
                    logger.logNextEpisodeClicked()
                    if (series!!.data.size > 1 && episode < series!!.data.size) {
                        var l1 = true
                        var l2 = true
                        var url = ""
                        var captions: String? = null
                        var fId: Long = 0L
                        //    runOnUiThread { toast("${episode - 2} Ep") }
                        for (i in series!!.data[episode].files) {
                            if (i.lang == language) {
                                l1 = false
                                if (!i.subtitles.isNullOrEmpty()) {
                                    captions = i.subtitles.filter { it.lang == "eng" }[0].url
                                }
                                for (j in i.files) {
                                    if (quality == j.quality) {
                                        l2 = false
                                        url = j.src
                                        fId = j.id
                                    } else url = j.src
                                }
                            } else url = i.files[0].src
                            if (!l1 and !l2)
                                break
                        }

                        val intent = Intent(applicationContext, WatchActivity::class.java)
                        intent.putExtra("s", season)
                        intent.putExtra("e", episode)
                        intent.putExtra("id", movie.id)
                        intent.putExtra("src", url)
                        intent.putExtra("subtitle", captions ?: "")
                        intent.putExtra("files", series)
                        intent.putExtra("file_id", fId)
                        intent.putExtra(
                            "movie_title",
                            "S${season}, E${episode + 1} - ${series!!.data[episode].title}"
                        )
                        startActivity(intent)
                        finish()
                    }
                }
    }

    private fun getPreviousEpisodeIfExists() {
        subscribeOnPause = false
            movie.time = 0
            val season = movie.season
            val episode = movie.episode - 1
            val language = defaultLanguage
            val quality = defaultQuality

                if (series != null) {
                    logger.logPreviousEpisodeClicked()
                    if (series!!.data.isNotEmpty() && episode < series!!.data.size && episode >= 0) {
                        var l1 = true
                        var l2 = true
                        var url = ""
                        var fId: Long = 0L
                        var captions: String? = null
                        for (i in series!!.data[episode - 1].files) {
                            if (i.lang == language) {
                                l1 = false
                                if (!i.subtitles.isNullOrEmpty()) {
                                    captions = i.subtitles.filter { it.lang == "eng" }[0].url
                                }
                                for (j in i.files) {
                                    if (quality == j.quality) {
                                        l2 = false
                                        url = j.src
                                        fId = j.id
                                    } else url = j.src
                                }
                            } else url = i.files[0].src
                            if (!l1 and !l2)
                                break
                        }

                            val intent = Intent(applicationContext, WatchActivity::class.java)
                            intent.putExtra("s", season)
                            intent.putExtra("e", episode)
                            intent.putExtra("id", movie.id)
                            intent.putExtra("src", url)
                            intent.putExtra("subtitle", captions ?: "")
                            intent.putExtra("files", series)
                            intent.putExtra("file_id", fId)
                            intent.putExtra(
                                "movie_title",
                                "S${season}, E${episode} - ${series!!.data[episode - 1].title}"
                            )
                            startActivity(intent)
                            overridePendingTransition(0, 0)

                            finish()
                    }
                }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK ->
                if (Const.isTV) binding.movieView.showController()
        }
        return super.onKeyDown(keyCode, event)
    }
}