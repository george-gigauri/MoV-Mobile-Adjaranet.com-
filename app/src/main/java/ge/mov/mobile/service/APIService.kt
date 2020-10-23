package ge.mov.mobile.service

import android.content.Context
import ge.mov.mobile.BuildConfig
import ge.mov.mobile.model.Series.EpisodeFiles
import ge.mov.mobile.model.Series.Person
import ge.mov.mobile.model.Series.PersonModel
import ge.mov.mobile.model.basic.BasicMovie
import ge.mov.mobile.model.featured.Featured
import ge.mov.mobile.model.movie.Genres
import ge.mov.mobile.model.movie.Movie
import ge.mov.mobile.model.movie.MovieItemModel
import ge.mov.mobile.util.Constants
import okhttp3.Cache
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

interface APIService {
    @GET("movies/featured?source=adjaranet")
    //@GET("movies/movie-day")
    suspend fun getFeatured(): Response<Featured>

    @GET("movies")
    fun getMovies(
        @Query("page") page: Int,
        @Query("per_page") per_page: Int = 30,
        @Query("filters[language]") language: String = "GEO",
        @Query("filters[type]") type: String = "movie", // adjaranet
        @Query("filters[only_public") public: String = "yes",
        @Query("filters[with_actors]") actors: Int = 3,
        @Query("filters[with_directors]") directors: Int = 1,
        @Query("filters[with_files]") files: String = "yes",
        @Query("filters[genre]") genre: Int? = null,
        @Query("filters[year_range]") yearsRange: String? = null,
        @Query("sort") sort: String = "-upload_date",
        @Query("source") source: String = "adjaranet"
    ) : Call<BasicMovie>

    @GET("movies/top")
    fun getTop (
        @Query("type") type: String = "movie",
        @Query("period") period: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30,
        @Query("filters[with_actors]") actors: Int = 3,
        @Query("filters[with_directors]") directors: Int = 1,
        @Query("source") source: String = "adjaranet"
    ) : Call<Movie>

    @GET("movies/{id}/persons")
    fun getCast(
        @Path("id") id: Long
    ) : Call<Person>

    @GET("movies/{id}")
    fun getMovie(@Path("id") id: Long) : Call<MovieItemModel>

    @GET("movies/{id}/season-files/{season}?source=adjaranet")
    fun getMovieFile(@Path("id") id: Long, @Path("season") season: Int) : Call<EpisodeFiles>

    @GET("search")
    fun searchMovie(
        @Query("filters[type]") filtersType: String = "movie,cast",
        @Query("keywords") keywords: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 35,
        @Query("source") source: String = "adjaranet"
    ): Call<BasicMovie>

    @GET("genres")
    fun getGenres(
        @Query("per_page") perPage: Int = 50
    ) : Call<Genres>

    @GET("casts/{id}/movies")
    fun getMoviesByPerson(
        @Path("id") id: Long,
        @Query("per_page") perPage: Int = 150
    ) : Call<BasicMovie>

    @GET("casts/{id}")
    suspend fun getPerson(
        @Path("id") id: Long
    ) : PersonModel

    companion object {
        operator fun invoke(): APIService
        {
            val client = okhttp3.OkHttpClient.Builder()
                .addInterceptor(CustomInterceptor())
                .also { client ->
                    if (BuildConfig.DEBUG) {
                        val logging = HttpLoggingInterceptor()
                        logging.level = HttpLoggingInterceptor.Level.BODY
                        client.addInterceptor(logging)
                    }
                }

            return Retrofit.Builder()
                .client(client.build())
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(APIService::class.java)
        }
    }
}