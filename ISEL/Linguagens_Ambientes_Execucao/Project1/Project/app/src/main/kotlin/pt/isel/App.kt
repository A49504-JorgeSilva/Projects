package pt.isel

import pt.isel.autorouter.autorouterDynamic
import pt.isel.autorouter.autorouterReflect
import pt.isel.autorouter.jsonServer

fun main() {
    ClassroomController().autorouterReflect().jsonServer().start(4000)
    //ClassroomController().autorouterDynamic().jsonServer().start(4000)
}
