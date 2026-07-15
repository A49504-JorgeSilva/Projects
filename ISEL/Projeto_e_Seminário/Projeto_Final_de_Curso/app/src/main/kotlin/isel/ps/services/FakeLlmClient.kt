package isel.ps.services

class FakeLlmClient : LlmClient {
    override suspend fun generate(systemPrompt: String, userContent: String): String =
        "## Resposta de teste\n\nEsta é uma resposta simulada para verificar que o fluxo completo funciona.\n\n**Conteúdo recebido:** ${userContent.take(100)}…"
}
