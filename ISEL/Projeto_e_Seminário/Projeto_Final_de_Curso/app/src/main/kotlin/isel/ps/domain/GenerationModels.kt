package isel.ps.domain

data class GenerationRequest(
    val objective: Objective,
    val materialType: MaterialType,
    val content: String
)

data class GenerationResult(
    val text: String
)
