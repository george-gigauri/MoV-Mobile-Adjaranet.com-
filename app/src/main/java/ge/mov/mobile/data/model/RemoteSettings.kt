package ge.mov.mobile.data.model

import com.google.gson.annotations.SerializedName

data class RemoteSettings (
//    val id: Int = 0,
//    val name: String,
//    val remoteVersion: Int = 0,
//    val playStoreUrl: String?,
//    val developer: Int,
//    val published: Boolean = false
    @SerializedName("is_app_enabled")
    val isAppEnabled: Boolean,
    @SerializedName("app_build_version")
    val remoteVersionCode: Int,
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