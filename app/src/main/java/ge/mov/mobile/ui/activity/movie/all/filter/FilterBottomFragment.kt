package ge.mov.mobile.ui.activity.movie.all.filter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.R
import ge.mov.mobile.data.model.Language
import ge.mov.mobile.data.model.movie.Genre
import ge.mov.mobile.databinding.FiltersSheetBinding
import ge.mov.mobile.ui.activity.movie.all.ViewModelAll
import ge.mov.mobile.ui.adapter.GenreAdapter
import ge.mov.mobile.ui.adapter.LanguageAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class FilterBottomFragment : BottomSheetDialogFragment(), GenreAdapter.OnGenreSelectListener, LanguageAdapter.OnLanguageSelectedListener {
    private var _binding: FiltersSheetBinding? = null
    private val binding: FiltersSheetBinding get() = _binding!!
    private val vm: ViewModelAll by viewModels()
    private var arrGenres: ArrayList<Int> = ArrayList()
    private var selectedLanguage: String =  "GEO"
    private lateinit var languageAdapter: LanguageAdapter

    private lateinit var adapter: GenreAdapter

    companion object {
        private var filterListener: OnFilterListener? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FiltersSheetBinding.inflate(inflater, container, false)
        return inflater.inflate(R.layout.filters_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FiltersSheetBinding.bind(view)
        binding.filters = vm

        languageAdapter = LanguageAdapter(this)
        binding.filterLanguagesRv.adapter = languageAdapter

        arrGenres =
            arguments?.getIntegerArrayList("selectedGenres") as ArrayList<Int>? ?: ArrayList()
        selectedLanguage = arguments?.getString("selectedLanguage", "GEO") ?: "GEO"
        binding.etYearFrom.setText(arguments?.getString("yearFrom", "0") ?: "0")
        binding.etYearTo.setText(arguments?.getString("yearTo", "0") ?: Calendar.getInstance()[Calendar.YEAR].toString())

        val listener = this
        lifecycleScope.launch {
            val genres = withContext(Dispatchers.IO) { vm.loadGenres() }

            withContext(Dispatchers.Main) {
                adapter = GenreAdapter(
                    genres.body()!!.data,
                    requireActivity(),
                    1,
                    selectedItems = arrGenres,
                    listener = listener
                )
                binding.filterGenresRv.adapter = adapter
                binding.filterGenresRv.scrollToPosition(adapter.firstSelectedPosition())
            }

            val languages = withContext(Dispatchers.IO) { vm.getLanguages() }
            withContext(Dispatchers.Main) {
                if (!languages.isNullOrEmpty())
                    languageAdapter.addAll(languages)
                languageAdapter.select(selectedLanguage)
                binding.filterLanguagesRv.scrollToPosition(languageAdapter.firstSelectedPosition())
            }
        }

        binding.playButton.setOnClickListener {
            var yearFrom: String = binding.etYearFrom.text.toString()
            var yearTo: String = binding.etYearTo.text.toString()

            yearFrom = if (yearFrom.isEmpty()) {
                (0).toString()
            } else {
                binding.etYearFrom.text.toString()
            }

            yearTo = if (yearTo.isEmpty()) (0).toString() else binding.etYearTo.text.toString()

            if (yearFrom.isNotEmpty() && yearTo.isNotEmpty()) {
                filterListener?.onFilterRequested(
                    arrGenres.toList(),
                    selectedLanguage,
                    yearFrom.toInt(),
                    yearTo.toInt()
                )
            }

            this.dismiss()
        }
    }

    fun attachListener(listener: OnFilterListener) {
        filterListener = listener
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
        filterListener = null
    }

    override fun getTheme() = R.style.BottomSheetDialogTheme

    override fun onSelected(item: Genre, holder: GenreAdapter.ViewHolder, position: Int) {
        val id = item.id

        if (arrGenres.contains(id))
            arrGenres.remove(id)
        else
            arrGenres.add(id)
    }

    interface OnFilterListener : Serializable {
        fun onFilterRequested(genres: List<Int>?, languageFilter: String, yearFrom: Int, yearTo: Int)
    }

    override fun onLanguageSelected(language: Language) {
        selectedLanguage = language.code
    }
}