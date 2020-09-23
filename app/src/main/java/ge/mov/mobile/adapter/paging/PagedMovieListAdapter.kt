package ge.mov.mobile.adapter.paging

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import ge.mov.mobile.R
import ge.mov.mobile.model.movie.MovieModel
import kotlinx.android.synthetic.main.movie_item.view.*

class PagedMovieListAdapter(private val context: Context) : PagedListAdapter<MovieModel, PagedMovieListAdapter.VH>(
    MOVIE_COMPARATOR) {
    class VH (i: View) : RecyclerView.ViewHolder(i) {
        private val image: ImageView = i.poster
        private val title: TextView = i.name

        fun bind(context: Context, movie: MovieModel) {
            title.text = movie.primaryName

            Glide.with(context)
                .asBitmap()
                .placeholder(R.color.colorPrimary)
                .error(R.color.colorPrimaryDark)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .load(movie.posters.data._240)
                .into(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val i = getItem(position)
        i?.let { holder.bind(context, i) }
    }

    companion object {
        private val MOVIE_COMPARATOR = object : DiffUtil.ItemCallback<MovieModel>() {
            override fun areItemsTheSame(oldItem: MovieModel, newItem: MovieModel): Boolean =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: MovieModel, newItem: MovieModel): Boolean =
                newItem == oldItem
        }
    }
}