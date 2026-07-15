package isel.ps.services

import isel.ps.domain.MaterialType
import isel.ps.domain.Objective
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.api.assertThrows
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class PromptRouterServiceTest {

    @TempDir
    lateinit var promptsDir: File

    @Test
    fun `resolve returns prompt content for valid combination`() {
        promptsDir.resolve("prompt_fuc.md").writeText("fuc prompt content")
        val service = PromptRouterService(promptsDir.absolutePath)

        val result = service.resolve(Objective.TEACHING_MATERIAL, MaterialType.FUC)

        assertEquals("fuc prompt content", result)
    }

    @Test
    fun `resolve throws for invalid combination`() {
        val service = PromptRouterService(promptsDir.absolutePath)

        assertThrows<IllegalStateException> {
            service.resolve(Objective.QUIZ, MaterialType.REPO)
        }
    }

    @Test
    fun `resolve throws when prompt file is missing from disk`() {
        val service = PromptRouterService(promptsDir.absolutePath)

        assertThrows<IllegalStateException> {
            service.resolve(Objective.TEACHING_MATERIAL, MaterialType.FUC)
        }
    }
}
