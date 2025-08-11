package pt.isel

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Column(
    val name: String,
)
