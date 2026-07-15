package isel.ps.services

interface LlmClient {
    suspend fun generate(systemPrompt: String, userContent: String): String
}

class LlmApiException(val statusCode: Int, message: String) : Exception(message)
