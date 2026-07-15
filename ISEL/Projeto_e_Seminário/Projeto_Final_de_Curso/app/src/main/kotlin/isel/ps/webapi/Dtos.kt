package isel.ps.webapi

import isel.ps.domain.MaterialType
import isel.ps.domain.Objective
import isel.ps.domain.PROMPT_MAPPINGS
import kotlinx.serialization.Serializable

@Serializable
data class GenerationResultDto(val text: String)

@Serializable
data class OptionsDto(val options: List<OptionEntryDto>)

@Serializable
data class OptionEntryDto(val objective: String, val materialType: String)

fun buildOptionsDto(): OptionsDto =
    OptionsDto(PROMPT_MAPPINGS.map { OptionEntryDto(it.objective.name, it.materialType.name) })
