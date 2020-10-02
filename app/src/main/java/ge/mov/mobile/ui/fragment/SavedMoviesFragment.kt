package ge.mov.mobile.ui.fragment

import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import ge.mov.mobile.R
import ge.mov.mobile.adapter.SavedMoviesAdapter
import ge.mov.mobile.database.MovieEntity
import ge.mov.mobile.databinding.FragmentSavedMoviesBinding
import ge.mov.mobile.ui.activity.SettingsActivity
import ge.mov.mobile.ui.viewmodel.FragmentSavedMoviesViewModel

class SavedMoviesFragment : AppCompatActivity() {
    private lateinit var binding: FragmentSavedMoviesBinding
    private lateinit var vm: FragmentSavedMoviesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_saved_movies)
        binding.lifecycleOwner = this
        vm = ViewModelProviders.of(this).get(FragmentSavedMoviesViewModel::class.java)

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
    }

    private fun loadData() {
        binding.savedMoviesRefresher.isRefreshing = true
        vm.getAllSavedMovies(applicationContext).observe(this, Observer {
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