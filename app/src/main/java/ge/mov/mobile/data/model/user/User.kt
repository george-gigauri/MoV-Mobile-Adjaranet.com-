package ge.mov.mobile.data.model.user

import android.annotation.SuppressLint
import com.google.firebase.firestore.Exclude
import ge.mov.mobile.util.Role
import java.text.SimpleDateFormat
import java.util.*

data class User(
    var id: String? = null,
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var birthDate: Long? = null,
    var role: Role = Role.USER,
    var fcmToken: String? = null
) {

    @get:Exclude
    val fullName = "$firstName $lastName"

    @SuppressLint("SimpleDateFormat")
    @get:Exclude
    val birth =
        SimpleDateFormat("dd MMMM, yyyy").format(Date(birthDate ?: System.currentTimeMillis()))
}
