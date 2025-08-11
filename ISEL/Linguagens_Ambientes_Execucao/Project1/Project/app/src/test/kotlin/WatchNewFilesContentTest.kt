import org.junit.jupiter.api.Test
import pt.isel.watchNewFilesContent

import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.test.assertTrue

class WatchNewFilesContentTest {

    @Test
    fun `writing only one time`() {
        val executor = Executors.newScheduledThreadPool(1)
        val directoryPath = "src/test/kotlin/TestFiles"
        val fileName = "Test1.txt"
        val directory = Paths.get(directoryPath)
        val fullPath = directory.resolve(fileName)


        fullPath.toFile().createNewFile()

        val delayTwoSeconds = 2L
        executor.schedule({
            Files.write(fullPath, listOf("Blah Blah Blah"))
        }, delayTwoSeconds, TimeUnit.SECONDS)

        val newFilesContent = directory.watchNewFilesContent()


        var found = false


        for (content in newFilesContent) {
            for (lines in content) {
                if (lines == "Blah Blah Blah") {
                    found = true
                    println(lines)
                    break
                }
            }
            if (found) {
                break

            }
        }


        assertTrue(found, "Expected content not found")
        executor.shutdown()
    }

    @Test
    fun `writing two times`() {
        val executor = Executors.newScheduledThreadPool(1)
        val directoryPath = "src/test/kotlin/TestFiles"
        val fileName = "Test2.txt"
        val directory = Paths.get(directoryPath)
        val fullPath = directory.resolve(fileName)

        val file = fullPath.toFile()

        val delayTwoSeconds = 2L
        executor.schedule({
            file.appendText("Barbie\n")
        }, delayTwoSeconds, TimeUnit.SECONDS)

        val delayThreeSeconds = 3L
        executor.schedule({
            file.appendText("Ken\n")
        }, delayThreeSeconds, TimeUnit.SECONDS)

        val newFilesContent = directory.watchNewFilesContent()


        var foundBarbie = false
        var foundKen = false

        for (content in newFilesContent) {
            for (lines in content) {
                println(lines)
                if (lines == "Barbie") {
                    foundBarbie = true


                }
                if (lines == "Ken") {
                    foundKen = true
                }

                if (foundBarbie && foundKen) {
                    break
                }
            }
            if (foundBarbie && foundKen) {
                break

            }
        }
        assertTrue(foundBarbie, "Expected content not found")
        assertTrue(foundKen, "Expected content not found")
        executor.shutdown()
    }

    @Test
    fun `writing on a file that already have things`() {
        val executor = Executors.newScheduledThreadPool(1)
        val directoryPath = "src/test/kotlin/TestFiles"
        val fileName = "Test3.txt"
        val directory = Paths.get(directoryPath)
        val fullPath = directory.resolve(fileName)

        val file = fullPath.toFile()

        val delayTwoSeconds = 2L
        executor.schedule({
            file.appendText("\nigual a ti")
        }, delayTwoSeconds, TimeUnit.SECONDS)

        val newFilesContent = directory.watchNewFilesContent()


        var found = false


        for (content in newFilesContent) {
            for (lines in content) {
                println(lines)
                if (lines == "igual a ti") {
                    found = true
                    break
                }
            }
            if (found) {
                break

            }
        }


        assertTrue(found, "Expected content not found")
        executor.shutdown()
    }

    @Test
    fun `test concurrent file change`() {
        //val executor = Executors.newScheduledThreadPool(1)
        val directoryPath = "src/test/kotlin/TestFiles"
        val fileName = "Test4.txt"
        val directory = Paths.get(directoryPath)
        val fullPath = directory.resolve(fileName)

        val file = fullPath.toFile()

        // Start watching the directory
        val contentSequence = directory.watchNewFilesContent()

        // Make a concurrent change to the file content
        thread {
            Thread.sleep(1000) // Wait for 1 second
            file.appendText("Concurrent change!")
        }

        // Get the first content sequence from the watch
        val firstContent = contentSequence.firstOrNull()

        // Check if the concurrent change is visible in the content
        assertTrue(firstContent?.any { it.contains("Concurrent change!") } ?: false)
    }
}