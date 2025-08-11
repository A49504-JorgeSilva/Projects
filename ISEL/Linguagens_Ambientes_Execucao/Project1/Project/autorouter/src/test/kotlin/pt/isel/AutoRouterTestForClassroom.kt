package pt.isel

import pt.isel.autorouter.ArHttpRoute
import pt.isel.autorouter.ArVerb
import pt.isel.autorouter.autorouterDynamic
import pt.isel.autorouter.autorouterReflect
import kotlin.test.*

class AutoRouterTestForClassroom {

    @Test fun get_students_via_reflection() {
        get_students(
            ClassroomController().autorouterReflect().toList()
        )
    }

    @Test fun get_students_via_dynamic() {
        get_students(
            ClassroomController().autorouterDynamic().toList()
        )
    }


    fun get_students(routes: List<ArHttpRoute>) {
        val r = routes.first { it.path == "/classroom/{classroom}" }
        val res = r.handler.handle(
            mapOf("classroom" to "i42d"),
            emptyMap(),
            emptyMap())
        assertContentEquals(
            listOf(
                Student(9876, "Ole Super", 7, 5),
                Student(4536, "Isel Maior", 7, 5),
                Student(5689, "Ever Sad", 7, 3),
            ),
            res.get() as List<Student>)
    }

    @Test fun get_students_with_name_containing_word_via_reflection() {
        get_students_with_name_containing_word(
            ClassroomController().autorouterReflect().toList()
        )
    }

    @Test fun get_students_with_name_containing_word_via_dynamic() {
        get_students_with_name_containing_word(
            ClassroomController().autorouterDynamic().toList()
        )
    }



    fun get_students_with_name_containing_word(routes: List<ArHttpRoute>) {
        val r = routes.first { it.path == "/classroom/{classroom}" }
        val res = r.handler.handle(
            mapOf("classroom" to "i42d"),
            mapOf("student" to "maior"),
            emptyMap())
        assertContentEquals(
            listOf(Student(4536, "Isel Maior", 7, 5)),
            res.get() as List<Student>)
    }

    @Test
    fun remove_student_via_reflection() {
        val controller = ClassroomController()
        val routes = controller.autorouterReflect().toList()

        // Executa a rota que remove o estudante
        val r = routes.first { it.path == "/classroom/{classroom}/students/{nr}" && it.method == ArVerb.DELETE}
        val res = r.handler.handle(
            mapOf("classroom" to "i42d", "nr" to "4536"),
            emptyMap(),
            emptyMap()
        )
        assertEquals(Student(4536, "Isel Maior", 7, 5),res.get())
        val x = controller.repo["i42d"]?.firstOrNull { it.nr == 4536 }
        assertEquals(null, x)
    }

    @Test
    fun remove_student_via_Dynamic() {
        val controller = ClassroomController()
        val routes = controller.autorouterDynamic().toList()

        // Executa a rota que remove o estudante
        val r = routes.first { it.path == "/classroom/{classroom}/students/{nr}" && it.method == ArVerb.DELETE}
        val res = r.handler.handle(
                mapOf("classroom" to "i42d", "nr" to "4536"),
                emptyMap(),
                emptyMap()
        )
        assertEquals(Student(4536, "Isel Maior", 7, 5),res.get())
        val x = controller.repo["i42d"]?.firstOrNull { it.nr == 4536 }
        assertEquals(null, x)
    }

    @Test
    fun add_student_via_reflection() {
        val controller = ClassroomController()
        val routes = controller.autorouterReflect().toList()

        // Executa a rota que adiciona o estudante
        val r = routes.first { it.path == "/classroom/{classroom}/students/{nr}" && it.method == ArVerb.PUT}
        val student = Student(9876, "Ole Super", 7, 5)
        val res = r.handler.handle(
            mapOf("classroom" to "i41d", "nr" to "9876"),
            emptyMap(),
            mapOf("nr" to "9876", "name" to "Ole Super", "group" to "7", "semester" to "5")
        )
        assertEquals(student, controller.repo["i41d"]?.firstOrNull { it.nr == 9876 })
    }

    @Test
    fun add_student_via_dynamic() {
        val controller = ClassroomController()
        val routes = controller.autorouterDynamic().toList()

        // Executa a rota que adiciona o estudante
        val r = routes.first { it.path == "/classroom/{classroom}/students/{nr}" && it.method == ArVerb.PUT}
        val student = Student(9876, "Ole Super", 7, 5)
        val res = r.handler.handle(
                mapOf("classroom" to "i41d", "nr" to "9876"),
                emptyMap(),
                mapOf("nr" to "9876", "name" to "Ole Super", "group" to "7", "semester" to "5")
        )
        assertEquals(student, controller.repo["i41d"]?.firstOrNull { it.nr == 9876 })
    }
}
