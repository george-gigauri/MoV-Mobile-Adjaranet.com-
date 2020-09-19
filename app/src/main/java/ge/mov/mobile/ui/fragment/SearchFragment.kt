package ge.mov.mobile.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import ge.mov.mobile.R
import ge.mov.mobile.adapter.MovieAdapter
import ge.mov.mobile.databinding.FragmentSearchBinding
import ge.mov.mobile.model.movie.MovieModel
import ge.mov.mobile.ui.viewmodel.FragmentSearchViewModel

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: FragmentSearchViewModel
    private var page: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        val view = binding.root
        viewModel = ViewModelProviders.of(this).get(FragmentSearchViewModel::class.java)

        val gridLayoutManager = GridLayoutManager(activity!!, 2)
        binding.searchResultsRv.layoutManager = gridLayoutManager

        binding.progressbar.visibility = View.GONE
        binding.inputSearchKeyword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.progressbar.visibility = View.VISIBLE
                viewModel.search(
                    binding.inputSearchKeyword.text.toString(),
                    page).observe(this@SearchFragment, Observer {

                    binding.searchResultsRv.adapter = MovieAdapter(activity!!, it.data)

                    binding.searchResultsRv.post {
                        binding.progressbar.visibility = View.GONE
                    }
                })
            }
        })

        binding.goBack.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }

        return view
    }
}