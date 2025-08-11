package pt.isel.model

data class Book(
    val title: String,
    val author: Author,
    val year: Int,
    val genres: List<String>
)