package ge.mov.mobile.data.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ge.mov.mobile.data.model.user.User
import ge.mov.mobile.util.Constants.Firebase.USERS_COLLECTION
import ge.mov.mobile.util.Role
import ge.mov.mobile.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepository @Inject constructor() {

    val state = MutableLiveData(State.empty())

    suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        state.postValue(State.loading())

        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { state.postValue(State.success("LOGIN_SUCCESS")) }
            .addOnFailureListener { state.postValue(State.failure(it.message)) }
    }

    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        birthDate: Long,
        role: Role
    ) = withContext(Dispatchers.IO) {
        state.postValue(State.loading())

        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                createUserDatabase(it.user!!.uid, email, firstName, lastName, birthDate, role)
            }.addOnFailureListener {
                state.postValue(State.failure(it.message))
            }
    }

    suspend fun sendResetPassword(email: String) = withContext(Dispatchers.IO) {
        state.postValue(State.loading())

        Firebase.auth.sendPasswordResetEmail(email).addOnSuccessListener {
            state.postValue(State.success("SIGN_UP_SUCCESS"))
        }.addOnFailureListener { state.postValue(State.failure(it.message)) }
    }

    private fun createUserDatabase(
        id: String,
        email: String,
        firstName: String,
        lastName: String,
        birthDate: Long,
        role: Role
    ) {
        val user = User().apply {
            this.id = id
            this.email = email
            this.firstName = firstName
            this.lastName = lastName
            this.birthDate = birthDate
            this.role = role
        }

        Firebase.firestore.collection(USERS_COLLECTION)
            .document(id)
            .set(user)
            .addOnSuccessListener { state.postValue(State.success("SIGN_UP_SUCCESS")) }
            .addOnFailureListener { state.postValue(State.failure(it.message)) }
    }
}