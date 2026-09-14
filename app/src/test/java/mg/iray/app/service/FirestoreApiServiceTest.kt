package mg.iray.app.service

import kotlinx.coroutines.test.runTest
import mg.iray.app.service.dto.FirestoreWriteRequest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FirestoreApiServiceTest {

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
    fun `set encode les champs Firestore et envoie l updateMask`() = runTest {
        server.enqueue(
            MockResponse().setResponseCode(200)
                .setBody("""{"name":"projects/p/databases/(default)/documents/users/u1/tasks/t1"}""")
        )

        val data = mapOf("title" to "Acheter du riz", "isDone" to true, "updatedAt" to 42L)
        api.set(
            "p", "users/u1/tasks", "t1",
            FirestoreWriteRequest(FirestoreFieldCodec.toFields(data)),
            data.keys.toList(),
            "Bearer tok"
        )

        val request = server.takeRequest()
        assertEquals("PATCH", request.method)
        assertTrue("path was: ${request.path}", request.path!!.contains("/v1/projects/p/databases/(default)/documents/users/u1/tasks/t1"))
        assertTrue("path was: ${request.path}", request.path!!.contains("updateMask.fieldPaths=title"))
        assertTrue("path was: ${request.path}", request.path!!.contains("updateMask.fieldPaths=isDone"))
        assertEquals("Bearer tok", request.getHeader("Authorization"))

        val body = request.body.readUtf8()
        assertTrue(body.contains("\"stringValue\":\"Acheter du riz\""))
        assertTrue(body.contains("\"booleanValue\":true"))
        assertTrue(body.contains("\"integerValue\":\"42\""))
    }

    @Test
    fun `list parse les documents et retablit les types`() = runTest {
        server.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """
                {
                  "documents": [
                    {
                      "name": "projects/p/databases/(default)/documents/users/u1/tasks/t1",
                      "fields": {
                        "title": {"stringValue": "Bonjour"},
                        "isDone": {"booleanValue": true},
                        "updatedAt": {"integerValue": "42"},
                        "tags": {"arrayValue": {"values": [{"stringValue": "a"}, {"stringValue": "b"}]}}
                      }
                    }
                  ]
                }
                """.trimIndent()
            )
        )

        val response = api.list("p", "users/u1/tasks", pageSize = 50, auth = null)
        val doc = response.documents!!.single()
        val data = FirestoreFieldCodec.toData(doc.fields)

        assertEquals("t1", doc.name.substringAfterLast('/'))
        assertEquals("Bonjour", data["title"])
        assertEquals(true, data["isDone"])
        assertEquals(42L, data["updatedAt"])
        assertEquals(listOf("a", "b"), data["tags"])
    }

    @Test
    fun `delete envoie une requete DELETE`() = runTest {
        server.enqueue(MockResponse().setResponseCode(200))

        val response = api.delete("p", "users/u1/tasks", "t1", "Bearer tok")
        val request = server.takeRequest()

        assertTrue("status=${response.code()} success=${response.isSuccessful}", response.isSuccessful)
        assertEquals("DELETE", request.method)
        assertTrue("path was: ${request.path}", request.path!!.contains("users/u1/tasks/t1"))
        assertEquals("Bearer tok", request.getHeader("Authorization"))
    }
}