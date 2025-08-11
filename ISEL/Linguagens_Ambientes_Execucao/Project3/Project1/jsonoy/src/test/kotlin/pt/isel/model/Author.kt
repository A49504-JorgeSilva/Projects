package pt.isel.model

data class Author(
    val name: String,
    val birthYear: Int,
    val books: List<Book>
)
