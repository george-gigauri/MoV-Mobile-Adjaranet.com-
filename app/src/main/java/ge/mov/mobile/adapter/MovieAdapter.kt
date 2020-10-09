package ge.mov.mobile.adapter

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.ImageResult
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideType
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ge.mov.mobile.R
import ge.mov.mobile.model.movie.MovieModel
import ge.mov.mobile.ui.activity.MovieActivity
import ge.mov.mobile.util.CoilUtils
import ge.mov.mobile.util.Utils
import kotlinx.android.synthetic.main.movie_item.view.*
import okhttp3.internal.Util
import java.util.*

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
        val language = Utils.loadLanguage(context)
        val lang_code = if (language?.id == "ka") "GEO" else "ENG"

        holder.text.text = if (lang_code == "GEO")
            if (i.primaryName != "")
                i.primaryName
            else
                i.secondaryName
        else
            i.secondaryName

        /**  USING COIL LIBRARY **/
   /*     CoilUtils.loadWithAnimationAndProgressBar(
            holder.itemView.poster,
            holder.itemView.progress,
            i.posters.data._240,
            450
        ) */
        /** =================== **/

       try {
            Glide.with(context)
                .asBitmap()
                .placeholder(R.color.colorPrimaryDark)
                .error(R.color.colorPrimaryDark)
                .load(i.posters.data._240)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.itemView.progress.visibility = View.VISIBLE
                        return true
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.photo.setImageBitmap(resource)
                        holder.itemView.progress.visibility = View.GONE
                        return true
                    }

                })
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
}