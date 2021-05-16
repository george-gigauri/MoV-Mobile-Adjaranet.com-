package ge.mov.mobile.ui.fragment.movie

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import ge.mov.mobile.R
import ge.mov.mobile.databinding.FragmentMovieCastBinding
import ge.mov.mobile.di.module.AppModule
import ge.mov.mobile.ui.adapter.PersonAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("ValidFragment")
class MovieCastFragment(private val _id: Int = 0) : Fragment(R.layout.fragment_movie_cast) {
    private lateinit var binding: FragmentMovieCastBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMovieCastBinding.bind(view)

        init()
        loadCast()
    }

    private fun init() {
        val orientation = resources.configuration.orientation
        val gridLayoutManager = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager(context, 4)
        } else {
            GridLayoutManager(context, 2)
        }
        binding.castList.layoutManager = gridLayoutManager
    }

    private fun loadCast() {
        lifecycleScope.launch {
            val body =
                withContext(Dispatchers.IO) {
                    AppModule.getMoviesApi(AppModule.getRetrofit()).getCast(id = _id)
                }

            if (context != null) {
                binding.castList.adapter = PersonAdapter(body.body()!!.data, requireContext())
            }
        }
    }
}