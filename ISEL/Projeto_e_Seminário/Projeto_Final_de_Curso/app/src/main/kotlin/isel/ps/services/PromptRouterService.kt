package isel.ps.services

import isel.ps.domain.MaterialType
import isel.ps.domain.Objective
import isel.ps.domain.PROMPT_MAPPINGS
import java.io.File

class PromptRouterService(private val promptsDir: String) {

    fun resolve(objective: Objective, materialType: MaterialType): String {
        val mapping = PROMPT_MAPPINGS.find { it.objective == objective && it.materialType == materialType }
            ?: error("Invalid combination: $objective + $materialType")

        val file = File(promptsDir, mapping.promptFile)
        check(file.exists()) { "Prompt file not found: ${mapping.promptFile}" }

        return file.readText()
    }
}
