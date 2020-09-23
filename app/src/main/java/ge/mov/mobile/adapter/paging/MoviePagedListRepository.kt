package ge.mov.mobile.adapter.paging

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ge.mov.mobile.model.movie.Movie
import ge.mov.mobile.model.movie.MovieModel

class MoviePagedListRepository {
    lateinit var moviePagedList: LiveData<PagedList<MovieModel>>
    lateinit var movieDataSourceFactory: MovieDataSourceFactory

    fun fetchLiveMoviePagedList(genre: Int? = null) : LiveData<PagedList<MovieModel>> {
        movieDataSourceFactory = MovieDataSourceFactory(genre)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(10)
            .build()

        moviePagedList = LivePagedListBuilder(movieDataSourceFactory, config).build()
        return moviePagedList
    }
}