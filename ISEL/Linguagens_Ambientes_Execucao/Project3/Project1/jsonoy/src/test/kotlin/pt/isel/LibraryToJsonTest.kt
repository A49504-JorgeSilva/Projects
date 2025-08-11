package pt.isel

import com.google.gson.Gson
import org.assertj.core.api.Assertions.assertThat
import pt.isel.model.Author
import pt.isel.model.Book
import pt.isel.model.Library
import kotlin.test.Test


class LibraryToJsonTest {
    private val author = buildExpectedAuthor()
    private val library = buildExpectedLibrary()
    private val gson = Gson()

    @Test
    fun testBookMembersToJson() {
        val json = author.books.first().membersToJson().also { println(it) }
        val fromJson = gson.fromJson(json, Book::class.java)
        assertThat(fromJson).usingRecursiveComparison().isEqualTo(author.books.first())
    }

    @Test
    fun testAuthorMembersToJson() {
        val json = author.membersToJson().also { println(it) }
        val fromJson = gson.fromJson(json, Author::class.java)
        assertThat(fromJson).usingRecursiveComparison().isEqualTo(author)
    }

    @Test
    fun testLibraryMembersToJson() {
        val json = library.membersToJson().also { println(it) }
        val fromJson = gson.fromJson(json, Library::class.java)
        assertThat(fromJson).usingRecursiveComparison().isEqualTo(library)
    }

    private fun buildExpectedAuthor(): Author {
        val books = listOf(
            Book(
                title = "The Great Gatsby",
                author = Author("F. Scott Fitzgerald", 1896, emptyList()),
                year = 1925,
                genres = listOf("Classic", "Fiction", "Drama")
            ),
            Book(
                title = "Tender Is the Night",
                author = Author("F. Scott Fitzgerald", 1896, emptyList()),
                year = 1934,
                genres = listOf("Novel", "Romance")
            )
        )
        return Author("F. Scott Fitzgerald", 1896, books)
    }

    private fun buildExpectedLibrary(): Library {
        val books = listOf(
            Book(
                title = "The Great Gatsby",
                author = Author("F. Scott Fitzgerald", 1896, emptyList()),
                year = 1925,
                genres = listOf("Classic", "Fiction", "Drama")
            ),
            Book(
                title = "1984",
                author = Author("George Orwell", 1903, emptyList()),
                year = 1949,
                genres = listOf("Dystopian", "Political Fiction")
            )
        )
        return Library("City Library", books)
    }
}
