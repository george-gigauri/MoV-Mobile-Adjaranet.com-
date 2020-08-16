package ge.mov.mobile.service

import com.google.gson.Gson
import ge.mov.mobile.model.featured.FeaturedModel
import ge.mov.mobile.model.movie.Movie
import ge.mov.mobile.model.movie.MovieModel
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
    @GET("movies/featured?source=adjaranet")
    fun getFeatured(): List<FeaturedModel>

    @GET("movies")
    fun getMovies(@Query("page") page: Int) : Call<Movie>

    @GET("movies/{id}")
    fun getMovie(@Path("id") id: Long) : MovieModel

    companion object {
        fun invoke(): APIService
        {
            return Retrofit.Builder()
                .baseUrl("https://api.adjaranet.com/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(APIService::class.java)
        }
    }
}