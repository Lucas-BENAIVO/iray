package mg.iray.app.service

import mg.iray.app.service.dto.FirestoreDocument
import mg.iray.app.service.dto.FirestoreListResponse
import mg.iray.app.service.dto.FirestoreWriteRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface FirestoreApiService {

    @GET("projects/{project}/databases/(default)/documents/{collection}")
    suspend fun list(
        @Path("project") project: String,
        @Path(value = "collection", encoded = true) collection: String,
        @Query("pageSize") pageSize: Int? = null,
        @Header("Authorization") auth: String?
    ): FirestoreListResponse

    @GET("projects/{project}/databases/(default)/documents/{collection}/{document}")
    suspend fun get(
        @Path("project") project: String,
        @Path(value = "collection", encoded = true) collection: String,
        @Path(value = "document", encoded = true) document: String,
        @Header("Authorization") auth: String?
    ): FirestoreDocument

    @PATCH("projects/{project}/databases/(default)/documents/{collection}/{document}")
    suspend fun set(
        @Path("project") project: String,
        @Path(value = "collection", encoded = true) collection: String,
        @Path(value = "document", encoded = true) document: String,
        @Body body: FirestoreWriteRequest,
        @Query("updateMask.fieldPaths") fieldPaths: List<String>? = null,
        @Header("Authorization") auth: String?
    ): FirestoreDocument

    @DELETE("projects/{project}/databases/(default)/documents/{collection}/{document}")
    suspend fun delete(
        @Path("project") project: String,
        @Path(value = "collection", encoded = true) collection: String,
        @Path(value = "document", encoded = true) document: String,
        @Header("Authorization") auth: String?
    ): Response<Unit>
}