package ge.mov.mobile.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.InterstitialAd
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.data.database.entity.MovieEntity
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.databinding.FragmentSavedMoviesBinding
import ge.mov.mobile.extension.loadAd
import ge.mov.mobile.extension.setPreferredColor
import ge.mov.mobile.viewmodel.FragmentSavedMoviesViewModel
import ge.mov.mobile.ui.adapter.MovieAdapter
import ge.mov.mobile.ui.adapter.SavedMoviesAdapter

@AndroidEntryPoint
class SavedMoviesFragment : BaseActivity<FragmentSavedMoviesBinding>(),
    MovieAdapter.OnClickListener {

    private val vm: FragmentSavedMoviesViewModel by viewModels()
    private lateinit var ad: InterstitialAd

    override val bindingFactory: (LayoutInflater) -> FragmentSavedMoviesBinding
        get() = { FragmentSavedMoviesBinding.inflate(it) }

    override fun setup(savedInstanceState: Bundle?) {
        ad = loadAd()

        setPreferredColor(binding.root)

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

        vm.getAllSavedMovies(this)
    }

    private fun loadData() {
        binding.savedMoviesRefresher.isRefreshing = true
        vm.movies.observe(this, {
            binding.savedMoviesRv.adapter = SavedMoviesAdapter(
                applicationContext,
                it as ArrayList<MovieEntity>,
                this
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

    override fun onItemClicked(item: Data) {
        val intent = Intent(this, MovieActivity::class.java)
        intent.putExtra("id", item.id)
        intent.putExtra("adjaraId", item.adjaraId)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}