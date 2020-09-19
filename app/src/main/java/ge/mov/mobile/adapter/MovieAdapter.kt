package ge.mov.mobile.adapter

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ge.mov.mobile.R
import ge.mov.mobile.model.movie.MovieModel
import ge.mov.mobile.ui.activity.MovieActivity

class MovieAdapter(
    private val context: Context,
    private var arr: List<MovieModel>
) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {
    inner class ViewHolder(i: View) : RecyclerView.ViewHolder(i) {
        val photo: ImageView = i.findViewById(R.id.poster)
        val text: TextView = i.findViewById(R.id.name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.movie_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val i = arr[position]

        holder.text.text = i.secondaryName

        try {
            Glide.with(context)
                .asBitmap()
                .placeholder(R.drawable.button_bg_login)
                .error(R.drawable.button_bg_login)
                .load(i.posters.data._240)
                .into(holder.photo)
        } catch (e: Exception) { }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, MovieActivity::class.java)
            intent.putExtra("id", i.id)
            intent.putExtra("adjaraId", i.adjaraId)
            intent.flags = FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    fun addAll(list: List<MovieModel>) {
        if (!arr.isNullOrEmpty()) {
            this.arr.distinct()
            this.arr = this.arr.plus(list)
            this.notifyDataSetChanged()
        } else {
            this.arr = list
            this.notifyDataSetChanged()
        }
    }
}