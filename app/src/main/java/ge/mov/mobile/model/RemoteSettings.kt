package ge.mov.mobile.model

import com.google.gson.annotations.SerializedName

data class RemoteSettings (
    @SerializedName("is_app_enabled")
    val isAppEnabled: Boolean,
    @SerializedName("developer_info")
    val developer: DeveloperInfo?,
    val approved: List<ApprovedEmail>?
)

data class DeveloperInfo (
    val name: String,
    val email: String,
    val photo: String,
    val social: List<Social>
)

data class Social (
    val type: String,
    val icon: String,
    val url: String
)

data class ApprovedEmail (
    val email: String?
)