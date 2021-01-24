package ge.mov.mobile.ui.adapter

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ge.mov.mobile.R
import ge.mov.mobile.data.model.movie.Genre
import ge.mov.mobile.ui.activity.main.MainActivity
import ge.mov.mobile.ui.activity.movie.MovieActivity
import ge.mov.mobile.ui.fragment.main.MoviesFragment
import ge.mov.mobile.util.Utils
import kotlinx.android.synthetic.main.genre_model.view.*
import java.io.Serializable

class GenreAdapter(
    private val arr: List<Genre>,
    private val context: Context?,
    private val type: Int,
    private val selectedItems: ArrayList<Int>? = null,
    private val listener: OnGenreSelectListener? = null
) : RecyclerView.Adapter<GenreAdapter.ViewHolder>() {
    class ViewHolder(i: View) : RecyclerView.ViewHolder(i)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.genre_model, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val i = arr[position]

        if (selectedItems != null)
            if (selectedItems.contains(i.id))
                i.isSelected = true

        if (type == 1 && context != null) {
            holder.itemView.genre.setBackgroundResource(R.drawable.categories_light_background)
            holder.itemView.genre.setTextColor(Color.BLACK)
        } else {
            holder.itemView.genre.setBackgroundResource(R.drawable.blue_bg_rounded)
            holder.itemView.genre.setTextColor(Color.WHITE)
        }

        if (type == 1 && listener != null)
            changeBackground(i.isSelected, holder)

        var lang_code = "ENG"
        if (context != null) {
            val language = Utils.loadLanguage(context)
            lang_code = if (language.id == "ka") "GEO" else "ENG"
        }

        holder.itemView.genre.text = if (lang_code == "GEO")
            if (i.primaryName != "")
                i.primaryName
            else
                i.secondaryName
        else
            i.secondaryName


        holder.itemView.setOnClickListener {
            if (listener == null) {
                val bundle = Bundle()
                bundle.putInt("genre", i.id)
                bundle.putString("genreName", holder.itemView.genre.text.toString())
                val fragmentMovies = MoviesFragment()
                fragmentMovies.arguments = bundle

                if (context != null) {
                    if (type == 1) {
                        (context as MainActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.root_main, fragmentMovies, "movies")
                            .addToBackStack("movies")
                            .commit()
                    } else {
                        (context as MovieActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.rootMovie, fragmentMovies, "movies")
                            .addToBackStack("movies")
                            .commit()
                    }
                }
            } else {
                i.isSelected = !i.isSelected
                changeBackground(i.isSelected, holder)
                listener.onSelected(i, holder, position)
            }
        }
    }

    private fun changeBackground(clicked: Boolean, holder: ViewHolder) {
        if (clicked) {
            holder.itemView.genre.setBackgroundResource(R.drawable.blue_bg_rounded)
            holder.itemView.genre.setTextColor(Color.WHITE)
        } else {
            holder.itemView.genre.setBackgroundResource(R.drawable.categories_light_background)
            holder.itemView.genre.setTextColor(Color.BLACK)
        }
    }

    override fun getItemCount() = arr.size

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.requestFocus()
    }

    fun firstSelectedPosition(): Int {
        return if (!selectedItems.isNullOrEmpty()) {
            var firstIndex = 0
            for (i in arr.indices) {
                for (j in selectedItems) {
                    if (arr[i].id == j) {
                        firstIndex = i
                        break
                    }
                }
            }
            firstIndex
        } else 0
    }

    interface OnGenreSelectListener : Serializable {
        fun onSelected(item: Genre, holder: ViewHolder, position: Int)
    }

    interface OnGenreClickListener {
        fun onClicked(item: Genre)
    }
}