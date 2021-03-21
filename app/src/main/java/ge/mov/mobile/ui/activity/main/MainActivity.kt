package ge.mov.mobile.ui.activity.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.ads.InterstitialAd
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.data.model.featured.FeaturedModel
import ge.mov.mobile.databinding.ActivityMainBinding
import ge.mov.mobile.ui.activity.base.BaseActivity
import ge.mov.mobile.ui.activity.movie.MovieActivity
import ge.mov.mobile.ui.activity.movie.all.AllMoviesActivity
import ge.mov.mobile.ui.activity.settings.SettingsActivity
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
class MainActivity : BaseActivity<ActivityMainBinding>(), MovieAdapter.OnClickListener,
    SliderAdapter.OnClickListener {

    lateinit var viewPager: ViewPager
    lateinit var sliderAdapter: SliderAdapter
    private lateinit var timer: Timer
    private val vm: MainActivityViewModel by viewModels()
    private lateinit var ad: InterstitialAd
    private lateinit var reviewManager: ReviewManager
    private var reviewInfo: ReviewInfo? = null

    override val bindingFactory: (LayoutInflater) -> ActivityMainBinding
        get() = { ActivityMainBinding.inflate(it) }

    override fun setup(savedInstanceState: Bundle?) {
        ad = loadAd()

        viewPager = binding.slider

        //reviewManager = ReviewManagerFactory.create(this)

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

        lifecycleScope.launchWhenStarted {
            withContext(Dispatchers.Main) { binding.progress.visible(true) }
            val slides = withContext(Dispatchers.Main) { vm.getSlides() }
            val top = withContext(Dispatchers.IO) { vm.getTopMovies() }
            val movies = withContext(Dispatchers.IO) { vm.getMovies() }
            val series = withContext(Dispatchers.IO) { vm.getSeries() }

            withContext(Dispatchers.Main) {

                if (!slides.isNullOrEmpty()) {
                    sliderAdapter = SliderAdapter(this@MainActivity, slides, this@MainActivity)
                    binding.slider.pageMargin = 85

                    if (!slides.isNullOrEmpty())
                        binding.slider.adapter = sliderAdapter

                    timer = Timer()
                    timer.scheduleAtFixedRate(SliderTimerTask(), 3500, 4000)
                }

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

                binding.series.post { binding.progress.visible(false) }
            }
        }

    //    if (!isFirstUse(this))
      //      reviewPrompt()
    }

    inner class SliderTimerTask : TimerTask()
    {
        override fun run() {
            runOnUiThread {
                if (viewPager.currentItem == sliderAdapter.count - 1)
                    viewPager.setCurrentItem(0, true)
                else viewPager.setCurrentItem(viewPager.currentItem + 1, true)
            }
        }
    }

    private fun reviewPrompt() {
        lifecycleScope.launchWhenStarted {
            withContext(Dispatchers.IO) {
                val request = reviewManager.requestReviewFlow()
                request.addOnCompleteListener { req ->
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            if (req.isSuccessful) {
                                // We got the ReviewInfo object
                                reviewInfo = req.result
                                reviewManager.launchReviewFlow(this@MainActivity, reviewInfo!!)
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

        if (Random.nextBoolean() && ad.isLoaded && Random.nextBoolean())
            ad.show()
    }

    override fun onSlideClick(item: FeaturedModel) {
        val intent = Intent(applicationContext, MovieActivity::class.java)
        intent.putExtra("id", item.id)
        intent.putExtra("adjaraId", item.adjaraId)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}