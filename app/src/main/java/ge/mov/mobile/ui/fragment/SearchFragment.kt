package ge.mov.mobile.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import ge.mov.mobile.R
import ge.mov.mobile.adapter.MovieAdapter
import ge.mov.mobile.databinding.FragmentSearchBinding
import ge.mov.mobile.model.movie.MovieModel
import ge.mov.mobile.ui.viewmodel.FragmentSearchViewModel
import ge.mov.mobile.util.KeyboardUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: FragmentSearchViewModel
    private lateinit var gridLayoutManager: GridLayoutManager
    private var page: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        val view = binding.root
        viewModel = ViewModelProviders.of(this).get(FragmentSearchViewModel::class.java)

        binding.inputSearchKeyword.requestFocus()
        KeyboardUtils.open(activity!!.applicationContext)

        val orientation = resources.configuration.orientation
        gridLayoutManager = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager(activity?.applicationContext, 4)
        } else {
            GridLayoutManager(activity?.applicationContext, 2)
        }
        binding.searchResultsRv.layoutManager = gridLayoutManager

        binding.progressbar.visibility = View.GONE

        binding.inputSearchKeyword.addTextChangedListener {
            GlobalScope.launch(Dispatchers.Main) {
                delay(600)
                binding.progressbar.visibility = View.VISIBLE
                viewModel.search(
                    binding.inputSearchKeyword.text.toString(),
                    page).observe(this@SearchFragment, {

                    binding.searchResultsRv.adapter = MovieAdapter(activity!!.applicationContext, it.data)

                    binding.searchResultsRv.post {
                        binding.progressbar.visibility = View.GONE
                    }
                })
            }.start()
        }

        binding.goBack.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
            KeyboardUtils.close(activity!!.applicationContext)
        }

        return view
    }
}