package ge.mov.mobile.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ge.mov.mobile.R
import ge.mov.mobile.data.database.entity.MovieEntity
import ge.mov.mobile.data.model.basic.Data
import ge.mov.mobile.data.model.movie.MovieItemModel
import ge.mov.mobile.data.model.movie.MovieModel
import ge.mov.mobile.di.module.AppModule
import ge.mov.mobile.extension.loadWithProgressBar
import ge.mov.mobile.util.Utils
import kotlinx.android.synthetic.main.movie_item.view.*
import kotlinx.coroutines.*

class SavedMoviesAdapter(
    private val context: Context,
    private val arr: ArrayList<MovieEntity>,
    private val listener: MovieAdapter.OnClickListener
) : RecyclerView.Adapter<SavedMoviesAdapter.ViewHolder>() {
    class ViewHolder(private val i: View) : RecyclerView.ViewHolder(i) {
        fun bind() {
            /* i.movie_hover.setOnFocusChangeListener { _, hasFocus ->
                 if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                     if (hasFocus)
                         i.movie_hover.setBackgroundResource(R.drawable.selected_stroke)
                     else i.movie_hover.background = null
                 }
             } */
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.movie_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val i = arr[position]
        loadMovie(holder, i.id)
        holder.bind()
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
                listener.onItemClicked(Data.fromMovieModel(i))
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