package ge.mov.mobile.data.repository

import ge.mov.mobile.data.model.movie.MovieModel
import ge.mov.mobile.data.network.APIService
import ge.mov.mobile.util.LanguageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val api: APIService
) {
    suspend fun getDetails(id: Int): MovieModel? = withContext(Dispatchers.IO) {
        val response = api.getMovie(id)
        val body = response.body() ?: return@withContext null
        return@withContext body.data
    }

    suspend fun getName(movie: MovieModel?) = withContext(Dispatchers.IO) {
        val language = LanguageUtil.language
        val lang_code = if (language?.id == "ka") "GEO" else "ENG"

        return@withContext if (lang_code == "GEO") {
            if (movie?.primaryName != "")
                movie?.primaryName
            else
                movie.secondaryName
        } else movie?.secondaryName
    }

    suspend fun getCountry(movie: MovieModel?) = withContext(Dispatchers.IO) {
        val language = LanguageUtil.language
        val lang_code = if (language?.id == "ka") "GEO" else "ENG"

        if (movie != null) {
            for (j in movie.countries.data) {
                if (lang_code == "GEO")
                    return@withContext j.primaryName

                if (lang_code == "ENG")
                    return@withContext j.secondaryName
            }
        }

        return@withContext movie?.primaryName
    }

    suspend fun getDescription(movie: MovieModel?) = withContext(Dispatchers.IO) {
        val language = LanguageUtil.language
        val lang_code = if (language?.id == "ka") "GEO" else "ENG"

        if (movie != null) {
            for (j in movie.plots.data) {
                if (j.language == lang_code)
                    return@withContext j.description
            }
        }

        return@withContext if (movie != null) {
            if (!movie.plots.data.isNullOrEmpty()) {
                movie.plots.data[0].description
            } else "emptyList<MovieModel>()"
        } else "emptyList<MovieModel>()"
    }

    suspend fun getCast(id: Int) = withContext(Dispatchers.IO) {
        val response = api.getCast(id = id)
        val body = response.body()

        return@withContext body?.data
    }
}