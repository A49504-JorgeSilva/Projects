package isel.ps.domain

data class PromptMapping(
    val objective: Objective,
    val materialType: MaterialType,
    val promptFile: String
)

val PROMPT_MAPPINGS: List<PromptMapping> = listOf(
    PromptMapping(Objective.TEACHING_MATERIAL, MaterialType.FUC,    "prompt_fuc.md"),
    PromptMapping(Objective.SUMMARY,           MaterialType.SLIDES, "prompt_summary_slides.md"),
    PromptMapping(Objective.SUMMARY,           MaterialType.NOTES,  "prompt_summary_notes.md"),
    PromptMapping(Objective.SUMMARY,           MaterialType.TEST,   "prompt_summary_test.md"),
    PromptMapping(Objective.SUMMARY,           MaterialType.REPO,   "prompt_summary_repo.md"),
    PromptMapping(Objective.QUIZ,              MaterialType.SLIDES, "prompt_quiz.md"),
    PromptMapping(Objective.QUIZ,              MaterialType.NOTES,  "prompt_quiz.md"),
)
