package ge.mov.mobile.paging.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ge.mov.mobile.R
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.databinding.MovieItemBinding
import ge.mov.mobile.util.LanguageUtil
import ge.mov.mobile.util.Utils
import ge.mov.mobile.util.loadWithProgressBar

class SearchMoviePagingAdapter(
    private val listener: ItemClickListener
) : PagingDataAdapter<Data, SearchMoviePagingAdapter.VH>(COMPARATOR) {

    inner class VH(private val binding: MovieItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.movieHover.setOnClickListener {
                val position = bindingAdapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(item)
                    }
                }
            }

            binding.movieHover.setOnFocusChangeListener { _, hasFocus ->
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    if (hasFocus)
                        binding.movieHover.setBackgroundResource(R.drawable.selected_stroke)
                    else binding.movieHover.background = null
                }
            }
        }

        fun bind(i: Data) {
            val language = LanguageUtil.language
            val lang_code = if (language?.id == "ka") "GEO" else "ENG"

            binding.apply {
                name.text = Utils.getNameByLanguage(i, lang_code)
                poster.loadWithProgressBar(progress, i.posters?.data?._240)
            }
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val current = getItem(position)

        if (current != null)
            holder.bind(current)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(
        MovieItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Data>() {
            override fun areItemsTheSame(oldItem: Data, newItem: Data) = oldItem == newItem

            override fun areContentsTheSame(oldItem: Data, newItem: Data) = oldItem == newItem
        }
    }

    interface ItemClickListener {
        fun onItemClick(item: Data)
    }
}