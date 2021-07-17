package ge.mov.mobile.ui.tv

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.databinding.ActivityMainTvBinding
import ge.mov.mobile.extension.toast
import ge.mov.mobile.ui.activity.BaseActivity
import ge.mov.mobile.ui.adapter.MovieAdapter
import ge.mov.mobile.viewmodel.MainViewModel
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainTvActivity : BaseActivity<ActivityMainTvBinding>(), MovieAdapter.OnClickListener {

    override val bindingFactory: (LayoutInflater) -> ActivityMainTvBinding
        get() = { ActivityMainTvBinding.inflate(it) }

    private val vm: MainViewModel by viewModels()

    override fun setup(savedInstanceState: Bundle?) {
        // lifecycleScope.launch {
        //val top = withContext(Dispatchers.IO) { vm.getTopMovies() }
       // val movies = runBlocking { vm.getMovies() }
        //val series = withContext(Dispatchers.IO) { vm.getSeries() }

        //      binding.rvTopMoviesTv.adapter =
        //          MovieAdapter(this@MainTvActivity, top!!.data, 1, this@MainTvActivity)

       // binding.rvMoviesTv.adapter =
        //    MovieAdapter(this@MainTvActivity, movies!!, 1, this@MainTvActivity)

        //    binding.rvSeriesTv.adapter =
        //       MovieAdapter(this@MainTvActivity, series!!, 1, this@MainTvActivity)
        //      }
    }

    override fun onItemClicked(item: Data) {
        toast("Clicked: " + item.originalName)
    }
}