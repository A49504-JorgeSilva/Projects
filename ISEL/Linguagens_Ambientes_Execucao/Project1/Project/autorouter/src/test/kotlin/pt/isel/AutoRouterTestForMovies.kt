package pt.isel

import pt.isel.autorouter.ArHttpRoute
import pt.isel.autorouter.ArVerb
import pt.isel.autorouter.autorouterDynamic
import pt.isel.autorouter.autorouterReflect
import kotlin.test.*

class AutoRouterTestForMovies {
    @Test fun get_movies_via_reflection() {
        get_movies(
            movieController().autorouterReflect().toList()
        )
    }

    @Test fun get_movies_via_dynamic() {
        get_movies(
                movieController().autorouterDynamic().toList()
        )
    }

    fun get_movies(routes: List<ArHttpRoute>) {
        val r = routes.first { it.path == "/movies/{movies}" }
        val res = r.handler.handle(
            mapOf("movies" to "acao"),
            emptyMap(),
            emptyMap())
        assertContentEquals(
            listOf(
                movie(9876, "Avengers", 182, 4.5),
                movie(4536, "John Wick", 122, 3.5),
                movie(5689, "Batman", 140, 4.0),
            ),
            res.get() as List<movie>)
    }

    @Test fun get_movies_with_name_containing_word_via_reflection() {
        get_movies_with_name_containing_word(
            movieController().autorouterDynamic().toList()
        )
    }

    @Test fun get_movies_with_name_containing_word_via_dynamic() {
        get_movies_with_name_containing_word(
                movieController().autorouterReflect().toList()
        )
    }

    fun get_movies_with_name_containing_word(routes: List<ArHttpRoute>) {
        val r = routes.first { it.path == "/movies/{movies}" }
        val res = r.handler.handle(
            mapOf("movies" to "acao"),
            mapOf("movie" to "Wick"),
            emptyMap())
        assertContentEquals(
            listOf(movie(4536, "John Wick", 122, 3.5)),
            res.get() as List<movie>)
    }

    @Test
    fun remove_student_via_reflection() {
        val controller = movieController()
        val routes = controller.autorouterReflect().toList()

        // Executa a rota que remove o movie
        val r = routes.first { it.path == "/movies/{movies}/movie/{id}" && it.method == ArVerb.DELETE}
        val res = r.handler.handle(
            mapOf("movies" to "acao", "id" to "4536"),
            emptyMap(),
            emptyMap()
        )
        assertEquals(movie(4536, "John Wick", 122, 3.5),res.get())
        val x = controller.repo["acao"]?.firstOrNull { it.id == 4536 }
        assertEquals(null, x)
    }

    @Test
    fun remove_student_via_dynamic() {
        val controller = movieController()
        val routes = controller.autorouterDynamic().toList()

        // Executa a rota que remove o movie
        val r = routes.first { it.path == "/movies/{movies}/movie/{id}" && it.method == ArVerb.DELETE}
        val res = r.handler.handle(
                mapOf("movies" to "acao", "id" to "4536"),
                emptyMap(),
                emptyMap()
        )
        assertEquals(movie(4536, "John Wick", 122, 3.5),res.get())
        val x = controller.repo["acao"]?.firstOrNull { it.id == 4536 }
        assertEquals(null, x)
    }

    @Test
    fun add_student_via_reflection() {
        val controller = movieController()
        val routes = controller.autorouterReflect().toList()

        // Executa a rota que adiciona o movie
        val r = routes.first { it.path == "/movies/{movies}/movie/{id}" && it.method == ArVerb.PUT}
        val movie = movie(9876, "Pinóquio", 105, 2.5)
        val res = r.handler.handle(
            mapOf("movies" to "infantil", "id" to "9876"),
            emptyMap(),
            mapOf("id" to "9876", "title" to "Pinóquio", "duration" to "105", "rating" to "2.5")
        )
        assertEquals(movie, controller.repo["infantil"]?.firstOrNull { it.id == 9876 })
    }

    @Test
    fun add_student_via_dynamic() {
        val controller = movieController()
        val routes = controller.autorouterDynamic().toList()

        // Executa a rota que adiciona o movie
        val r = routes.first { it.path == "/movies/{movies}/movie/{id}" && it.method == ArVerb.PUT}
        val movie = movie(9876, "Pinóquio", 105, 2.5)
        val res = r.handler.handle(
                mapOf("movies" to "infantil", "id" to "9876"),
                emptyMap(),
                mapOf("id" to "9876", "title" to "Pinóquio", "duration" to "105", "rating" to "2.5")
        )
        assertEquals(movie, controller.repo["infantil"]?.firstOrNull { it.id == 9876 })
    }
}