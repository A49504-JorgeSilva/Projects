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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class OpenAIRequest(val model: String, val messages: List<OpenAIMessage>)

@Serializable
private data class OpenAIMessage(val role: String, val content: String)

@Serializable
private data class OpenAIResponse(val choices: List<Choice>)

@Serializable
private data class Choice(val message: OpenAIMessage)

class OpenAICompatibleClientImpl(
    private val apiKey: String,
    private val baseUrl: String,
    private val model: String
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
        val httpResponse = httpClient.post("$baseUrl/chat/completions") {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(
                OpenAIRequest(
                    model = model,
                    messages = listOf(
                        OpenAIMessage(role = "system", content = systemPrompt),
                        OpenAIMessage(role = "user", content = userContent)
                    )
                )
            )
        }

        if (!httpResponse.status.isSuccess()) {
            throw LlmApiException(httpResponse.status.value, httpResponse.bodyAsText())
        }

        val response: OpenAIResponse = httpResponse.body()
        return response.choices.firstOrNull()?.message?.content
            ?: error("Empty response from LLM API")
    }
}
