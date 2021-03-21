package ge.mov.mobile.paging.movies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ge.mov.mobile.MovApplication
import ge.mov.mobile.R
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.databinding.MovieItemBinding
import ge.mov.mobile.util.LanguageUtil
import ge.mov.mobile.util.loadWithProgressBar

class MoviePagingAdapter(
    private val listener: MovieClickListener
) : PagingDataAdapter<Data, MoviePagingAdapter.VH>(COMPARATOR) {
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
            binding.apply {
                name.text = i.getNameByLanguage(MovApplication.language?.code)
                poster.loadWithProgressBar(progress, i.posters?.data?._240)
            }
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val current = getItem(position)

        if (current != null)
            holder.bind(current)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
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

    interface MovieClickListener {
        fun onItemClick(movie: Data)
    }
}