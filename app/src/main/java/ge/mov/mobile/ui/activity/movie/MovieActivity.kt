package ge.mov.mobile.ui.activity.movie

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import coil.load
import coil.request.CachePolicy
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.MovApplication.Companion.language
import ge.mov.mobile.R
import ge.mov.mobile.data.database.AppDatabase
import ge.mov.mobile.data.database.MovieDao
import ge.mov.mobile.data.database.MovieEntity
import ge.mov.mobile.databinding.ActivityMovieBinding
import ge.mov.mobile.ui.activity.base.BaseActivity
import ge.mov.mobile.ui.activity.dialog.showMovieDialog
import ge.mov.mobile.ui.adapter.GenreAdapter
import ge.mov.mobile.ui.adapter.MoviePagerAdapter
import ge.mov.mobile.ui.fragment.movie.bottom.BottomFragment
import ge.mov.mobile.util.*
import kotlinx.android.synthetic.main.activity_movie.*
import kotlinx.coroutines.*
import java.util.*

@AndroidEntryPoint
class MovieActivity : BaseActivity<ActivityMovieBinding>() {
    private val vm: MovieDetailViewModel by viewModels()
    private lateinit var db: MovieDao
    private var id: Int? = null
    private var adjaraId: Int? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var ad: InterstitialAd

    private val SHARE_MOVIE_REQUEST_CODE = 240

    override val bindingFactory: (LayoutInflater) -> ActivityMovieBinding
        get() = { ActivityMovieBinding.inflate(it) }

    override fun setup(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)

        ad = loadAd()

        init()
        loadInfo()

        binding.goBack.setOnClickListener { finish() }
    }

    private fun init() {
        id = intent?.extras?.getInt("id", 0)
        adjaraId = intent?.extras?.getInt("adjaraId", 0)

        GlobalScope.launch(Dispatchers.IO) {
            db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "saved_movies"
            ).build().movieDao()
        }
    }

    private fun loadInfo() {
        binding.progress.visible(true)
        lifecycleScope.launchWhenStarted {
            val it = withContext(Dispatchers.Main) {
                val tmp = vm.getMovieDetails(id!!)
                adjaraId = tmp?.adjaraId
                return@withContext tmp
            }

            var saved: Boolean = withContext(Dispatchers.IO) {
                try {
                    vm.isMovieSaved(
                        applicationContext,
                        it!!.id
                    )
                } catch (e: Exception) {
                    false
                }
            }

            if (it != null) {
                runOnUiThread {
                    if (!Constants.showAdultContent && it.adult) {
                        toast("You don't have permission to watch this movie.")
                        finish()
                    }

                    val cover: String = when {
                        it.covers.data._1920 != "" -> it.covers.data._1920
                        it.covers.data._1050 != "" -> it.covers.data._510
                        it.covers.data._367 != "" -> it.covers.data._367
                        else -> it.covers.data._145
                    }

                    binding.movieName.text = it.getNameByLanguage(language?.code)
                    binding.description.text = it.getDescriptionByLanguage(language?.code)

                    binding.poster.load(cover) {
                        placeholder(R.color.colorAccent)
                        error(Constants.getErrorImage())
                        diskCachePolicy(CachePolicy.DISABLED)
                        memoryCachePolicy(CachePolicy.DISABLED)
                    }

                    binding.genresRv.adapter =
                        GenreAdapter(it.genres.data, this@MovieActivity, 2)

                    if (saved) {
                        binding.saveButton.setMinAndMaxProgress(0.0f, 1.0f)
                        binding.saveButton.playAnimation()
                    } else {
                        binding.saveButton.setMinAndMaxProgress(0.0f, 0.0f)
                        binding.saveButton.playAnimation()
                    }

                    binding.saveButton.setOnClickListener { _ ->
                        lifecycleScope.launch {
                            saved = withContext(Dispatchers.IO) {
                                vm.isMovieSaved(applicationContext, it.id)
                            }

                            val movie = MovieEntity(id = it.id, adjaraId = it.adjaraId)

                            if (saved)
                                unsaveMovie(movie)
                            else
                                saveMovie(movie)
                        }
                    }

                    binding.playButton.setOnClickListener { _ ->
                        val popupStyle = sharedPreferences.getInt("popup_style", 1)
                        if (popupStyle == 0) {
                            showMovieDialog(this@MovieActivity, it.id)
                        } else {
                            val bottomFragment = BottomFragment()
                            val bundle = Bundle()
                            bundle.putInt("id", it.id)
                            bottomFragment.arguments = bundle
                            bottomFragment.show(supportFragmentManager, null)
                        }
                    }

                    binding.share.setOnClickListener { _ ->
                        binding.progress.visible(true)
                        val url =
                            "https://www.adjaranet.com/movies/${it.adjaraId}/${it.secondaryName}"
                        shareUrl(it.secondaryName, url)
                    }

                    // ViewPager2 Setup
                    binding.tabPager.adapter = MoviePagerAdapter(
                        applicationContext,
                        supportFragmentManager,
                        lifecycle,
                        it.id,
                        it.adjaraId,
                        ad
                    )
                    val titles =
                        arrayListOf(getString(R.string.similar_movies), getString(R.string.cast))
                    TabLayoutMediator(
                        binding.tabRecommendedMovies,
                        binding.tabPager
                    ) { tab, position ->
                        tab.text = titles[position]
                        binding.tabPager.currentItem = tab.position
                    }.attach()

                    binding.progress.visible(false)
                }
            }
        }
    }

    private fun unsaveMovie(movie: MovieEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            vm.deleteFromDatabase(this@MovieActivity, movie)
        }

        binding.saveButton.setMinAndMaxProgress(0.0f, 0.0f)
        binding.saveButton.playAnimation()
    }

    private fun saveMovie(movie: MovieEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            vm.insertMovieToDatabase(this@MovieActivity, movie)
        }

        binding.saveButton.setMinAndMaxProgress(0.0f, 1.0f)
        binding.saveButton.playAnimation()
    }

    private fun shareUrl(title: String, url: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type="text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Shared via MoV Mobile app: $url\n\nDownload MoV Mobile on your android: https://play.google.com/store/apps/details?id=ge.mov.mobile")
        val chooser = Intent.createChooser(shareIntent, title)
        startActivityForResult(chooser, SHARE_MOVIE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SHARE_MOVIE_REQUEST_CODE) {
            if (resultCode == RESULT_OK)
                binding.progress.visible(false)
            else
                binding.progress.visible(false)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}