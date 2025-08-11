package pt.isel

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import pt.isel.autorouter.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.stream.Stream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JsonServerTestForMovies {

    private var server: JsonServer? = null
    private val mapper = ObjectMapper()


    fun makeJsonServer(): Stream<JsonServer> = Stream.of(
        movieController().autorouterReflect().jsonServer(),
    )

    fun setup(jsonServer: JsonServer) = runBlocking {
        suspendCoroutine { cont ->
            server = jsonServer.apply {
                javalin().events {
                    it.serverStarted { cont.resume(Unit) }
                    it.serverStartFailed { cont.resume(Unit) }
                }
                start(4000)
            }
        }
    }

    fun teardown() = runBlocking {
        suspendCoroutine { cont ->
            server?.javalin()?.events {
                it.serverStopped { cont.resume(Unit) }
                it.serverStopFailed { cont.resume(Unit) }
            }
            server?.close()
            server = null
        }
    }

    fun runJsonServer(jsonServer: JsonServer, block: () -> Unit) {
        try {
            setup(jsonServer)
            block()
        } finally {
            teardown()
        }
    }

    @ParameterizedTest
    @MethodSource("makeJsonServer")
    fun get_all_movies(jsonServer: JsonServer) = runJsonServer(jsonServer) {
        val json = URL("http://localhost:4000/movies/acao")
            .openStream()
            .bufferedReader()
            .readText()
        val actual = mapper.readValue(json, object : TypeReference<List<movie>>() {})
        assertContentEquals(
            listOf(
                movie(9876, "Avengers", 182, 4.5),
                movie(4536, "John Wick", 122, 3.5),
                movie(5689, "Batman", 140, 4.0),
            ),
            actual
        )
    }

    @ParameterizedTest
    @MethodSource("makeJsonServer")
    fun get_movies_with_name_containing_word(jsonServer: JsonServer) = runJsonServer(jsonServer) {
        val json = URL("http://localhost:4000/movies/acao?movie=wick")
            .openStream()
            .bufferedReader()
            .readText()
        val actual = mapper.readValue(json, object : TypeReference<List<movie>>() {})
        assertContentEquals(
            listOf(movie(4536, "John Wick", 122, 3.5)),
            actual
        )
    }

    @ParameterizedTest
    @MethodSource("makeJsonServer")
    fun insert_movie_in_classroom(jsonServer: JsonServer) = runJsonServer(jsonServer) {
        val json = URL("http://localhost:4000/movies/acao/movie/7777")
            .put("""{"id": "7777", "title":"Transformers","duration":"143", "rating":"4.7"}""")
        val actual = mapper.readValue(json, movie::class.java)
        assertEquals(
            movie(7777, "Transformers", 143, 4.7),
            actual)
    }

    @ParameterizedTest
    @MethodSource("makeJsonServer")
    fun remove_student_from_classroom(jsonServer: JsonServer) = runJsonServer(jsonServer) {
        val json = URL("http://localhost:4000/movies/acao/movie/4536")
            .delete()
        val actual = mapper.readValue(json, movie::class.java)
        assertEquals(
            movie(4536, "John Wick", 122, 3.5),
            actual)
    }

    fun URL.put(json: String): String = (this.openConnection() as HttpURLConnection).run {
        requestMethod = "PUT"
        doOutput = true
        val out = json.toByteArray(StandardCharsets.UTF_8)
        val length = out.size
        setFixedLengthStreamingMode(length)
        setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        connect()
        outputStream.use { os ->
            os.write(out)
            this.inputStream.bufferedReader().readText()
        }
    }

    fun URL.delete(): String = (this.openConnection() as HttpURLConnection).run {
        requestMethod = "DELETE"
        doOutput = true
        connect()
        this.inputStream.bufferedReader().readText()
    }
}