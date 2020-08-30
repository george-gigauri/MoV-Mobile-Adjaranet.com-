package ge.mov.mobile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ge.mov.mobile.R
import ge.mov.mobile.model.movie.Genre
import kotlinx.android.synthetic.main.genre_model.view.*

class GenreAdapter (
    private val arr: List<Genre>,
    private val context: Context
) : RecyclerView.Adapter<GenreAdapter.ViewHolder>() {
    class ViewHolder(i: View) : RecyclerView.ViewHolder(i)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.genre_model, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val i = arr[position]

        holder.itemView.genre.text = i.primaryName
    }

    override fun getItemCount(): Int {
        return arr.size
    }
}