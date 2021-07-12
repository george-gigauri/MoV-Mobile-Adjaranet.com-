package ge.mov.mobile.data.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ge.mov.mobile.data.model.user.User
import ge.mov.mobile.util.Constants.Firebase.USERS_COLLECTION
import ge.mov.mobile.util.Role
import ge.mov.mobile.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepository @Inject constructor() {

    fun login(email: String, password: String) = flow {
        emit(State.loading())

        val result = Firebase.auth.signInWithEmailAndPassword(email, password).await()

        emit(State.success("LOGIN_SUCCESS"))
    }.catch { emit(State.failure(it.message)) }.flowOn(Dispatchers.IO)

    fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        birthDate: Long,
        role: Role
    ) = flow {
        emit(State.loading())

        val result = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
        createUserDatabase(result.user!!.uid, email, firstName, lastName, birthDate, role)

        emit(State.success())
    }.catch { emit(State.failure(it.message)) }.flowOn(Dispatchers.IO)

    fun sendResetPassword(email: String) = flow {
        emit(State.loading())

        Firebase.auth.sendPasswordResetEmail(email).await()

        emit(State.success("SIGN_UP_SUCCESS"))
    }.catch { emit(State.failure(it.message)) }.flowOn(Dispatchers.IO)

    private suspend fun createUserDatabase(
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
            .await()
    }
}