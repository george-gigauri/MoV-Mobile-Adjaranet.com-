package ge.mov.mobile.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.InterstitialAd
import ge.mov.mobile.R
import ge.mov.mobile.data.database.MovieEntity
import ge.mov.mobile.data.model.basic.BasicMovie
import ge.mov.mobile.data.model.movie.MovieItemModel
import ge.mov.mobile.data.model.movie.MovieModel
import ge.mov.mobile.di.module.AppModule
import ge.mov.mobile.ui.activity.movie.MovieActivity
import ge.mov.mobile.util.Utils
import ge.mov.mobile.util.loadWithProgressBar
import ge.mov.mobile.util.showAd
import kotlinx.android.synthetic.main.movie_item.view.*
import kotlinx.coroutines.*
import kotlin.random.Random

class SavedMoviesAdapter (
    private val context: Context,
    private val arr: ArrayList<MovieEntity>,
    private val ad: InterstitialAd? = null
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

    private fun loadMovie(holder: ViewHolder, id: Int) {
        holder.itemView.progress.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) { AppModule.getMoviesApi(AppModule.getRetrofit()).getMovie(id = id) }
            load(holder, response.body()!!)
        }
        holder.itemView.progress.visibility = View.GONE
    }

    private fun load(holder: ViewHolder, response: MovieItemModel) {
        val i = response.data

        val language = Utils.loadLanguage(context)
        val lang_code = if (language.id == "ka") "GEO" else "ENG"

        CoroutineScope(Dispatchers.Main).launch {
            setMovieName(holder, i, lang_code)
            setMoviePoster(holder, if (i.posters.data != null) i.posters.data._240 else "")

            holder.itemView.setOnClickListener {
                val show = Random.nextBoolean()

                val intent = Intent(context, MovieActivity::class.java)
                intent.putExtra("id", i.id)
                intent.putExtra("adjaraId", i.adjaraId)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)

                if (show)
                    ad?.let { it1 -> context.showAd(it1) }
            }
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
        holder.itemView.poster.loadWithProgressBar(holder.itemView.progress, poster)
    }

    fun addAll(list: List<MovieEntity>) {
        val count = arr.size
        arr.addAll(list)
        notifyItemInserted(count)
    }
}