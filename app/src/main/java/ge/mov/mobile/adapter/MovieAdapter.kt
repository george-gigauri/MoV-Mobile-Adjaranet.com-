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
import ge.mov.mobile.R
import ge.mov.mobile.model.basic.Data
import ge.mov.mobile.ui.activity.MovieActivity
import ge.mov.mobile.util.Utils
import ge.mov.mobile.util.loadWithAnimationAndProgressBar
import ge.mov.mobile.util.loadWithProgressBar
import kotlinx.android.synthetic.main.movie_item.view.*

class MovieAdapter(
    private val context: Context,
    private var arr: List<Data>
) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {
    inner class ViewHolder(i: View) : RecyclerView.ViewHolder(i) {
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
        val language = Utils.loadLanguage(context)
        val lang_code = if (language?.id == "ka") "GEO" else "ENG"

        holder.text.text = if (lang_code == "GEO")
            if (i.primaryName != "")
                i.primaryName
            else
                i.secondaryName
        else
            i.secondaryName

        if (i.posters?.data != null) {
            holder.itemView.poster.loadWithProgressBar(
                holder.itemView.progress,
                i.posters.data._240
            )
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, MovieActivity::class.java)
            intent.putExtra("id", i.id)
            intent.putExtra("adjaraId", i.adjaraId)
            intent.flags = FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}