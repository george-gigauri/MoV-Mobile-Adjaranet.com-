package ge.mov.mobile.ui.activity

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import coil.request.CachePolicy
import ge.mov.mobile.R
import ge.mov.mobile.adapter.MovieAdapter
import ge.mov.mobile.databinding.ActivityPersonMoviesBinding
import ge.mov.mobile.service.APIService
import ge.mov.mobile.ui.viewmodel.PersonMovieViewModel
import ge.mov.mobile.util.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PersonMoviesActivity : AppCompatActivity() {
    private lateinit var dataBinding: ActivityPersonMoviesBinding
    private lateinit var viewModel: PersonMovieViewModel
    private var actorId: Long = 0L

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
        viewModel.getMoviesByActor(this.actorId).observe(this, {
            dataBinding.moviesList.adapter = MovieAdapter(applicationContext, it.data)
            dataBinding.moviesList.post {
                dataBinding.progressBar.visible(false)
            }
        })
    }

    private fun init() {
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_person_movies)
        dataBinding.lifecycleOwner = this
        viewModel = ViewModelProvider(this)[PersonMovieViewModel::class.java]

        actorId = intent?.extras?.getLong("actorId")!!
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
}