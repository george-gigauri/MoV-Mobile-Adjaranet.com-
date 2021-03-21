package ge.mov.mobile.ui.activity.movie

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.google.android.gms.ads.InterstitialAd
import ge.mov.mobile.R
import ge.mov.mobile.data.database.MovieSubscriptionEntity
import ge.mov.mobile.data.model.Series.EpisodeFiles
import ge.mov.mobile.databinding.ActivityWatchBinding
import ge.mov.mobile.util.*
import kotlinx.android.synthetic.main.fragment_saved_movies.*
import kotlinx.coroutines.*


class WatchActivity : AppCompatActivity(), Player.EventListener {
    private var isScreenLocked = false
    private val TIME_INTERVAL = 2000
    private var mBackPressed: Long = 0

    companion object {
        private var movie: MovieSubscriptionEntity = MovieSubscriptionEntity()
        private lateinit var movieSrc: String
        private lateinit var subtitlesSrc: String
        private var series: EpisodeFiles? = null
        private lateinit var defaultLanguage: String
        private lateinit var defaultQuality: String
    }

    private lateinit var vm: WatchViewModel
    private lateinit var binding: ActivityWatchBinding

    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var lockButton: ImageView
    private lateinit var ad: InterstitialAd

    private var subscribeOnPause = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        FullScreencall()
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_watch)
        vm = ViewModelProvider(this)[WatchViewModel::class.java]

        ad = loadAd()

        val myLang = Utils.loadLanguage(this)
        Utils.saveLanguage(this, myLang)

        showSeekBackward(false)
        showSeekForward(false)

        init()

        if (movieSrc != "") {
            initPlayer()
        } else {
            toast("Error parsing an URL !")
        }

        binding.movieView.findViewById<ImageView>(R.id.next_episode).setOnClickListener {
            getNextEpisodeIfExists()
        }

        binding.movieView.findViewById<ImageView>(R.id.prev_episode).setOnClickListener {
            getPreviousEpisodeIfExists()
        }
    }

    private fun init() {
        movieSrc = intent.getStringExtra("src") ?: ""
        subtitlesSrc = intent.getStringExtra("subtitle") ?: ""
        defaultLanguage = intent.getStringExtra("def_lang") ?: "ENG"
        defaultQuality = intent.getStringExtra("def_quality") ?: "HIGH"
        movie.id = intent.getIntExtra("id", 0)
        movie.season = intent.getIntExtra("s", 0)
        movie.episode = intent.getIntExtra("e", 0)
        series = intent.extras?.getSerializable("files") as EpisodeFiles?

        Log.i(
            "SE State",
            "Current Season: ${movie.season},   Saved Episode:  ${movie.episode}"
        )

        lifecycleScope.launch(Dispatchers.Main) {
            if (series != null && series!!.data.size > 1) {
                binding.movieView.findViewById<ImageView>(R.id.next_episode).apply {
                    setImageDrawable(drawable(R.drawable.exo_ic_skip_next))
                    isEnabled = true
                    isClickable = true
                }

                if (movie.season > 0 && movie.episode > 1) {
                    binding.movieView.findViewById<ImageView>(R.id.prev_episode).apply {
                        setImageDrawable(drawable(R.drawable.exo_ic_previous_enabled))
                        isClickable = true
                        isEnabled = true
                    }
                } else {
                    binding.movieView.findViewById<ImageView>(R.id.prev_episode).apply {
                        setImageDrawable(drawable(R.drawable.exo_ic_previous_disabled))
                        isEnabled = false
                        isClickable = false
                    }
                }
            } else {
                binding.movieView.findViewById<ImageView>(R.id.prev_episode).apply {
                    setImageDrawable(drawable(R.drawable.exo_ic_previous_disabled))
                    isClickable = false
                    isEnabled = false
                }

                binding.movieView.findViewById<ImageView>(R.id.prev_episode).apply {
                    setImageDrawable(drawable(R.drawable.exo_ic_previous_disabled))
                    isClickable = false
                    isEnabled = false
                }
            }
        }

        lifecycleScope.launch {
            val state = withContext(Dispatchers.IO) { vm.loadState(this@WatchActivity, movie.id) }
            if (state != null) {
                Log.i(
                    "SE State",
                    "Saved Season: ${movie.season},   Saved Episode:  ${movie.episode}"
                )
                movie.time = if (state.season == movie.season && state.episode == movie.episode) {
                    state.time
                } else {
                    0
                }
            }
        }

        findViewById<TextView>(R.id.movie_title).text = intent.extras?.getString(
            "movie_title",
            "Loading..."
        )

        lockButton = binding.movieView.findViewById(R.id.lock_screen)

        lockButton.setOnClickListener {
            if (isScreenLocked) {
                isScreenLocked = false
                lockButton.setImageDrawable(drawable(R.drawable.ic_baseline_lock_open_24))
            } else {
                isScreenLocked = true
                lockButton.setImageDrawable(drawable(R.drawable.ic_baseline_lock_24))
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initPlayer() {
        exoPlayer = SimpleExoPlayer.Builder(this).build()
        if (subtitlesSrc != "")
            loadSubtitles()
        else
            loadNormalMovie()
        subtitlesListener()
        binding.movieView.subtitleView?.visible(false)

        if (movie.time != null) {
            val savedState = runBlocking { vm.loadState(this@WatchActivity, movie.id) }
            if (savedState?.time != null)
                if (savedState.season == movie.season && savedState.episode == movie.episode)
                    exoPlayer.seekTo(savedState.time!!)
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
        movie.time = exoPlayer.currentPosition
        vm.saveVideoState(this, movie)

        exoPlayer.pause()

        if (isScreenLocked) {
            val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.moveTaskToFront(taskId, 0)
        }
        super.onPause()
    }

    override fun onDestroy() {
        exoPlayer.stop()
        exoPlayer.release()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

        if (this::exoPlayer.isInitialized) {
            val restored = runBlocking { vm.loadState(this@WatchActivity, movie.id) }
            if (movie.season == restored?.season && movie.episode == restored.episode && restored.time != null)
                exoPlayer.seekTo(restored.time!!)
            exoPlayer.play()
        } else {
            initPlayer()
        }
    }

    override fun onBackPressed() {
        if (!isScreenLocked) {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed()
                finish()
                return
            } else {
                toast(getString(R.string.press_again_to_exit))
            }
            mBackPressed = System.currentTimeMillis()
        } else {
            toast("ეკრანი დაბლოკილია.")
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val myLang = Utils.loadLanguage(this)
        Utils.saveLanguage(this, myLang)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_watch)
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
                seekIndicator.setImageResource(R.drawable.ic_forward)
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
        Log.i(
            "Previous SE",
            "Previous Season: ${movie.season},   Previous Episode:  ${movie.episode}"
        )
        lifecycleScope.launch {
            val season = movie.season
            val episode = movie.episode + 1
            val language = defaultLanguage
            val quality = defaultQuality

            withContext(Dispatchers.IO) {
                if (series != null) {
                    if (series!!.data.size > 1 && episode < series!!.data.size) {
                        var l1 = true
                        var l2 = true
                        var url = ""
                        var captions: String? = null
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
                                    } else url = j.src
                                }
                            } else url = i.files[0].src
                            if (!l1 and !l2)
                                break
                        }

                        withContext(Dispatchers.Main) {
                            val intent = Intent(applicationContext, WatchActivity::class.java)
                            intent.putExtra("s", season)
                            intent.putExtra("e", episode)
                            intent.putExtra("id", movie.id)
                            intent.putExtra("src", url)
                            intent.putExtra("subtitle", captions ?: "")
                            intent.putExtra("files", series)
                            intent.putExtra(
                                "movie_title",
                                "S${season}, E${episode} - ${series!!.data[episode - 1].title}"
                            )
                            startActivity(intent)

                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun getPreviousEpisodeIfExists() {
        subscribeOnPause = false
        lifecycleScope.launch {
            //  withContext(Dispatchers.IO) { DialogHelper.subscribe(this@WatchActivity, movie) }
            //  movie.episode--
            movie.time = 0
            val season = movie.season
            val episode = movie.episode - 1
            val language = defaultLanguage
            val quality = defaultQuality

            withContext(Dispatchers.IO) {
                if (series != null) {
                    if (series!!.data.isNotEmpty() && episode < series!!.data.size && episode >= 0) {
                        var l1 = true
                        var l2 = true
                        var url = ""
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
                                    } else url = j.src
                                }
                            } else url = i.files[0].src
                            if (!l1 and !l2)
                                break
                        }

                        withContext(Dispatchers.Main) {
                            val intent = Intent(applicationContext, WatchActivity::class.java)
                            intent.putExtra("s", season)
                            intent.putExtra("e", episode)
                            intent.putExtra("id", movie.id)
                            intent.putExtra("src", url)
                            intent.putExtra("subtitle", captions ?: "")
                            intent.putExtra("files", series)
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

    override fun onStart() {
        super.onStart()

        FullScreencall()
    }

    private fun FullScreencall() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
}