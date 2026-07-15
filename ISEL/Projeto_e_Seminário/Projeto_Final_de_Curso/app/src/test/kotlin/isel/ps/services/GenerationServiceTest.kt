package isel.ps.services

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import isel.ps.domain.GenerationRequest
import isel.ps.domain.MaterialType
import isel.ps.domain.Objective
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GenerationServiceTest {

    private val promptRouter = mockk<PromptRouterService>()
    private val llmClient = mockk<LlmClient>()
    private val service = GenerationServiceImpl(promptRouter, llmClient)

    @Test
    fun `generate returns result with text from llm client`() = runTest {
        val request = GenerationRequest(Objective.TEACHING_MATERIAL, MaterialType.FUC, "fuc content")
        every { promptRouter.resolve(Objective.TEACHING_MATERIAL, MaterialType.FUC) } returns "system prompt"
        coEvery { llmClient.generate("system prompt", "fuc content") } returns "generated text"

        val result = service.generate(request)

        assertEquals("generated text", result.text)
    }

    @Test
    fun `generate calls promptRouter with correct objective and materialType`() = runTest {
        val request = GenerationRequest(Objective.SUMMARY, MaterialType.SLIDES, "slides content")
        every { promptRouter.resolve(Objective.SUMMARY, MaterialType.SLIDES) } returns "summary prompt"
        coEvery { llmClient.generate(any(), any()) } returns "result"

        service.generate(request)

        verify { promptRouter.resolve(Objective.SUMMARY, MaterialType.SLIDES) }
        coVerify { llmClient.generate("summary prompt", "slides content") }
    }

    @Test
    fun `generate propagates exception from promptRouter`() = runTest {
        val request = GenerationRequest(Objective.QUIZ, MaterialType.REPO, "content")
        every { promptRouter.resolve(any(), any()) } throws IllegalStateException("Invalid combination")

        assertFailsWith<IllegalStateException> {
            service.generate(request)
        }
    }

    @Test
    fun `generate propagates exception from llm client`() = runTest {
        val request = GenerationRequest(Objective.TEACHING_MATERIAL, MaterialType.FUC, "content")
        every { promptRouter.resolve(any(), any()) } returns "prompt"
        coEvery { llmClient.generate(any(), any()) } throws RuntimeException("API error")

        assertFailsWith<RuntimeException> {
            service.generate(request)
        }
    }
}
