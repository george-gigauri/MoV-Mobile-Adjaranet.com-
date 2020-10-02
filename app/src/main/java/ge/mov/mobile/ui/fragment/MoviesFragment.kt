package ge.mov.mobile.ui.fragment

import android.content.Context.WINDOW_SERVICE
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import ge.mov.mobile.R
import ge.mov.mobile.adapter.MovieAdapter
import ge.mov.mobile.databinding.FragmentMoviesBinding
import ge.mov.mobile.ui.viewmodel.FragmentMoviesViewModel

class MoviesFragment : Fragment() {
    private lateinit var binding: FragmentMoviesBinding
    private lateinit var vm: FragmentMoviesViewModel
    private lateinit var gridLayoutManager: GridLayoutManager
    private var genre: Int? = null
    private var genreName: String = ""
    private var page = 1
    private var perPage = 30

    private lateinit var adapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        globalInit(inflater, container)
        val view = binding.root
        initRecyclerView()

        //progressVisible(true)
        binding.prevPageButton.visibility = View.GONE

        binding.goback.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        vm.isLoading().observe(this, Observer {
            progressVisible(it)
        })


        vm.getMovies(genre=genre, page=page, perPage = perPage).observe(this, Observer {
            if (it.meta.pagination.currentPage == 1)
                if (it.meta.pagination.totalPages - 1 == it.meta.pagination.currentPage) {
                    prevButtonVisible(false)
                    nextButtonVisible(false)
                } else {
                    prevButtonVisible(false)
                    nextButtonVisible(true)
                }
            else {
                if (it.meta.pagination.totalPages - 1 == it.meta.pagination.currentPage)
                    nextButtonVisible(false)
                else
                    nextButtonVisible(true)
                prevButtonVisible(true)
            }

            binding.allmoviesRv.adapter = MovieAdapter(activity!!, it.data)
            binding.allmoviesRv.post { progressVisible(false) }

            binding.nextPageButton.setOnClickListener {
                page++
                binding.pageCount.text = page.toString()

                vm.getMovies(genre=genre, page=page, perPage = perPage).observe(this, Observer {movieRes ->
                   // progressVisible(true)
                    if (movieRes.meta.pagination.currentPage == 1)
                        if (movieRes.meta.pagination.totalPages - 1 == movieRes.meta.pagination.currentPage) {
                            prevButtonVisible(false)
                            nextButtonVisible(false)
                        } else {
                            prevButtonVisible(false)
                            nextButtonVisible(true)
                        }
                    else {
                        if (movieRes.meta.pagination.totalPages - 1 == movieRes.meta.pagination.currentPage)
                            nextButtonVisible(false)
                        else
                            nextButtonVisible(true)
                        prevButtonVisible(true)
                    }

                    binding.allmoviesRv.adapter = MovieAdapter(activity!!, movieRes.data)
                    recyclerViewToTop()
                   // binding.allmoviesRv.post { progressVisible(false) }
                })
            }

            binding.prevPageButton.setOnClickListener {
                page--
                binding.pageCount.text = page.toString()

                vm.getMovies(genre=genre, page=page, perPage = perPage).observe(this, Observer {movieRes ->
                    if (movieRes.meta.pagination.totalPages == page)
                        nextButtonVisible(false)
                    else
                        nextButtonVisible(true)

                    binding.allmoviesRv.adapter = MovieAdapter(activity!!, movieRes.data)
                    recyclerViewToTop()
                })
            }
        })

        return view
    }

    private fun initRecyclerView() {
        val orientation = resources.configuration.orientation
        gridLayoutManager = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            perPage = 32
            GridLayoutManager(activity?.applicationContext, 4)
        } else {
            GridLayoutManager(activity?.applicationContext, 2)
        }
        binding.allmoviesRv.layoutManager = gridLayoutManager
    }

    private fun globalInit(inflater: LayoutInflater, container: ViewGroup?) {
        genre = arguments?.getInt("genre")
        genreName = arguments!!.getString("genreName", "")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movies, container, false)
        vm = ViewModelProviders.of(this).get(FragmentMoviesViewModel::class.java)

        binding.genreTitle.text = genreName
    }

    private fun prevButtonVisible(isVisible: Boolean) {
        if (isVisible)
            binding.prevPageButton.visibility = View.VISIBLE
        else
            binding.prevPageButton.visibility = View.GONE
    }

    private fun nextButtonVisible(isVisible: Boolean) {
        if (isVisible)
            binding.nextPageButton.visibility = View.VISIBLE
        else
            binding.nextPageButton.visibility = View.GONE
    }

    private fun progressVisible(visible: Boolean) {
        if (visible)
            binding.progress.visibility = View.VISIBLE
        else
            binding.progress.visibility = View.GONE
    }

    private fun recyclerViewToTop() {
        binding.scrollview.smoothScrollTo(0, 0, 1400)
    }
}