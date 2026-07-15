package isel.ps.webapi

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.opcoesRoutes() {
    get("/options") {
        call.respond(buildOptionsDto())
    }
}
