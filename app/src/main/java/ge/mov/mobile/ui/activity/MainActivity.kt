package ge.mov.mobile.ui.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.ads.InterstitialAd
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.data.model.featured.FeaturedModel
import ge.mov.mobile.data.model.movie.Genre
import ge.mov.mobile.databinding.ActivityMainBinding
import ge.mov.mobile.extension.loadAd
import ge.mov.mobile.extension.setPreferredColor
import ge.mov.mobile.ui.adapter.*
import ge.mov.mobile.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), MovieAdapter.OnClickListener,
    SliderAdapter.OnClickListener, GenreAdapter.OnGenreClickListener {

    lateinit var viewPager: ViewPager
    lateinit var sliderAdapter: SliderAdapter
    private lateinit var timer: Timer
    private val vm: MainActivityViewModel by viewModels()
    private lateinit var ad: InterstitialAd

    override val bindingFactory: (LayoutInflater) -> ActivityMainBinding
        get() = { ActivityMainBinding.inflate(it) }

    override fun setup(savedInstanceState: Bundle?) {

        checkConnection()

        ad = loadAd()

        viewPager = binding.slider

        setPreferredColor(binding.nestedScrollViewMain)

        forwardUser()

        vm.updateSavedMovies(this)

        binding.ivAvatar.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.rootMain.setOnRefreshListener {
            if (binding.rootMain.isRefreshing) {
                vm.updateSavedMovies(this)
                binding.rootMain.isRefreshing = false
            }
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
            logger.logSearchOpened()
        }

        binding.txtAllSavedMovies.setOnClickListener {
            startActivity(Intent(this, SavedMoviesFragment::class.java))
        }

        vm.savedMovies.observe(this) {
            binding.txtSavedMovies.isVisible = !it.isNullOrEmpty()
            binding.savedMovies.isVisible = !it.isNullOrEmpty()
            binding.txtAllSavedMovies.isVisible = !it.isNullOrEmpty()
            binding.savedMovies.adapter = SavedMoviesAdapter(this, ArrayList(it), this)
        }

        lifecycleScope.launchWhenStarted {
            withContext(Dispatchers.Main) { binding.progress.visible(true) }
            val slides = withContext(Dispatchers.Main) { vm.getSlides() }
            val genres = withContext(Dispatchers.IO) { vm.getGenresFull() }
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

                if (!genres?.data.isNullOrEmpty()) {
                    binding.categories.adapter = GenreAdapter(
                        genres!!.data,
                        this@MainActivity,
                        1,
                        this@MainActivity
                    )
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
    }

    inner class SliderTimerTask : TimerTask() {
        override fun run() {
            runOnUiThread {
                if (viewPager.currentItem == sliderAdapter.count - 1)
                    viewPager.setCurrentItem(0, true)
                else viewPager.setCurrentItem(viewPager.currentItem + 1, true)
            }
        }
    }

    private fun checkConnection() {
        var status = false

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm.activeNetwork != null && cm.getNetworkCapabilities(cm.activeNetwork) != null) {
                // connected to the internet
                status = true
            }
        } else {
            if (cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnectedOrConnecting) {
                // connected to the internet
                status = true
            }
        }

        if (!status) {
            val intent = Intent(applicationContext, DownloadedMoviesActivity::class.java)
            startActivity(intent)
            finish()
            return
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

    override fun onClicked(item: Genre, position: Int) {
        // Open movies with genre
        val intent = Intent(this, AllMoviesActivity::class.java)
        intent.putExtra("genre_title", item.primaryName)
        intent.putExtra("genre_id", item.id)
        startActivity(intent)
    }
}