package ge.mov.mobile.paging.movies.load_state

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ge.mov.mobile.databinding.PagingFooterBinding

class MovieLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<MovieLoadStateAdapter.VH>() {
    inner class VH(private val binding: PagingFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retry.setOnClickListener { retry.invoke() }
        }

        fun bind(item: LoadState) {
            binding.loadingMore.isVisible = item is LoadState.Loading
            binding.loadingText.isVisible = item is LoadState.Loading

            binding.errorMessage.isVisible = item !is LoadState.Loading
            binding.retry.isVisible = item !is LoadState.Loading
        }
    }

    override fun onBindViewHolder(holder: VH, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) = VH(
        PagingFooterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )
}