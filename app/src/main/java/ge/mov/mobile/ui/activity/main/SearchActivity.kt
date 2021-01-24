package ge.mov.mobile.ui.activity.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.InterstitialAd
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.R
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.databinding.ActivitySearchBinding
import ge.mov.mobile.paging.search.SearchMoviePagingAdapter
import ge.mov.mobile.ui.activity.movie.MovieActivity
import ge.mov.mobile.util.loadAd
import kotlin.random.Random

@AndroidEntryPoint
class SearchActivity : AppCompatActivity(), SearchMoviePagingAdapter.ItemClickListener {
    private val vm: FragmentSearchViewModel by viewModels()
    private var _binding: ActivitySearchBinding? = null
    private val binding: ActivitySearchBinding
        get() = _binding!!

    private lateinit var adapter: SearchMoviePagingAdapter
    private lateinit var ad: InterstitialAd

    private lateinit var gridLayoutManager: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadConfig(resources.configuration)
        ad = loadAd()

        adapter = SearchMoviePagingAdapter(this)
        binding.searchResultsRv.adapter = adapter
        vm.result.observe(this) {
            adapter.submitData(lifecycle, it)
            adapter.notifyDataSetChanged()
        }

        binding.inputSearchKeyword.addTextChangedListener {
            val query = it.toString()
            if (query.isNotEmpty())
                vm.search(query)
        }

        binding.goBack.setOnClickListener { onBackPressed() }
    }

    private fun loadConfig(configuration: Configuration) {
        val orientation = configuration.orientation
        gridLayoutManager = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager(applicationContext, 4)
        } else {
            GridLayoutManager(applicationContext, 2)
        }
        binding.searchResultsRv.layoutManager = gridLayoutManager
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        loadConfig(newConfig)
        super.onConfigurationChanged(newConfig)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun onItemClick(item: Data) {
        val intent = Intent(applicationContext, MovieActivity::class.java)
        intent.putExtra("id", item.id)
        intent.putExtra("adjaraId", item.adjaraId)
        startActivity(intent)

        if (ad.isLoaded && Random.nextBoolean())
            ad.show()
    }
}