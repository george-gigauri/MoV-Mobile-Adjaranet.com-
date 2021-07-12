package ge.mov.mobile.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.InterstitialAd
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.databinding.ActivityAllMoviesBinding
import ge.mov.mobile.extension.loadAd
import ge.mov.mobile.extension.setPreferredColor
import ge.mov.mobile.extension.showAd
import ge.mov.mobile.extension.visible
import ge.mov.mobile.paging.movies.MoviePagingAdapter
import ge.mov.mobile.viewmodel.ViewModelAll
import ge.mov.mobile.ui.FilterBottomFragment
import kotlin.random.Random

@AndroidEntryPoint
class AllMoviesActivity : BaseActivity<ActivityAllMoviesBinding>(),
    MoviePagingAdapter.MovieClickListener,
    FilterBottomFragment.OnFilterListener {

    private val viewModel: ViewModelAll by viewModels()
    private lateinit var adapter: MoviePagingAdapter
    private lateinit var ad: InterstitialAd
    private var selectedLanguage = "GEO"
    private var yearFrom: Int? = null
    private var yearTo: Int? = null

    private val selectedGenres = ArrayList<Int>()

    override val bindingFactory: (LayoutInflater) -> ActivityAllMoviesBinding
        get() = { ActivityAllMoviesBinding.inflate(it) }

    companion object {
        private var type: String? = null
    }

    override fun setup(savedInstanceState: Bundle?) {
        adapter = MoviePagingAdapter(this)

        ad = loadAd()

        setPreferredColor(binding.root)

        type = intent.getStringExtra("type") ?: "movie"
        viewModel.load(type!!)

        binding.title.text = intent.getStringExtra("genre_title") ?: "ყველა / All"

        val genreId = intent.extras?.getInt("genre_id")
        //  if (genreId != null) {
        viewModel.setGenre(genreId)
        //      binding.btnFilters.visibility = View.INVISIBLE
        //  }

        (binding.rv.layoutManager as GridLayoutManager).spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int = adapter.getItemViewType(position)
            }

        binding.rv.adapter = adapter
        binding.rv.layoutManager = GridLayoutManager(this, 2)

        layoutSpanCount(resources.configuration)
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        viewModel.result.observe(this) {
            adapter.submitData(lifecycle, it)
            adapter.notifyDataSetChanged()
            binding.rv.smoothScrollToPosition(0)
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

        type = null
        selectedGenres.clear()
    }

    override fun onItemClick(movie: Data) {
        if (Random.nextBoolean() && ad.isLoaded && !Random.nextBoolean()) {
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

        Log.i("AllMoviesActivityGenres", genres.toString())

        this.selectedGenres.clear()
        if (genres != null) {
            this.selectedGenres.addAll(genres)
        }
    }
}