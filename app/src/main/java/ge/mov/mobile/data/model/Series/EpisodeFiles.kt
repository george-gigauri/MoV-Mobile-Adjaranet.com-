package ge.mov.mobile.data.model.Series

import com.google.gson.annotations.SerializedName
import ge.mov.mobile.data.model.movie.Covers
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
) : Serializable {
    override fun toString(): String = title
}

data class File(
    val lang: String,
    val files: List<FileItem>,
    val subtitles: List<Subtitle>
) : Serializable {
    override fun toString(): String = lang
}

data class FileItem(
    val id: Long,
    val quality: String,
    val src: String,
    val duration: Long
) : Serializable {
    override fun toString(): String = quality
}

data class Subtitle(
    val lang: String,
    val url: String?
) : Serializable {
    override fun toString(): String = lang
}