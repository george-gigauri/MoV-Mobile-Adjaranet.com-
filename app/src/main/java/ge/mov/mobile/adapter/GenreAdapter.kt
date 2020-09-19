package ge.mov.mobile.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ge.mov.mobile.R
import ge.mov.mobile.model.movie.Genre
import ge.mov.mobile.ui.activity.MainActivity
import ge.mov.mobile.ui.fragment.MoviesFragment
import kotlinx.android.synthetic.main.genre_model.view.*

class GenreAdapter (
    private val arr: List<Genre>,
    private val context: Context,
    private val type: Int
) : RecyclerView.Adapter<GenreAdapter.ViewHolder>() {
    class ViewHolder(i: View) : RecyclerView.ViewHolder(i)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.genre_model, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val i = arr[position]

        if (type == 1) {
            holder.itemView.genre.setBackgroundResource(R.drawable.categories_light_background)
            holder.itemView.genre.setTextColor(context.resources.getColor(R.color.colorPrimaryDark))
        } else {
            holder.itemView.genre.setBackgroundResource(R.drawable.blue_bg_rounded)
            holder.itemView.genre.setTextColor(Color.WHITE)
        }

        holder.itemView.genre.text = i.primaryName

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("genre", i.id)
            val fragmentMovies = MoviesFragment()
            fragmentMovies.arguments = bundle

            (context as MainActivity).supportFragmentManager.beginTransaction().add(R.id.root_main, fragmentMovies, "movies").addToBackStack("movies").commit()
        }

    }

    override fun getItemCount(): Int {
        return arr.size
    }
}