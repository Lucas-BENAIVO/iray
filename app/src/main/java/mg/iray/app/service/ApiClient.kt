package mg.iray.app.service

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    const val FIRESTORE_BASE_URL = "https://firestore.googleapis.com/v1/"

    fun firestoreApi(
        baseUrl: String = FIRESTORE_BASE_URL,
        client: OkHttpClient = defaultClient()
    ): FirestoreApiService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FirestoreApiService::class.java)

    fun projectId(): String =
        FirebaseApp.getInstance().options.projectId.orEmpty()

    suspend fun firebaseToken(): String? =
        FirebaseAuth.getInstance().currentUser
            ?.getIdToken(false)
            ?.await()
            ?.token

    fun defaultClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .build()
}