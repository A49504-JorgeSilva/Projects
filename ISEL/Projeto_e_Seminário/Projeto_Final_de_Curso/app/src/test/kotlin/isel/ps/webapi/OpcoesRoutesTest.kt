package isel.ps.webapi

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import isel.ps.domain.PROMPT_MAPPINGS
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class OpcoesRoutesTest {

    @Test
    fun `GET options returns 200`() = testApplication {
        install(ContentNegotiation) { json() }
        routing { opcoesRoutes() }

        val response = client.get("/options")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET options returns all valid combinations`() = testApplication {
        install(ContentNegotiation) { json() }
        routing { opcoesRoutes() }

        val response = client.get("/options")
        val body = Json.decodeFromString<OptionsDto>(response.bodyAsText())
        assertEquals(PROMPT_MAPPINGS.size, body.options.size)
    }

    @Test
    fun `GET options entries contain objective and materialType`() = testApplication {
        install(ContentNegotiation) { json() }
        routing { opcoesRoutes() }

        val response = client.get("/options")
        val body = Json.decodeFromString<OptionsDto>(response.bodyAsText())
        body.options.forEach { entry ->
            assert(entry.objective.isNotBlank())
            assert(entry.materialType.isNotBlank())
        }
    }
}
