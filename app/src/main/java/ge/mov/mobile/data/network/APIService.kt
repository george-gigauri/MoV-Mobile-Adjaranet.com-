package ge.mov.mobile.data.network

import ge.mov.mobile.data.model.LanguageData
import ge.mov.mobile.data.model.MovieFileUrl
import ge.mov.mobile.data.model.MovieReminder
import ge.mov.mobile.data.model.Series.EpisodeFiles
import ge.mov.mobile.data.model.Series.Person
import ge.mov.mobile.data.model.Series.PersonModel
import ge.mov.mobile.data.model.basic.BasicMovie
import ge.mov.mobile.data.model.featured.Featured
import ge.mov.mobile.data.model.movie.Genres
import ge.mov.mobile.data.model.movie.MovieItemModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
    @GET("movies/featured?source=adjaranet")
    suspend fun getFeatured(): Response<Featured>

    @GET("movies")
    suspend fun getMovies(
        @Query("page") page: Int,
        @Query("per_page") per_page: Int = 18,
        @Query("filters[language]") language: String = "GEO",
        @Query("filters[type]") type: String = "movie", // adjaranet
        @Query("filters[only_public]") public: String = "yes",
        @Query("filters[with_actors]") actors: Int = 3,
        @Query("filters[with_directors]") directors: Int = 1,
        @Query("filters[with_files]") files: String = "yes",
        @Query("filters[genre]") genre: String? = null,
        @Query("filters[year_range]") yearsRange: String? = null,
        @Query("sort") sort: String = "-upload_date",
        @Query("source") source: String = "adjaranet"
    ) : Response<BasicMovie>

    @GET("movies/{id}/related")
    suspend fun getSimilarMovies(
        @Path("id") id: Int,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 12,
        @Query("source") source: String = "adjaranet"
    ) : Response<BasicMovie>

    @GET("movies/top")
    suspend fun getTop (
        @Query("type") type: String = "movie",
        @Query("period") period: String = "day",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 18,
        @Query("filters[genre]") genre: String? = null,
        @Query("filters[with_actors]") actors: Int = 3,
        @Query("filters[with_directors]") directors: Int = 1,
        @Query("source") source: String = "adjaranet"
    ) : Response<BasicMovie>

    @GET("movies/{id}/persons")
    suspend fun getCast(
        @Path("id") id: Int,
        @Query("filters[role]") role: String? = "cast"
    ) : Response<Person>

    @GET("movies/{id}")
    suspend fun getMovie(@Path("id") id: Int) : Response<MovieItemModel>

    @GET("movies/{id}/season-files/{season}?source=adjaranet")
    suspend fun getMovieFile(@Path("id") id: Int, @Path("season") season: Int) : Response<EpisodeFiles>

    @GET("search")
    suspend fun searchMovie(
        @Query("filters[type]") filtersType: String = "movie",
        @Query("keywords") keywords: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 35,
        @Query("source") source: String = "adjaranet"
    ): Response<BasicMovie>

    @GET("genres")
    suspend fun getGenres(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 50
    ) : Response<Genres>

    @GET("casts/{id}/movies")
    suspend fun getMoviesByPerson(
        @Path("id") id: Long,
        @Query("per_page") perPage: Int = 250
    ): Response<BasicMovie>

    @GET("languages")
    suspend fun getLanguages(): Response<LanguageData>

    @GET("casts/{id}")
    suspend fun getPerson(
        @Path("id") id: Long
    ): Response<PersonModel>

    @GET("movies/latest-episodes?source=adjaranet")
    suspend fun getLatest(): Response<MovieReminder>

    @Headers(
        "x-source: adjaranet",
        "sec-ch-ua: \" Not A;Brand\";v=\"99\", \"Chromium\";v=\"90\", \"Microsoft Edge\";v=\"90\""
    )
    @GET("movies/{movie_id}/files/{file_id}?source=adjaranet&rd=0")
    suspend fun getMoviePlayUrl(
        @Path("movie_id") id: Int,
        @Path("file_id") fileId: Long
    ): Response<MovieFileUrl>
}