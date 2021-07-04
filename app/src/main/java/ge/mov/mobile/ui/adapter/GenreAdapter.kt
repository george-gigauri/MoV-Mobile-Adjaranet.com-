package ge.mov.mobile.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ge.mov.mobile.R
import ge.mov.mobile.data.model.movie.Genre
import ge.mov.mobile.util.Utils
import kotlinx.android.synthetic.main.genre_model.view.*

class GenreAdapter(
    private val arr: List<Genre>,
    private val context: Context?,
    private val type: Int,
    private val listener: OnGenreClickListener? = null
) : RecyclerView.Adapter<GenreAdapter.ViewHolder>() {
    class ViewHolder(i: View) : RecyclerView.ViewHolder(i)

    var selectedItems: ArrayList<Int> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.genre_model, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val i = arr[position]

        i.isSelected = selectedItems.contains(i.id)

        if (type == 1 && context != null) {
            holder.itemView.genre.isSelected = true
            holder.itemView.genre.setTextColor(Color.BLACK)
        } else {
            holder.itemView.genre.isSelected = true
            holder.itemView.genre.setTextColor(holder.itemView.context.resources.getColor(R.color.colorAccent))
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

        holder.itemView.setOnClickListener { listener?.onClicked(i, position) }

        holder.itemView.genre.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                holder.itemView.genre.requestFocus()
            else holder.itemView.genre.clearFocus()
        }
    }

    private fun changeBackground(isSelected: Boolean, holder: ViewHolder) {
        holder.itemView.genre.isSelected = isSelected
        if (isSelected) {
            holder.itemView.genre.setTextColor(holder.itemView.context.resources.getColor(R.color.colorAccent))
        } else {
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

    interface OnGenreClickListener {
        fun onClicked(item: Genre, position: Int)
    }
}