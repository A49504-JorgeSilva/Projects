package pt.isel

import java.sql.Connection
import kotlin.reflect.KProperty1

class QueryableBuilder<T>(
    private val connection: Connection,
    private val query: String,
    private val mapResultSetToEntity: (java.sql.ResultSet) -> T,
    private val propertyToColumn: Map<KProperty1<T, *>, String>,
) : Queryable<T> {
    private val whereClauses = mutableListOf<String>()
    private val orderByClauses = mutableListOf<String>()

    private fun getColumnName(prop: KProperty1<T, *>): String = propertyToColumn[prop] ?: prop.name

    override fun <V> whereEquals(
        prop: KProperty1<T, V>,
        value: V,
    ): Queryable<T> {
        val columnName = getColumnName(prop)
        whereClauses.add("$columnName = '$value'")
        return this
    }

    override fun <V> orderBy(prop: KProperty1<T, V>): Queryable<T> {
        val columnName = getColumnName(prop)
        orderByClauses.add(columnName)
        return this
    }

    override fun iterator(): Iterator<T> =
        sequence {
            val whereClause =
                if (whereClauses.isNotEmpty()) {
                    " WHERE ${whereClauses.joinToString(" AND ")}"
                } else {
                    ""
                }

            val orderByClause =
                if (orderByClauses.isNotEmpty()) {
                    " ORDER BY ${orderByClauses.joinToString(", ")}"
                } else {
                    ""
                }

            connection.prepareStatement("$query$whereClause$orderByClause").use { stmt ->
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        yield(mapResultSetToEntity(rs))
                    }
                }
            }
        }.iterator()
}
