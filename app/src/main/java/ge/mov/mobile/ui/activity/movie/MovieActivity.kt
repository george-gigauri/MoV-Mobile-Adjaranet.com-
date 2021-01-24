package ge.mov.mobile.ui.activity.movie

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import coil.load
import coil.request.CachePolicy
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.R
import ge.mov.mobile.data.database.AppDatabase
import ge.mov.mobile.data.database.MovieDao
import ge.mov.mobile.data.database.MovieEntity
import ge.mov.mobile.databinding.ActivityMovieBinding
import ge.mov.mobile.ui.activity.dialog.showMovieDialog
import ge.mov.mobile.ui.adapter.GenreAdapter
import ge.mov.mobile.ui.adapter.MoviePagerAdapter
import ge.mov.mobile.ui.fragment.movie.bottom.BottomFragment
import ge.mov.mobile.util.*
import kotlinx.android.synthetic.main.activity_movie.*
import kotlinx.coroutines.*
import java.util.*

@AndroidEntryPoint
class MovieActivity : AppCompatActivity() {
    private lateinit var dataBinding: ActivityMovieBinding
    private val vm: MovieDetailViewModel by viewModels()
    private lateinit var db: MovieDao
    private var id: Int? = null
    private var adjaraId: Int? = null
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var ad: InterstitialAd


    private val SHARE_MOVIE_REQUEST_CODE = 240

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie)
        dataBinding.lifecycleOwner = this

        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)

        ad = loadAd()

        /*   if (!approved) {
               mIntestitialAd = InterstitialAd(this)
               mIntestitialAd.adUnitId = "ca-app-pub-2337439332290274/2854996575"
               mIntestitialAd.loadAd(AdRequest.Builder().build())
           } else {
               toast("User Approved for Non-Ads service.")
           } */

        init()
        loadInfo()

        dataBinding.goBack.setOnClickListener {
//            if (!approved)
//                if (mIntestitialAd.isLoaded)
//                    mIntestitialAd.show()
            finish()
        }
    }

    private fun init() {
        id = intent?.getIntExtra("id", 0)
        adjaraId = intent?.getIntExtra("adjaraId", 0)

        dataBinding.details = vm

        GlobalScope.launch(Dispatchers.IO) {
            db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "saved_movies"
            ).build().movieDao()
        }
    }

    private fun loadInfo() {
        dataBinding.progress.visible(true)
        lifecycleScope.launch {
            val it = withContext(Dispatchers.IO) { vm.getMovieDetails(id!!) }
            var saved: Boolean = withContext(Dispatchers.IO) {
                vm.isMovieSaved(
                    applicationContext,
                    it!!.id
                )
            }

            if (it != null) {
                withContext(Dispatchers.Main) {
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

                    dataBinding.poster.load(cover) {
                        placeholder(R.color.colorAccent)
                        error(Constants.getErrorImage())
                        diskCachePolicy(CachePolicy.DISABLED)
                        memoryCachePolicy(CachePolicy.DISABLED)
                    }

                    dataBinding.genresRv.adapter =
                        GenreAdapter(it.genres.data, this@MovieActivity, 2)

                    if (saved) {
                        dataBinding.saveButton.setMinAndMaxProgress(0.0f, 1.0f)
                        dataBinding.saveButton.playAnimation()
                    } else {
                        dataBinding.saveButton.setMinAndMaxProgress(0.0f, 0.0f)
                        dataBinding.saveButton.playAnimation()
                    }

                    dataBinding.saveButton.setOnClickListener { _ ->
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

                    dataBinding.playButton.setOnClickListener { _ ->
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

                    dataBinding.share.setOnClickListener { _ ->
                        dataBinding.progress.visible(true)
                        val url =
                            "https://www.adjaranet.com/movies/${it.adjaraId}/${it.secondaryName}"
                        shareUrl(it.secondaryName, url)
                    }

                    // ViewPager2 Setup
                    dataBinding.tabPager.adapter = MoviePagerAdapter(
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
                        dataBinding.tabRecommendedMovies,
                        dataBinding.tabPager
                    ) { tab, position ->
                        tab.text = titles[position]
                        dataBinding.tabPager.currentItem = tab.position
                    }.attach()

                    dataBinding.progress.visible(false)
                }
            }
        }
    }

    private fun unsaveMovie(movie: MovieEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            vm.deleteFromDatabase(this@MovieActivity, movie)
        }

        /* dataBinding.saveButton.setImageDrawable(
             ContextCompat.getDrawable(
                 applicationContext,
                 R.drawable.ic_heart_outline
             )
         ) */

        dataBinding.saveButton.setMinAndMaxProgress(0.0f, 0.0f)
        dataBinding.saveButton.playAnimation()

        toast("Movie removed from saved list.")
    }

    private fun saveMovie(movie: MovieEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            vm.insertMovieToDatabase(this@MovieActivity, movie)
        }

        /*  dataBinding.saveButton.setImageDrawable(
              ContextCompat.getDrawable(
                  applicationContext,
                  R.drawable.ic_heart_filled
              )
          ) */

        dataBinding.saveButton.setMinAndMaxProgress(0.0f, 1.0f)
        dataBinding.saveButton.playAnimation()
        toast("Movie has been saved for later.")
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
                dataBinding.progress.visible(false)
            else
                dataBinding.progress.visible(false)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        if (!approved)
//            if (mIntestitialAd.isLoaded)
//                mIntestitialAd.show()
        finish()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val lang = Utils.loadLanguage(this)
        Utils.saveLanguage(this, lang)
        setTheme(R.style.AppTheme)
        newConfig.locale = Locale(lang.id, lang.code)
    }
}