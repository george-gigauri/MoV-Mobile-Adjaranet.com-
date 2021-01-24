package ge.mov.mobile.ui.activity.main

import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.ads.InterstitialAd
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.BuildConfig
import ge.mov.mobile.MovApplication
import ge.mov.mobile.R
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.databinding.ActivityMainBinding
import ge.mov.mobile.di.module.AppModule
import ge.mov.mobile.ui.activity.movie.MovieActivity
import ge.mov.mobile.ui.activity.movie.all.AllMoviesActivity
import ge.mov.mobile.ui.activity.other.NoConnectionActivity
import ge.mov.mobile.ui.activity.settings.SettingsActivity
import ge.mov.mobile.ui.activity.setup.ApplicationSetupActivity
import ge.mov.mobile.ui.activity.setup.SetupBirthdayActivity
import ge.mov.mobile.ui.adapter.MovieAdapter
import ge.mov.mobile.ui.adapter.SliderAdapter
import ge.mov.mobile.util.*
import ge.mov.mobile.util.Utils.isFirstUse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.random.Random

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MovieAdapter.OnClickListener {
    lateinit var viewPager: ViewPager
    lateinit var sliderAdapter: SliderAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var timer: Timer
    private val vm: MainActivityViewModel by viewModels()
    private lateinit var ad: InterstitialAd
    private lateinit var languageUtil: LanguageUtil
    private lateinit var reviewManager: ReviewManager
    private lateinit var fakeReviewManager: FakeReviewManager
    private var reviewInfo: ReviewInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageUtil = LanguageUtil(this)
        forwardUser()

        if (Utils.isBirthdayInfoProvided(this))
            if (Utils.isUserAdult(context = this))
                Constants.showAdultContent = true

        setTheme(R.style.AppTheme)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ad = loadAd()

        viewPager = binding.slider

        reviewManager = ReviewManagerFactory.create(this)
        fakeReviewManager = FakeReviewManager(this)

        val externalData = getWebData()
        if (externalData != null) {
            val intent = Intent(applicationContext, MovieActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("id", externalData)
            intent.putExtra("adjaraId", externalData)
            startActivity(intent)
            finish()
            return
        }

        binding.txtMovies.setOnClickListener {
            val intent = Intent(applicationContext, AllMoviesActivity::class.java)
            startActivity(intent)
        }

        binding.txtAllPopularMovies.setOnClickListener {
            val intent = Intent(applicationContext, AllMoviesActivity::class.java)
            intent.putExtra("type", "popular")
            startActivity(intent)
        }

        binding.txtAllMovies.setOnClickListener {
            val intent = Intent(applicationContext, AllMoviesActivity::class.java)
            startActivity(intent)
        }

        binding.txtAllSeriess.setOnClickListener {
            val intent = Intent(applicationContext, AllMoviesActivity::class.java)
            intent.putExtra("type", "series")
            startActivity(intent)
        }

        binding.btnProfilePic.setOnClickListener {
            val intent = Intent(applicationContext, SettingsActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        binding.btnSearchMovie.setOnClickListener {
            val intent = Intent(applicationContext, SearchActivity::class.java)
            startActivity(intent)
        }

        lifecycleScope.launch {
            val top = withContext(Dispatchers.IO) { vm.getTopMovies() }
            val movies = withContext(Dispatchers.IO) { vm.getMovies() }
            val series = withContext(Dispatchers.IO) { vm.getSeries() }

            withContext(Dispatchers.Main) {
                /*   if (!genres?.data.isNullOrEmpty())
                       binding.categories.adapter = GenreAdapter(genres!!.data, this@MainActivity, 1) */

                if (!top?.data.isNullOrEmpty()) {
                    binding.topMovies.adapter = if (!Constants.showAdultContent) {
                        MovieAdapter(
                            this@MainActivity,
                            (top?.data as ArrayList<Data>).filter { m -> !m.adult },
                            listener = this@MainActivity
                        )
                    } else {
                        MovieAdapter(
                            this@MainActivity,
                            top?.data as ArrayList<Data>,
                            listener = this@MainActivity
                        )
                    }
                }

                if (!movies.isNullOrEmpty()) {
                    binding.movies.adapter = if (!Constants.showAdultContent) {
                        MovieAdapter(
                            this@MainActivity,
                            (movies as ArrayList<Data>).filter { m -> !m.adult },
                            listener = this@MainActivity
                        )
                    } else {
                        MovieAdapter(
                            this@MainActivity,
                            movies as ArrayList<Data>,
                            listener = this@MainActivity
                        )
                    }
                }

                if (!series.isNullOrEmpty()) {
                    binding.series.adapter = if (!Constants.showAdultContent) {
                        MovieAdapter(
                            this@MainActivity,
                            (series as ArrayList<Data>).filter { m -> !m.adult },
                            listener = this@MainActivity
                        )
                    } else {
                        MovieAdapter(
                            this@MainActivity,
                            series as ArrayList<Data>,
                            listener = this@MainActivity
                        )
                    }
                }
            }
        }

        vm.getSlides().observe(this) {
            sliderAdapter = SliderAdapter(this, it)
            binding.slider.pageMargin = 85

            if (!it.isNullOrEmpty())
                binding.slider.adapter = sliderAdapter

            timer = Timer()
            timer.scheduleAtFixedRate(SliderTimerTask(), 3500, 4000)
        }
    }

    inner class SliderTimerTask : TimerTask()
    {
        override fun run() {
            runOnUiThread {
                if (viewPager.currentItem == sliderAdapter.count - 1) {
                    viewPager.setCurrentItem(0, true)
                } else {
                    viewPager.setCurrentItem(viewPager.currentItem + 1, true)
                }
            }
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
            supportFragmentManager.beginTransaction().remove(supportFragmentManager.fragments[0])
        }
    }

    override fun onResume() {
        super.onResume()

        checkUpdate()
    }

    override fun onStart() {
        super.onStart()

        if (!isFirstUse(this))
            reviewPrompt()
    }

    private fun getWebData(): Long? {
        val uri = intent.data
        val path = uri?.path
        val id: Long
        if (path != null) {
            if (path.contains("movies")) {
                return try {
                    val tempUrl = path
                    val s = tempUrl.split('/') as ArrayList<String>
                    s.removeAt(0)
                    id = s[1].toLong()
                    id
                } catch (e: Exception) {
                    Log.i("SplashActivity", e.message.toString())
                    null
                }
            }
        }
        return null
    }

    private fun forwardUser() {
        if (!NetworkUtils.isNetworkConnected(this)) {
            val intent = Intent(applicationContext, NoConnectionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else if (Utils.isFirstUse(this)) {
            val intent = Intent(applicationContext, ApplicationSetupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else if (!Utils.isBirthdayInfoProvided(this)) { // Check if user has provided an age
            val intent = Intent(applicationContext, SetupBirthdayActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun checkUpdate() {
        lifecycleScope.launch {
            val currentBuildCode = BuildConfig.VERSION_CODE
            withContext(Dispatchers.IO) {
                val retrofit = withContext(Dispatchers.IO) { AppModule.getRemoteRetrofit() }
                val config = withContext(Dispatchers.IO) { AppModule.getRemoteApi(retrofit) }
                val response = config.getRemoteSettings().body() ?: return@withContext

                if (currentBuildCode < response.remoteVersionCode)
                    notifyUpdate()
            }
        }.start()
    }

    private suspend fun notifyUpdate() {
        withContext(Dispatchers.Main) {
            AlertDialog.Builder(this@MainActivity)
                .setTitle(getString(R.string.update_available))
                .setMessage(getString(R.string.update_available_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.update_now)) { _, _ ->
                    val url = "https://play.google.com/store/apps/details?id=ge.mov.mobile"
                    val intent = Intent(ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                }.create().show()
        }
    }

    private fun reviewPrompt() {
        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
                val request = reviewManager.requestReviewFlow()
                request.addOnCompleteListener { req ->
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            if (req.isSuccessful) {
                                // We got the ReviewInfo object
                                reviewInfo = req.result
                                val flow =
                                    reviewManager.launchReviewFlow(this@MainActivity, reviewInfo!!)
                                flow.addOnCompleteListener {}
                            } else {
                                // There was some problem, continue regardless of the result.
                                toast("Some problem..." + req.exception?.message)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onItemClicked(item: Data) {
        val intent = Intent(applicationContext, MovieActivity::class.java)
        intent.putExtra("id", item.id)
        intent.putExtra("adjaraId", item.adjaraId)
        startActivity(intent)

        if (Random.nextBoolean() && ad.isLoaded)
            ad.show()
    }
}