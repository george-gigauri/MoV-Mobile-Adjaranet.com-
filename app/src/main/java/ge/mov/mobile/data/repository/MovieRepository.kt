package ge.mov.mobile.data.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ge.mov.mobile.data.model.movie.MovieModel
import ge.mov.mobile.data.network.APIService
import ge.mov.mobile.util.LanguageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val api: APIService
) {
    private val fUser = Firebase.auth.currentUser

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

    suspend fun saveMovie(id: Int, adjaraId: Int) = withContext(Dispatchers.IO) {
        if (fUser != null) {
            Firebase.firestore.collection("users").document(fUser.uid)
                .collection("saved")
                .document(id.toString())
                .set(mapOf("id" to id, "adjaraId" to adjaraId))
                .await()
        }
    }

    suspend fun unsaveMovie(id: Int, adjaraId: Int) = withContext(Dispatchers.IO) {
        if (fUser != null)
            Firebase.firestore.collection("users").document(fUser.uid)
                .collection("saved")
                .document(id.toString())
                .delete()
                .await()
    }

    suspend fun isMovieSavedToFirebase(id: Int, adjaraId: Int): Boolean =
        withContext(Dispatchers.IO) {
            return@withContext if (fUser == null) false
            else {
                Firebase.firestore.collection("users")
                    .document(fUser.uid)
                    .collection("saved")
                    .document(id.toString())
                    .get()
                    .await()
                    .exists()
            }
        }
}