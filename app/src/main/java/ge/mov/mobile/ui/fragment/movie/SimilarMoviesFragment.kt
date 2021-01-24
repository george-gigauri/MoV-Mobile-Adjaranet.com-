package ge.mov.mobile.ui.fragment.movie

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.InterstitialAd
import ge.mov.mobile.R
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.databinding.FragmentSimilarMoviesBinding
import ge.mov.mobile.di.module.AppModule
import ge.mov.mobile.ui.activity.movie.MovieActivity
import ge.mov.mobile.ui.adapter.MovieAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

@SuppressLint("ValidFragment")
class SimilarMoviesFragment(
    private val _id: Int = 0,
    private val adjaraId: Int = 0,
    private val ad: InterstitialAd
) : Fragment(), MovieAdapter.OnClickListener {
    private lateinit var binding: FragmentSimilarMoviesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_similar_movies, container, false)
        binding = FragmentSimilarMoviesBinding.bind(view)
        binding.lifecycleOwner = this

        init()
        loadSimilarMovies()

        return view
    }

    private fun init() {
        val orientation = resources.configuration.orientation
        val gridLayoutManager = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager(context, 4)
        } else {
            GridLayoutManager(context, 2)
        }
        binding.similarMoviesList.layoutManager = gridLayoutManager
    }

    private fun loadSimilarMovies() {
        lifecycleScope.launch {
            val body = withContext(Dispatchers.IO) { AppModule.getMoviesApi(AppModule.getRetrofit()).getSimilarMovies(id=adjaraId).body() }
            if (context != null) {
                if (body?.data != null) {
                    binding.similarMoviesList.adapter =
                        MovieAdapter(requireContext(), body.data, 2, listener = this@SimilarMoviesFragment)
                }
            }
        }
    }

    override fun onItemClicked(item: Data) {
        val intent = Intent(requireContext(), MovieActivity::class.java)
        intent.putExtra("id", item.id)
        intent.putExtra("adjaraId", item.adjaraId)
        startActivity(intent)

        if (Random.nextBoolean() && ad.isLoaded)
            ad.show()
    }
}