package ge.mov.mobile.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bumptech.glide.Glide
import ge.mov.mobile.R
import ge.mov.mobile.adapter.GenreAdapter
import ge.mov.mobile.adapter.PersonAdapter
import ge.mov.mobile.database.AppDatabase
import ge.mov.mobile.database.MovieDao
import ge.mov.mobile.database.MovieEntity
import ge.mov.mobile.databinding.ActivityMovieBinding
import ge.mov.mobile.ui.activity.dialog.showMovieDialog
import ge.mov.mobile.ui.viewmodel.MovieDetailViewModel
import ge.mov.mobile.util.Constants
import ge.mov.mobile.util.toast
import kotlinx.coroutines.*
import kotlin.properties.Delegates

class MovieActivity : AppCompatActivity() {
    private lateinit var dataBinding: ActivityMovieBinding
    private lateinit var vm: MovieDetailViewModel
    private lateinit var db: MovieDao
    private var id: Long? = null
    private var adjaraId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie)
        dataBinding.lifecycleOwner = this

        init()
        loadInfo()

        dataBinding.goBack.setOnClickListener {
            finish()
        }
    }

    private fun init() {
        id = intent?.getLongExtra("id", 0)
        adjaraId = intent?.getLongExtra("adjaraId", 0)

        vm = ViewModelProviders.of(this).get(MovieDetailViewModel::class.java)
        dataBinding.details = vm

        db = Room.databaseBuilder (
            this,
            AppDatabase::class.java,
            "saved_movies"
        ).build().movieDao()

        val gridLayoutManager = GridLayoutManager(this, 2)
        dataBinding.castRv.layoutManager = gridLayoutManager
    }

    private fun loadInfo() {
        vm.getMovieDetails(this, id!!).observe(this, Observer {
            if (it != null)
            {
                var isSaved = vm.isMovieSaved(applicationContext, it.id)

                val cover: String = when {
                    it.covers.data._1920 != "" -> it.covers.data._1920
                    it.covers.data._1050 != "" -> it.covers.data._510
                    it.covers.data._367 != "" -> it.covers.data._367
                    else -> it.covers.data._145
                }

                Glide.with(this)
                    .asDrawable()
                    .load(cover)
                    .into(dataBinding.poster)

                dataBinding.genresRv.adapter = GenreAdapter(it.genres.data, this, 2)

                if (isSaved) {
                    dataBinding.saveButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_heart_filled))
                } else {
                    dataBinding.saveButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_heart_outline))
                }

                dataBinding.playButton.setOnClickListener { i ->
                    showMovieDialog(this, it.id)
                }

                dataBinding.saveButton.setOnClickListener {v ->
                    isSaved = vm.isMovieSaved(applicationContext, it.id)
                    val movie = MovieEntity(id = it.id, adjaraId = it.adjaraId)

                    if (isSaved)
                        unsaveMovie(movie)
                    else
                        saveMovie(movie)
                }
            }
        })

        vm.getCast(id!!).observe(this, Observer {
            if (it != null)
                if (!it.data.isNullOrEmpty())
                    dataBinding.castRv.adapter = PersonAdapter(it.data, this)
        })
    }

    private fun unsaveMovie(movie: MovieEntity) {
        vm.deleteFromDatabase(this, movie)

        dataBinding.saveButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_heart_outline))
        toast("Movie removed from saved list.")
    }

    private fun saveMovie(movie: MovieEntity) {
        vm.insertMovieToDatabase(this, movie)

        dataBinding.saveButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_heart_filled))
        toast("Movie has been saved for later.")
    }

    override fun onResume() {
        super.onResume()

        Constants.current_movie_left_at = 0
    }
}