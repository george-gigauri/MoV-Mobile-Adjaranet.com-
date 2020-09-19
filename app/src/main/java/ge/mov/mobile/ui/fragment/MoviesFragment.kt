package ge.mov.mobile.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ge.mov.mobile.R
import ge.mov.mobile.adapter.MovieAdapter
import ge.mov.mobile.databinding.FragmentMoviesBinding
import ge.mov.mobile.model.movie.MovieModel
import ge.mov.mobile.ui.viewmodel.FragmentMoviesViewModel

class MoviesFragment : Fragment() {
    private lateinit var binding: FragmentMoviesBinding
    private lateinit var vm: FragmentMoviesViewModel
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var adapter: MovieAdapter

    private var page = 1

    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val genre: Int? = arguments?.getInt("genre", 0)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movies, container, false)
        vm = ViewModelProviders.of(this).get(FragmentMoviesViewModel::class.java)

        val view = binding.root

        initRecyclerView()

        adapter = MovieAdapter(activity!!, emptyList())

        vm.getMovies(genre = genre, page = page).observe(this, Observer {
            adapter.addAll(it)
            binding.allmoviesRv.adapter = adapter
            binding.allmoviesRv.post { binding.progressbar.visibility = View.GONE }
        })

        binding.allmoviesRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val lm = recyclerView.layoutManager as GridLayoutManager

                if (!binding.allmoviesRv.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        page++
                        binding.progressbar.visibility = View.VISIBLE
                        vm.getMovies(genre, perPage = 10, page = page).observe(this@MoviesFragment, Observer {

                            adapter = MovieAdapter(activity!!, it)
                            binding.allmoviesRv.adapter = adapter
                            //adapter.addAll(it)

                          //  binding.allmoviesRv.post {
                                binding.progressbar.visibility = View.GONE
                         //   }
                        })
                    }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
//                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
//                val visibleItemCount = layoutManager.childCount
//                val totalItemCount = layoutManager.itemCount
//
//                val isNotLoadingAndIsNotALastPage = !isLoading && !isLastPage
//                val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
//                val isNotAtTop = firstVisibleItemPosition >= 0
//                val shouldPaginate = isNotLoadingAndIsNotALastPage && isAtLastItem && isNotAtTop && isScrolling

                super.onScrolled(recyclerView, dx, dy)
            }
        })

        return view
    }

    private fun initRecyclerView() {
        gridLayoutManager = GridLayoutManager(activity, 2)
        binding.allmoviesRv.layoutManager = gridLayoutManager
    }
}