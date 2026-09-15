package mg.iray.app.auth

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthSource(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthSource {

    override fun currentUserId(): String? = auth.currentUser?.uid

    override fun isAnonymous(): Boolean = auth.currentUser?.isAnonymous ?: true

    override suspend fun signInAnonymously(): String {
        val result = auth.signInAnonymously().await()
        return result.user?.uid ?: error("signInAnonymously() returned no user")
    }

    override suspend fun linkWithEmail(email: String, password: String): String {
        val user = auth.currentUser ?: error("Link requires a signed-in user")
        val credential = EmailAuthProvider.getCredential(email, password)
        return user.linkWithCredential(credential).await().user?.uid
            ?: error("linkWithCredential() returned no user")
    }
}