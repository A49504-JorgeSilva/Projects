package isel.ps.services

import isel.ps.domain.GenerationRequest
import isel.ps.domain.GenerationResult

interface GenerationService {
    suspend fun generate(request: GenerationRequest): GenerationResult
}

class GenerationServiceImpl(
    private val promptRouter: PromptRouterService,
    private val llmClient: LlmClient
) : GenerationService {

    override suspend fun generate(request: GenerationRequest): GenerationResult {
        val systemPrompt = promptRouter.resolve(request.objective, request.materialType)
        val text = llmClient.generate(systemPrompt, request.content)
        return GenerationResult(text)
    }
}
