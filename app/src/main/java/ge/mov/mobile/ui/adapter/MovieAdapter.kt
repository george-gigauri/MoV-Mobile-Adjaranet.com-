package ge.mov.mobile.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ge.mov.mobile.MovApplication
import ge.mov.mobile.R
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.databinding.MovieItemBinding
import ge.mov.mobile.extension.loadWithProgressBar
import ge.mov.mobile.util.Utils.getNameByLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieAdapter(
    private val context: Context,
    private var arr: List<Data>,
    private val type: Int = 1,
    private val listener: OnClickListener
) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: MovieItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.movieHover.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    val item = arr[pos]
                    listener.onItemClicked(item)
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

        fun bind(item: Data) {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    binding.name.text = getNameByLanguage(item, MovApplication.language?.code)

                    if (type == 1)
                        binding.name.setTextColor(Color.WHITE)
                    else
                        binding.name.setTextColor(Color.BLACK)

                    if (item.posters?.data != null) {
                        binding.poster.loadWithProgressBar(
                            binding.progress,
                            item.posters.data._240
                        )
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        MovieItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun getItemCount(): Int = arr.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = arr[position]
        holder.bind(item)
    }

    interface OnClickListener {
        fun onItemClicked(item: Data)
    }
}