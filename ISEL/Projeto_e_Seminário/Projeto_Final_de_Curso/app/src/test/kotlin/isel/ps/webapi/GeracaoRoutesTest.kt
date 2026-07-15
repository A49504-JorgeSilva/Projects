package isel.ps.webapi

import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockk
import isel.ps.domain.GenerationRequest
import isel.ps.domain.GenerationResult
import isel.ps.domain.MaterialType
import isel.ps.domain.Objective
import isel.ps.services.GenerationService
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class GeracaoRoutesTest {

    private val mockService = mockk<GenerationService>()

    private fun ApplicationTestBuilder.setup() {
        install(ContentNegotiation) { json() }
        routing { geracaoRoutes(mockService) }
    }

    private fun multipartOf(vararg fields: Pair<String, String>) = formData {
        fields.forEach { (key, value) -> append(key, value) }
    }

    @Test
    fun `POST generate with valid file returns 200 and result`() = testApplication {
        setup()
        coEvery { mockService.generate(any()) } returns GenerationResult("generated text")

        val response = client.submitFormWithBinaryData(
            url = "/generate",
            formData = formData {
                append("objective", Objective.TEACHING_MATERIAL.name)
                append("materialType", MaterialType.FUC.name)
                append("content", "fuc file content".toByteArray(), Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=fuc.txt")
                })
            }
        )

        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.decodeFromString<GenerationResultDto>(response.bodyAsText())
        assertEquals("generated text", body.text)
    }

    @Test
    fun `POST generate with content as form field returns 200`() = testApplication {
        setup()
        coEvery { mockService.generate(any()) } returns GenerationResult("repo summary")

        val response = client.submitFormWithBinaryData(
            url = "/generate",
            formData = multipartOf(
                "objective" to Objective.SUMMARY.name,
                "materialType" to MaterialType.REPO.name,
                "content" to "https://github.com/example/repo"
            )
        )

        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.decodeFromString<GenerationResultDto>(response.bodyAsText())
        assertEquals("repo summary", body.text)
    }

    @Test
    fun `POST generate with missing objective returns 400`() = testApplication {
        setup()

        val response = client.submitFormWithBinaryData(
            url = "/generate",
            formData = multipartOf(
                "materialType" to MaterialType.FUC.name,
                "content" to "some content"
            )
        )

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `POST generate with invalid objective returns 400`() = testApplication {
        setup()

        val response = client.submitFormWithBinaryData(
            url = "/generate",
            formData = multipartOf(
                "objective" to "INVALID",
                "materialType" to MaterialType.FUC.name,
                "content" to "some content"
            )
        )

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `POST generate with missing content returns 400`() = testApplication {
        setup()

        val response = client.submitFormWithBinaryData(
            url = "/generate",
            formData = multipartOf(
                "objective" to Objective.SUMMARY.name,
                "materialType" to MaterialType.SLIDES.name
            )
        )

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `POST generate passes correct request to service`() = testApplication {
        setup()
        val capturedRequest = mutableListOf<GenerationRequest>()
        coEvery { mockService.generate(capture(capturedRequest)) } returns GenerationResult("result")

        client.submitFormWithBinaryData(
            url = "/generate",
            formData = multipartOf(
                "objective" to Objective.QUIZ.name,
                "materialType" to MaterialType.NOTES.name,
                "content" to "notes content"
            )
        )

        assertEquals(1, capturedRequest.size)
        assertEquals(Objective.QUIZ, capturedRequest[0].objective)
        assertEquals(MaterialType.NOTES, capturedRequest[0].materialType)
        assertEquals("notes content", capturedRequest[0].content)
    }
}
