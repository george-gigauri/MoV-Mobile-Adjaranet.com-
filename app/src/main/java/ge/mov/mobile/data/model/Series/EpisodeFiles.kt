package ge.mov.mobile.data.model.Series

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import ge.mov.mobile.data.model.movie.Covers
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class EpisodeFiles(
    val data: List<Episode>
) : Serializable

data class Episode(
    val episode: Int,
    @SerializedName("episodes_include")
    val episodesInclude: String,
    val title: String,
    val description: String,
    val rating: Double,
    val poster: String,
    val covers: Covers,
    val files: List<File>
): Serializable

data class File(
    val lang: String,
    val files: List<FileItem>,
    val subtitles: List<Subtitle>
): Serializable

data class FileItem(
    val id: Long,
    val quality: String,
    val src: String,
    val duration: Long
):  Serializable

data class Subtitle (
    val lang: String,
    val url: String?
):  Serializable