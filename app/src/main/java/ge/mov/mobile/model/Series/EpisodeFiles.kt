package ge.mov.mobile.model.Series

import com.google.gson.annotations.SerializedName
import ge.mov.mobile.model.movie.Covers

data class EpisodeFiles(
    val data: List<Episode>
)

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
)

data class File(
    val lang: String,
    val files: List<FileItem>
)

data class FileItem(
    val id: Long,
    val quality: String,
    val src: String,
    val duration: Long
)