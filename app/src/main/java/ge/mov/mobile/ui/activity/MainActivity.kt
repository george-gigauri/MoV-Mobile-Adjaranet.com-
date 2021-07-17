package ge.mov.mobile.ui.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.ads.InterstitialAd
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.data.model.MainActivityDto
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.data.model.featured.FeaturedModel
import ge.mov.mobile.data.model.movie.Genre
import ge.mov.mobile.databinding.ActivityMainBinding
import ge.mov.mobile.extension.loadAd
import ge.mov.mobile.extension.setPreferredColor
import ge.mov.mobile.extension.toast
import ge.mov.mobile.ui.adapter.GenreAdapter
import ge.mov.mobile.ui.adapter.MovieAdapter
import ge.mov.mobile.ui.adapter.SavedMoviesAdapter
import ge.mov.mobile.ui.adapter.SliderAdapter
import ge.mov.mobile.util.State
import ge.mov.mobile.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collect
import java.util.*
import kotlin.random.Random

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), MovieAdapter.OnClickListener,
    SliderAdapter.OnClickListener, GenreAdapter.OnGenreClickListener {

    lateinit var viewPager: ViewPager
    lateinit var sliderAdapter: SliderAdapter
    private lateinit var timer: Timer
    private val vm: MainViewModel by viewModels()
    private lateinit var ad: InterstitialAd

    override val bindingFactory: (LayoutInflater) -> ActivityMainBinding
        get() = { ActivityMainBinding.inflate(it) }

    override fun setup(savedInstanceState: Bundle?) {

        checkConnection()

        ad = loadAd()

        viewPager = binding.slider

        setPreferredColor(binding.nestedScrollViewMain)

        forwardUser()

        setOnClickListeners()
        initObservers()

        binding.rootMain.setOnRefreshListener {
            if (binding.rootMain.isRefreshing) {
                vm.getData()
                binding.rootMain.isRefreshing = false
            }
        }
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            vm.data.collect {
                when (it.status) {
                    State.Status.LOADING -> showProgress()
                    State.Status.EMPTY -> hideProgress()
                    State.Status.SUCCESS -> {
                        val data = (it.data as MainActivityDto)
                        onSuccessLoadData(data)
                        hideProgress()
                    }
                    State.Status.FAILURE -> {
                        hideProgress()
                        toast(it.message ?: "Unknown Error")
                    }
                }
            }
        }
    }

    private fun onSuccessLoadData(data: MainActivityDto) {
        binding.apply {
            slider.adapter = SliderAdapter(this@MainActivity, data.featured, this@MainActivity)
            savedMovies.adapter =
                SavedMoviesAdapter(this@MainActivity, data.saved, this@MainActivity)
            topMovies.adapter = MovieAdapter(this@MainActivity, data.top, 1, this@MainActivity)
            movies.adapter = MovieAdapter(this@MainActivity, data.movies, 1, this@MainActivity)
            series.adapter = MovieAdapter(this@MainActivity, data.series, 1, this@MainActivity)

            txtSavedMovies.isVisible = !data.saved.isNullOrEmpty()
            txtAllSavedMovies.isVisible = !data.saved.isNullOrEmpty()
            savedMovies.isVisible = !data.saved.isNullOrEmpty()
        }
    }

    private fun setOnClickListeners() {
        binding.ivAvatar.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.txtMovies.setOnClickListener {
            startActivity(Intent(this, AllMoviesActivity::class.java))
        }

        binding.txtAllPopularMovies.setOnClickListener {
            val intent = Intent(applicationContext, AllMoviesActivity::class.java)
            intent.putExtra("type", "popular")
            startActivity(intent)
        }

        binding.txtAllMovies.setOnClickListener {
            startActivity(Intent(this, AllMoviesActivity::class.java))
        }

        binding.txtAllSeriess.setOnClickListener {
            val intent = Intent(applicationContext, AllMoviesActivity::class.java)
            intent.putExtra("type", "series")
            startActivity(intent)
        }

        binding.btnProfilePic.setOnClickListener {
            startActivity(Intent(applicationContext, SettingsActivity::class.java))
            finish()
        }

        binding.btnSearchMovie.setOnClickListener {
            startActivity(Intent(applicationContext, SearchActivity::class.java))
            logger.logSearchOpened()
        }

        binding.txtAllSavedMovies.setOnClickListener {
            startActivity(Intent(this, SavedMoviesFragment::class.java))
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