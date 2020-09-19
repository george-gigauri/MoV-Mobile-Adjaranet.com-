package ge.mov.mobile.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import ge.mov.mobile.R
import ge.mov.mobile.adapter.GenreAdapter
import ge.mov.mobile.adapter.PersonAdapter
import ge.mov.mobile.databinding.ActivityMovieBinding
import ge.mov.mobile.ui.activity.dialog.showMovieDialog
import ge.mov.mobile.ui.viewmodel.MovieDetailViewModel
import ge.mov.mobile.util.Constants
import kotlin.properties.Delegates

class MovieActivity : AppCompatActivity() {
    private var id: Long? = null
    private var adjaraId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataBinding: ActivityMovieBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_movie)
        dataBinding.lifecycleOwner = this

        id = intent?.getLongExtra("id", 0)
        adjaraId = intent?.getLongExtra("adjaraId", 0)

        val vm = ViewModelProviders.of(this).get(MovieDetailViewModel::class.java)
        dataBinding.details = vm

        vm.getMovieDetails(id!!).observe(this, Observer {
            if (it != null)
            {
                val cover: String
                if (it.covers.data._1920 != "")
                    cover = it.covers.data._1920
                else if (it.covers.data._1050 != "")
                    cover = it.covers.data._510
                else if (it.covers.data._367 != "")
                    cover = it.covers.data._367
                else
                    cover = it.covers.data._145

                Glide.with(this)
                    .asDrawable()
                    .load(cover)
                    .into(dataBinding.poster)

                dataBinding.genresRv.adapter = GenreAdapter(it.genres.data, this, 2)

                dataBinding.playButton.setOnClickListener { i ->
                   showMovieDialog(this, it.id)
                }
            }
        })

        val gridLayoutManager = GridLayoutManager(this, 2)
        dataBinding.castRv.layoutManager = gridLayoutManager
        vm.getCast(id!!).observe(this, Observer {
            if (it != null)
                if (!it.data.isNullOrEmpty())
                    dataBinding.castRv.adapter = PersonAdapter(it.data, this)
        })
    }

    override fun onResume() {
        super.onResume()

        Constants.current_movie_left_at = 0
    }
}