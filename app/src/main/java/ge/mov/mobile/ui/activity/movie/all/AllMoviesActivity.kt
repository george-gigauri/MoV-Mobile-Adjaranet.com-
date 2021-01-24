package ge.mov.mobile.ui.activity.movie.all

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.InterstitialAd
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.R
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.databinding.ActivityAllMoviesBinding
import ge.mov.mobile.paging.movies.MoviePagingAdapter
import ge.mov.mobile.ui.activity.movie.MovieActivity
import ge.mov.mobile.ui.activity.movie.all.filter.FilterBottomFragment
import ge.mov.mobile.util.loadAd
import ge.mov.mobile.util.showAd
import ge.mov.mobile.util.visible
import kotlin.random.Random

@AndroidEntryPoint
class AllMoviesActivity : AppCompatActivity(), MoviePagingAdapter.MovieClickListener,
    FilterBottomFragment.OnFilterListener {
    private var _binding: ActivityAllMoviesBinding? = null
    private val binding: ActivityAllMoviesBinding get() = _binding!!
    private val viewModel: ViewModelAll by viewModels()
    private lateinit var adapter: MoviePagingAdapter
    private lateinit var ad: InterstitialAd
    private var selectedLanguage = "GEO"
    private var yearFrom: Int? = null
    private var yearTo: Int? = null

    private val selectedGenres = ArrayList<Int>()

    companion object {
        private var type: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_all_movies)
        adapter = MoviePagingAdapter(this)

        ad = loadAd()

        type = intent.getStringExtra("type") ?: "movie"
        viewModel.load(type!!)

        (binding.rv.layoutManager as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int = adapter.getItemViewType(position)
        }

        binding.rv.adapter = adapter
        binding.rv.layoutManager = GridLayoutManager(this, 2)
        binding.rv.setHasFixedSize(true)

        layoutSpanCount(resources.configuration)
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        viewModel.result.observe(this) {
            adapter.submitData(lifecycle, it)
            adapter.notifyDataSetChanged()
        }

        binding.rv.post { binding.progress.visible(false) }

        binding.goBack.setOnClickListener {
            finish()
        }

        binding.btnFilters.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("selectedLanguage", selectedLanguage)
            bundle.putString("yearFrom", yearFrom.toString())
            bundle.putString("yearTo", yearTo.toString())

            if (!selectedGenres.isNullOrEmpty()) {
                bundle.putSerializable("selectedGenres", selectedGenres)
            }

            val sheet = FilterBottomFragment()
            sheet.attachListener(this)
            sheet.arguments = bundle
            sheet.show(supportFragmentManager, null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
        type = null
        selectedGenres.clear()
    }

    override fun onItemClick(movie: Data) {
        if (Random.nextBoolean() && ad.isLoaded) {
            showAd(ad)
            ad = loadAd()
        }
        val intent = Intent(applicationContext, MovieActivity::class.java)
        intent.putExtra("id", movie.id)
        intent.putExtra("adjaraId", movie.adjaraId)
        startActivity(intent)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        layoutSpanCount(newConfig)
    }

    private fun layoutSpanCount(config: Configuration) {
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.rv.layoutManager = GridLayoutManager(this, 4)
        } else {
            binding.rv.layoutManager = GridLayoutManager(this, 2)
        }
    }

    override fun onFilterRequested(genres: List<Int>?, languageFilter: String, yearFrom: Int, yearTo: Int) {
        this.selectedLanguage = languageFilter
        this.yearFrom = yearFrom
        this.yearTo = yearTo
        viewModel.load(type!!, genres, languageFilter, yearFrom, yearTo)

        this.selectedGenres.clear()
        if (genres != null) {
            this.selectedGenres.addAll(genres)
        }
    }
}