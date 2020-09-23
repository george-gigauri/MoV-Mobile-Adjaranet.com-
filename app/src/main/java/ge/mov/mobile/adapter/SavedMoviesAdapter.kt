package ge.mov.mobile.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ge.mov.mobile.R
import ge.mov.mobile.database.MovieEntity
import ge.mov.mobile.model.movie.MovieItemModel
import ge.mov.mobile.model.movie.MovieModel
import ge.mov.mobile.service.APIService
import ge.mov.mobile.ui.activity.MovieActivity
import ge.mov.mobile.util.Utils
import kotlinx.android.synthetic.main.movie_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SavedMoviesAdapter (
    private val context: Context,
    private val arr: List<MovieEntity>
) : RecyclerView.Adapter<SavedMoviesAdapter.ViewHolder>() {
    class ViewHolder (i: View) : RecyclerView.ViewHolder(i)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.movie_item,
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val i = arr[position]
        loadMovie(holder, i.id)
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    private fun loadMovie(holder: ViewHolder, id: Long) {
        holder.itemView.progress.visibility = View.VISIBLE
        APIService.invoke().getMovie(id = id)
            .enqueue(object : Callback<MovieItemModel> {
                override fun onResponse(
                    call: Call<MovieItemModel>,
                    response: Response<MovieItemModel>
                ) {
                    val body = response.body()
                    if (response.code() == 200) {
                        if (response.isSuccessful) {
                            if (body != null){
                                load(holder, body)
                                holder.itemView.progress.visibility = View.GONE
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<MovieItemModel>, t: Throwable) {
                    holder.itemView.progress.visibility = View.GONE
                    Log.i("SavedMoviesAdapter", t.message.toString())
                }
            })
    }

    private fun load(holder: ViewHolder, response: MovieItemModel) {
        val i = response.data

        val language = Utils.loadLanguage(context)
        val lang_code = if (language?.id == "ka") "GEO" else "ENG"

        setMovieName(holder, i, lang_code)
        setMoviePoster(holder, i.posters.data._240)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, MovieActivity::class.java)
            intent.putExtra("id", i.id)
            intent.putExtra("adjaraId", i.adjaraId)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    private fun setMovieName(holder: ViewHolder, name: MovieModel, lang_code: String) {
        holder.itemView.name.text = if (lang_code == "GEO")
            if (name.primaryName != "")
                name.primaryName
            else
                name.secondaryName
        else
            name.secondaryName
    }

    private fun setMoviePoster(holder: ViewHolder, poster: String) {
        try {
            holder.itemView.progress.visibility = View.VISIBLE
            Glide.with(context)
                .asBitmap()
                .placeholder(R.color.colorPrimaryDark)
                .error(R.color.colorPrimaryDark)
                .load(poster)
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.i("SavedMoviesAdapter", e?.message.toString())
                        holder.itemView.progress.visibility = View.GONE
                        return true
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.itemView.poster.setImageBitmap(resource)
                        holder.itemView.progress.visibility = View.GONE
                        return true
                    }
                }).submit()

        } catch (e: Exception) {
            holder.itemView.progress.visibility = View.GONE
            Log.i("SavedMoviesAdapter", e.message.toString())
        }
    }
}