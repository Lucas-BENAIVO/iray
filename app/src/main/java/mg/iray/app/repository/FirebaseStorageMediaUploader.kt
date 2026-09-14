package mg.iray.app.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import mg.iray.app.entity.MediaEntity

class FirebaseStorageMediaUploader(
    private val storage: FirebaseStorage,
    private val uid: () -> String
) : MediaUploader {

    override suspend fun upload(media: MediaEntity): String {
        val ref = storage.reference.child("users/${uid()}/media/${media.id}")
        ref.putFile(Uri.parse(media.localUri)).await()
        return ref.downloadUrl.await().toString()
    }
}