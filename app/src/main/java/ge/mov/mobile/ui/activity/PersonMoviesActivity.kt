package ge.mov.mobile.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import coil.request.CachePolicy
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.databinding.ActivityPersonMoviesBinding
import ge.mov.mobile.extension.loadAd
import ge.mov.mobile.extension.setPreferredColor
import ge.mov.mobile.viewmodel.PersonMovieViewModel
import ge.mov.mobile.ui.adapter.MovieAdapter
import ge.mov.mobile.util.Constants
import kotlin.random.Random

@AndroidEntryPoint
class PersonMoviesActivity : BaseActivity<ActivityPersonMoviesBinding>(), MovieAdapter.OnClickListener {
    
    private val viewModel: PersonMovieViewModel by viewModels()
    private var actorId: Long = 0L
    private lateinit var ad: com.google.android.gms.ads.InterstitialAd

    override val bindingFactory: (LayoutInflater) -> ActivityPersonMoviesBinding
        get() = { ActivityPersonMoviesBinding.inflate(it) }

    override fun setup(savedInstanceState: Bundle?) {
        init()
        initGridLayoutManager()
        loadActor()
        loadMovies()

        setPreferredColor(binding.root)

        binding.goback.setOnClickListener {
            finish()
        }
    }

    private fun loadActor() {
        val name = intent?.extras?.getString("actorName")
        val img = intent?.extras?.getString("actorImage")

        if (name != null)
            binding.actorName.text = name
        if (img != null)
            binding.actorImage.load(img) { memoryCachePolicy(CachePolicy.DISABLED); diskCachePolicy(CachePolicy.DISABLED) }
    }

    private fun loadMovies() {
        binding.progressBar.visible(true)
        val it = viewModel.getMoviesByActor(this.actorId)

        binding.moviesList.adapter =  if(!Constants.showAdultContent) {
            MovieAdapter(applicationContext, (it as ArrayList<Data>).filter { m -> !m.adult }, listener = this)
        } else {
            MovieAdapter(applicationContext, it as ArrayList<Data>, listener = this)
        }
        binding.moviesList.post {
            binding.progressBar.visible(false)
        }
    }

    private fun init() {
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
        binding.moviesList.layoutManager = gridLayoutManager
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