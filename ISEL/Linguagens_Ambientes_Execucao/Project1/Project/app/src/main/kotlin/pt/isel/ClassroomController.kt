package pt.isel

import io.javalin.websocket.executor
import pt.isel.autorouter.*
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.TimeUnit

class ClassroomController {

    val repo = mutableMapOf(
        "i41d" to listOf(
            Student(7236, "Jonas Mancas Lubri", 56, 4),
            Student(8634, "Maria Calma Paz", 23, 5),
            Student(4485, "Jose Jose Relax", 56, 4),
            Student(5362, "Antony Batata Beterraba", 23, 3),
            Student(2356, "Jusy Mary Saby", 23, 3),
            Student(5342, "Rambo Golias a Monte", 65, 2),
            Student(8695, "Juanita Sabri", 65, 1),
            Student(4862, "Bainha Balizas", 65, 4),
        ),
        "i42d" to listOf(
            Student(9876, "Ole Super", 7, 5),
            Student(4536, "Isel Maior", 7, 5),
            Student(5689, "Ever Sad", 7, 3),
        )
    )

    /**
     * Example:
     *   http://localhost:4000/classroom/i42d?student=jo
     */
    @Synchronized
    @AutoRoute(path = "/classroom/{classroom}")
    fun search(@ArRoute classroom: String, @ArQuery student: String?): Optional<List<Student>> {
        return repo[classroom]
            ?.let {
                if(student == null) Optional.of(it)
                else Optional.of(it.filter { st -> st.name.lowercase().contains(student.lowercase()) })
            }
            ?: Optional.empty()
    }

    /**
     * Example:
     *   curl --header "Content-Type: application/json" \
     *     --request PUT \
     *     --data '{"nr": "7777", "name":"Ze Gato","group":"11", "semester":"3"}' \
     *     http://localhost:4000/classroom/i42d/students/7777
     */
    @Synchronized
    @AutoRoute(path = "/classroom/{classroom}/students/{nr}", method = ArVerb.PUT)
    fun addStudent(
        @ArRoute classroom: String,
        @ArRoute nr: Int,
        @ArBody s: Student
    ): Optional<Student> {
        if(nr != s.nr) return Optional.empty() // return 409 instead ?
        val stds = repo[classroom] ?: emptyList()
        repo[classroom] = stds.filter { it.nr != nr } + s
        return Optional.of(s)
    }


    /**
     * Example:
     *   curl --request DELETE http://localhost:4000/classroom/i42d/students/4536
     */
    @Synchronized
    @AutoRoute(path = "/classroom/{classroom}/students/{nr}", method = ArVerb.DELETE)
    fun removeStudent(@ArRoute classroom: String, @ArRoute nr: Int) : Optional<Student> {
        val stds = repo[classroom] ?: return Optional.empty()
        val s = stds.firstOrNull { it.nr == nr } ?: return Optional.empty()
        repo[classroom] = stds.filter { it.nr != nr }
        return Optional.of(s)
    }

    @Synchronized
    @AutoRoute(path = "/", method = ArVerb.GET)
    @ArSequence
    fun writeOnFile (): Optional<Sequence<Sequence<String>>> {
        val directoryPath = "app/src/main/kotlin/Files"
        val fileName = "fase3.txt"
        val directory = Paths.get(directoryPath)
        val fullPath = directory.resolve(fileName)
        val file = fullPath.toFile()
        val contentSequence = directory.watchNewFilesContent()
        var n = 0
        val task = Runnable {
            n++
            file.appendText("$n\n")
        }
        executor.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS)
        return Optional.of(contentSequence)
    }

}