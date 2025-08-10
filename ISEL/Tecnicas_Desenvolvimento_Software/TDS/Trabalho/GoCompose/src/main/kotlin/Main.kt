package pt.isel.tds

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import pt.isel.tds.go.model.GameSerializer
import pt.isel.tds.storage.*
import pt.isel.tds.ttt.tds.ttt.model.Name
import pt.isel.tds.go.view.*
import pt.isel.tds.ttt.pt.isel.tds.go.view.GoApp

/**
 * The Go game in "compose desktop".
 */
fun main() = MongoDriver("TVSGO").use { driver ->
    val storage = MongoStorage<Name,_>(driver, "TVSGO", GameSerializer)
    application(exitProcessOnExit = false) {
        Window(
            onCloseRequest = {},
            title = "GO - TDS",
            resizable = false,
            state = WindowState(size = DpSize.Unspecified)
        ) {
            GoApp(onExit = ::exitApplication, storage = storage)
        }   }   }


