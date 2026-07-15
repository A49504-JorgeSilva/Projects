package isel.ps.webapi

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.readRemaining
import isel.ps.domain.GenerationRequest
import isel.ps.domain.MaterialType
import isel.ps.domain.Objective
import isel.ps.services.GenerationService
import isel.ps.services.LlmApiException
import kotlinx.io.readByteArray
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper

fun Route.geracaoRoutes(generationService: GenerationService) {
    post("/generate") {
        var objective: Objective? = null
        var materialType: MaterialType? = null
        var content: String? = null

        call.receiveMultipart().forEachPart { part ->
            when {
                part is PartData.FormItem && part.name == "objective" ->
                    objective = runCatching { Objective.valueOf(part.value) }.getOrNull()
                part is PartData.FormItem && part.name == "materialType" ->
                    materialType = runCatching { MaterialType.valueOf(part.value) }.getOrNull()
                part is PartData.FormItem && part.name == "content" ->
                    content = part.value
                part is PartData.FileItem && content == null -> {
                    val bytes = part.provider().readRemaining().readByteArray()
                    val filename = part.originalFileName ?: ""
                    content = if (filename.endsWith(".pdf", ignoreCase = true)) {
                        Loader.loadPDF(bytes).use { doc ->
                            PDFTextStripper().getText(doc)
                        }
                    } else {
                        bytes.toString(Charsets.UTF_8)
                    }
                }
            }
            part.dispose()
        }

        if (objective == null || materialType == null || content == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing or invalid fields: objective, materialType, content")
            return@post
        }

        val result = try {
            generationService.generate(GenerationRequest(objective!!, materialType!!, content!!))
        } catch (e: LlmApiException) {
            val message = when (e.statusCode) {
                401 -> "Chave de API inválida. Verifica a variável LLM_API_KEY."
                402 -> "Saldo insuficiente. Adiciona créditos na plataforma do provider."
                429 -> "Limite de pedidos excedido. Aguarda um momento e tenta novamente."
                503 -> "Serviço temporariamente indisponível. Tenta novamente mais tarde."
                else -> "Erro inesperado da API (código ${e.statusCode}). Tenta novamente."
            }
            call.respond(HttpStatusCode.BadGateway, message)
            return@post
        } catch (e: Exception) {
            val message = if (e.message?.contains("timeout", ignoreCase = true) == true)
                "O pedido demorou demasiado tempo. O documento pode ser demasiado grande ou o serviço está lento. Tenta novamente."
            else
                "Erro inesperado ao comunicar com a API. Tenta novamente."
            call.respond(HttpStatusCode.BadGateway, message)
            return@post
        }
        call.respond(GenerationResultDto(result.text))
    }
}
