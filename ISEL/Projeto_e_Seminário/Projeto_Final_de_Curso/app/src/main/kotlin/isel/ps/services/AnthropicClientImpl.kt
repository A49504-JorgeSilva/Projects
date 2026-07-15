package isel.ps.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class AnthropicRequest(
    val model: String,
    @SerialName("max_tokens") val maxTokens: Int,
    val system: String,
    val messages: List<AnthropicMessage>
)

@Serializable
private data class AnthropicMessage(val role: String, val content: String)

@Serializable
private data class AnthropicResponse(val content: List<ContentBlock>)

@Serializable
private data class ContentBlock(val type: String, val text: String = "")

class AnthropicClientImpl(
    private val apiKey: String,
    private val model: String = "claude-opus-4-8"
) : LlmClient {

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
        }
    }

    override suspend fun generate(systemPrompt: String, userContent: String): String {
        val httpResponse = httpClient.post("https://api.anthropic.com/v1/messages") {
            header("x-api-key", apiKey)
            header("anthropic-version", "2023-06-01")
            contentType(ContentType.Application.Json)
            setBody(
                AnthropicRequest(
                    model = model,
                    maxTokens = 8096,
                    system = systemPrompt,
                    messages = listOf(AnthropicMessage(role = "user", content = userContent))
                )
            )
        }

        if (!httpResponse.status.isSuccess()) {
            throw LlmApiException(httpResponse.status.value, httpResponse.bodyAsText())
        }

        val response: AnthropicResponse = httpResponse.body()
        return response.content
            .filter { it.type == "text" }
            .joinToString("") { it.text }
    }
}
