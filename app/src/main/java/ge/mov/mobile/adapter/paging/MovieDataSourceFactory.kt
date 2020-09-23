package ge.mov.mobile.adapter.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import ge.mov.mobile.model.movie.MovieModel


class MovieDataSourceFactory(private val genre: Int? = null) : DataSource.Factory<Int, MovieModel>() {
    val movieLiveDataSource: MutableLiveData<MovieDataSource> = MutableLiveData()

    override fun create(): DataSource<Int, MovieModel> {
        val dataSrc = MovieDataSource(genre)
        movieLiveDataSource.postValue(dataSrc)
        return dataSrc
    }
}