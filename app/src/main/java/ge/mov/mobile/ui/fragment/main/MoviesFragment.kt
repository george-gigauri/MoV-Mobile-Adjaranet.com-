package ge.mov.mobile.ui.fragment.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.InterstitialAd
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.R
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.databinding.FragmentMoviesBinding
import ge.mov.mobile.ui.activity.movie.MovieActivity
import ge.mov.mobile.ui.adapter.MovieAdapter
import ge.mov.mobile.ui.fragment.movie.FragmentMoviesViewModel
import ge.mov.mobile.util.Constants
import ge.mov.mobile.util.loadAd
import kotlin.random.Random

@AndroidEntryPoint
class MoviesFragment : Fragment(R.layout.fragment_movies), MovieAdapter.OnClickListener {
    private lateinit var binding: FragmentMoviesBinding
    private val vm: FragmentMoviesViewModel by viewModels()
    private lateinit var gridLayoutManager: GridLayoutManager
    private var genre: Int? = null
    private var genreName: String = ""
    private var page = 1
    private var perPage = 30
    private lateinit var ad: InterstitialAd

    private lateinit var adapter: MovieAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMoviesBinding.bind(view)

        ad = requireActivity().loadAd()

        initRecyclerView()

        binding.prevPageButton.visibility = View.GONE

        binding.goback.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        vm.isLoading().observe(viewLifecycleOwner, Observer {
            progressVisible(it)
        })


        vm.getMovies(genre=genre, perPage = perPage).observe(viewLifecycleOwner, Observer {
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

            binding.allmoviesRv.adapter = if (!Constants.showAdultContent) {
                MovieAdapter(requireActivity(), it.data.filter { m -> !m.adult }, listener = this)
            } else {
                MovieAdapter(requireActivity(), it.data, listener = this)
            }


            binding.allmoviesRv.post {
                progressVisible(false)
                recyclerViewToTop()
            }

            binding.nextPageButton.setOnClickListener {
                page++
                binding.pageCount.text = page.toString()

                vm.getMovies(genre = genre, perPage = perPage)
                    .observe(viewLifecycleOwner, { movieRes ->
                        progressVisible(true)
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

                        binding.allmoviesRv.adapter = null
                        binding.allmoviesRv.adapter = if (!Constants.showAdultContent) {
                            MovieAdapter(
                                requireActivity(),
                                movieRes.data.filter { m -> !m.adult },
                                listener = this
                            )
                        } else {
                            MovieAdapter(requireActivity(), movieRes.data, listener = this)
                        }

                        binding.allmoviesRv.post {
                            progressVisible(false)
                            recyclerViewToTop()
                        }
                    })
            }

            binding.prevPageButton.setOnClickListener {
                page--
                binding.pageCount.text = page.toString()

                vm.getMovies(genre = genre, perPage = perPage)
                    .observe(viewLifecycleOwner, { movieRes ->
                        if (movieRes.meta.pagination.totalPages == vm.page)
                            nextButtonVisible(false)
                        else
                            nextButtonVisible(true)

                        binding.allmoviesRv.adapter = if (!Constants.showAdultContent) {
                            MovieAdapter(
                                requireActivity(),
                                movieRes.data.filter { m -> !m.adult },
                                listener = this
                            )
                        } else {
                            MovieAdapter(requireActivity(), movieRes.data, listener = this)
                        }

                        recyclerViewToTop()
                    })
            }
        })
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
        genre = requireArguments().getInt("genre")
        genreName = requireArguments().getString("genreName", "")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_movies, container, false)

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

    override fun onItemClicked(item: Data) {
        val intent = Intent(requireContext(), MovieActivity::class.java)
        intent.putExtra("id", item.id)
        intent.putExtra("adjaraId", item.adjaraId)
        startActivity(intent)

        if (Random.nextBoolean() && ad.isLoaded)
            ad.show()
    }
}