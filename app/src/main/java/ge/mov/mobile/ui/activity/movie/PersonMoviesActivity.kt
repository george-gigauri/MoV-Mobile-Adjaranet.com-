package ge.mov.mobile.ui.activity.movie

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import coil.request.CachePolicy
import com.google.android.gms.ads.interstitial.InterstitialAd
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.R
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.databinding.ActivityPersonMoviesBinding
import ge.mov.mobile.ui.adapter.MovieAdapter
import ge.mov.mobile.util.Constants
import ge.mov.mobile.util.loadAd
import ge.mov.mobile.util.visible
import kotlin.random.Random

@AndroidEntryPoint
class PersonMoviesActivity : AppCompatActivity(), MovieAdapter.OnClickListener {
    private lateinit var dataBinding: ActivityPersonMoviesBinding
    private lateinit var viewModel: PersonMovieViewModel
    private var actorId: Long = 0L
    private lateinit var ad: com.google.android.gms.ads.InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        initGridLayoutManager()
        loadActor()
        loadMovies()

        dataBinding.goback.setOnClickListener {
            finish()
        }
    }

    private fun loadActor() {
        val name = intent?.extras?.getString("actorName")
        val img = intent?.extras?.getString("actorImage")

        if (name != null)
            dataBinding.actorName.text = name
        if (img != null)
            dataBinding.actorImage.load(img) { memoryCachePolicy(CachePolicy.DISABLED); diskCachePolicy(CachePolicy.DISABLED) }
    }

    private fun loadMovies() {
        dataBinding.progressBar.visible(true)
        val it = viewModel.getMoviesByActor(this.actorId)

        dataBinding.moviesList.adapter =  if(!Constants.showAdultContent) {
            MovieAdapter(applicationContext, (it as ArrayList<Data>).filter { m -> !m.adult }, listener = this)
        } else {
            MovieAdapter(applicationContext, it as ArrayList<Data>, listener = this)
        }
        dataBinding.moviesList.post {
            dataBinding.progressBar.visible(false)
        }
    }

    private fun init() {
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_person_movies)
        dataBinding.lifecycleOwner = this
        viewModel = ViewModelProvider(this)[PersonMovieViewModel::class.java]

        actorId = intent?.extras?.getLong("actorId")!!

        ad = loadAd()
    }

    private fun initGridLayoutManager() {
        val orientation = resources.configuration.orientation
        val gridLayoutManager = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager(applicationContext, 4)
        } else {
            GridLayoutManager(applicationContext, 2)
        }
        dataBinding.moviesList.layoutManager = gridLayoutManager
    }

    override fun onBackPressed() {
        super.onBackPressed()

        finish()
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