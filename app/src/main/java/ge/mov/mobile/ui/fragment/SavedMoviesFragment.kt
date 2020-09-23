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

        binding.savedMoviesRefresher.isRefreshing = true
        vm.getAllSavedMovies(this).observe(this, Observer {
            binding.savedMoviesRv.adapter = SavedMoviesAdapter(this, it)
            binding.savedMoviesRv.post { binding.savedMoviesRefresher.isRefreshing = false }
        })

        binding.savedMoviesRefresher.setOnRefreshListener {
            if (binding.savedMoviesRefresher.isRefreshing)
                binding.savedMoviesRefresher.isRefreshing = false
        }
    }
  /*  override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_saved_movies, container, false)
        binding.lifecycleOwner = this
        vm = ViewModelProviders.of(this).get(FragmentSavedMoviesViewModel::class.java)

        init()
        binding.goBack.setOnClickListener {
            (activity!! as SettingsActivity).supportFragmentManager.popBackStack()
        }

        binding.savedMoviesRefresher.isRefreshing = true
        vm.getAllSavedMovies(activity!!).observe(this, Observer {
            binding.savedMoviesRv.adapter = SavedMoviesAdapter(activity!!, it)
            binding.savedMoviesRv.post { binding.savedMoviesRefresher.isRefreshing = false }
        })

        binding.savedMoviesRefresher.setOnRefreshListener {
            if (binding.savedMoviesRefresher.isRefreshing)
                binding.savedMoviesRefresher.isRefreshing = false
        }

        return binding.root
    } */

    private fun init() {
        val gridLayoutManager = GridLayoutManager(this, 2)
        binding.savedMoviesRv.layoutManager = gridLayoutManager
    }
}