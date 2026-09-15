package mg.iray.app.service

import kotlinx.coroutines.test.runTest
import mg.iray.app.sync.RemoteDocument
import mg.iray.app.sync.RestRemoteSync
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RestRemoteSyncTest {

    private lateinit var server: MockWebServer
    private lateinit var api: FirestoreApiService

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        api = ApiClient.firestoreApi(baseUrl = server.url("/v1/").toString())
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `rest remote sync pousse un document avec le token firebase`() = runTest {
        server.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        val sync = RestRemoteSync(api, "p") { "Bearer tok-123" }
        sync.set("users/uid1/tasks", "t1", mapOf("title" to "X", "updatedAt" to 1L))

        val request = server.takeRequest()
        assertEquals("Bearer tok-123", request.getHeader("Authorization"))
    }

    @Test
    fun `rest remote sync liste et remonte les documents utilises par le pull`() = runTest {
        server.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """
                {
                  "documents": [
                    {
                      "name": "projects/p/databases/(default)/documents/users/uid1/tasks/t1",
                      "fields": {
                        "title": {"stringValue": "Distant"},
                        "isDone": {"booleanValue": true},
                        "updatedAt": {"integerValue": "99"}
                      }
                    }
                  ]
                }
                """.trimIndent()
            )
        )

        val sync = RestRemoteSync(api, "p") { null }
        val docs: List<RemoteDocument> = sync.list("users/uid1/tasks")

        assertEquals(1, docs.size)
        assertEquals("t1", docs[0].id)
        assertEquals("Distant", docs[0].data["title"])
        assertEquals(true, docs[0].data["isDone"])
        assertEquals(99L, docs[0].updatedAt)
    }

    @Test
    fun `rest remote sync supprime un document`() = runTest {
        server.enqueue(MockResponse().setResponseCode(200))

        val sync = RestRemoteSync(api, "p") { null }
        sync.delete("users/uid1/tasks", "t1")

        val request = server.takeRequest()
        assertEquals("DELETE", request.method)
        assertTrue(request.path!!.contains("/databases/(default)/documents/users/uid1/tasks/t1"))
    }
}