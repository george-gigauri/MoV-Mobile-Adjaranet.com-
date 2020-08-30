package ge.mov.mobile.service

import com.google.gson.Gson
import ge.mov.mobile.model.Person
import ge.mov.mobile.model.featured.Featured
import ge.mov.mobile.model.featured.FeaturedModel
import ge.mov.mobile.model.movie.Movie
import ge.mov.mobile.model.movie.MovieItemModel
import ge.mov.mobile.model.movie.MovieModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface APIService {
    @GET("movies/featured?source=adjaranet")
    fun getFeatured(): Call<Featured>

    @GET("movies")
    fun getMovies(
        @Query("page") page: Int,
        @Query("per_page") per_page: Int = 30,
        @Query("filters[language]") language: String = "GEO",
        @Query("filters[type]") type: String = "movie",
        @Query("filters[only_public") public: String = "yes",
        @Query("filters[with_actors]") actors: Int = 3,
        @Query("filters[with_directors]") directors: Int = 1,
        @Query("filters[with_files]") files: String = "yes",
        @Query("sort") sort: String = "-upload_date",
        @Query("source") source: String = "adjaranet"
    ) : Call<Movie>

    @GET("movies/{id}/persons")
    fun getCast(
        @Path("id") id: Long
    ) : Call<Person>

    @GET("movies/{id}")
    fun getMovie(@Path("id") id: Long) : Call<MovieItemModel>

    companion object {
        operator fun invoke(): APIService
        {
            return Retrofit.Builder()
                .baseUrl("https://api.adjaranet.com/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(APIService::class.java)
        }
    }
}