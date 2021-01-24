package ge.mov.mobile.ui.activity.settings

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.InterstitialAd
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.R
import ge.mov.mobile.ui.adapter.SavedMoviesAdapter
import ge.mov.mobile.data.database.MovieEntity
import ge.mov.mobile.databinding.FragmentSavedMoviesBinding
import ge.mov.mobile.util.loadAd

@AndroidEntryPoint
class SavedMoviesFragment : AppCompatActivity() {
    private lateinit var binding: FragmentSavedMoviesBinding
    private val vm: FragmentSavedMoviesViewModel by viewModels()
    private lateinit var ad: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_saved_movies)

        ad = loadAd()

        init()
        binding.goBack.setOnClickListener {
            finish()
        }

        loadData()

        binding.savedMoviesRefresher.setOnRefreshListener {
            loadData()
        }
    }

    private fun init() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 2)
        binding.savedMoviesRv.layoutManager = gridLayoutManager
        binding.goBack.isSelected = true
    }

    private fun loadData() {
        binding.savedMoviesRefresher.isRefreshing = true
        vm.getAllSavedMovies(applicationContext).observe(this, {
            binding.savedMoviesRv.adapter = SavedMoviesAdapter(applicationContext,
                it as ArrayList<MovieEntity>
            )
            binding.savedMoviesRv.post {
                if (it.isNullOrEmpty()) {
                    binding.noMoviesTxt.visibility = View.VISIBLE
                } else {
                    binding.noMoviesTxt.visibility = View.GONE
                }

                binding.savedMoviesRefresher.isRefreshing = false
            }
        })
    }

    override fun onResume() {
        super.onResume()

        loadData()
    }
}