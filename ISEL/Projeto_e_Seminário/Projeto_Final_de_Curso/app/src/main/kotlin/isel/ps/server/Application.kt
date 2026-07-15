package isel.ps.server

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import isel.ps.services.AnthropicClientImpl
import isel.ps.services.FakeLlmClient
import isel.ps.services.GenerationServiceImpl
import isel.ps.services.LlmClient
import isel.ps.services.OpenAICompatibleClientImpl
import isel.ps.services.PromptRouterService
import isel.ps.webapi.geracaoRoutes
import isel.ps.webapi.opcoesRoutes
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Application")

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    val promptsDir = System.getenv("PROMPTS_DIR") ?: "prompts"
    val llmClient = createLlmClient()

    val promptRouter = PromptRouterService(promptsDir)
    val generationService = GenerationServiceImpl(promptRouter, llmClient)

    logger.info("Starting server — prompts dir: $promptsDir, provider: ${System.getenv("LLM_PROVIDER") ?: "fake"}")

    install(ContentNegotiation) { json() }

    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }

    routing {
        staticResources("/", "static") {
            default("index.html")
        }
        opcoesRoutes()
        geracaoRoutes(generationService)
    }
}

private fun createLlmClient(): LlmClient {
    val apiKey = System.getenv("LLM_API_KEY")
    return when (System.getenv("LLM_PROVIDER")) {
        "anthropic" -> AnthropicClientImpl(
            apiKey = apiKey ?: error("LLM_API_KEY is required for provider 'anthropic'"),
            model = System.getenv("LLM_MODEL") ?: "claude-opus-4-8"
        )
        "openai-compatible" -> OpenAICompatibleClientImpl(
            apiKey = apiKey ?: error("LLM_API_KEY is required for provider 'openai-compatible'"),
            baseUrl = System.getenv("LLM_BASE_URL") ?: error("LLM_BASE_URL is required for provider 'openai-compatible'"),
            model = System.getenv("LLM_MODEL") ?: error("LLM_MODEL is required for provider 'openai-compatible'")
        )
        else -> FakeLlmClient()
    }
}
