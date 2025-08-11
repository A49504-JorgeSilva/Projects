package pt.isel

import java.sql.Connection
import java.sql.PreparedStatement
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberProperties

open class RepositoryReflect<K : Any, T : Any>(
    protected val connection: Connection,
    private val domainKlass: KClass<T>,
) : Repository<K, T> {
    // Table name derived from the class name
    private val tableName = domainKlass.simpleName?.lowercase()?.plus("s")
    private val pKPropertyName = getIdProperty().name
    private val pKColumnName =
        getColumnName(
            domainKlass.constructors
                .first()
                .parameters
                .first { it.name == pKPropertyName },
        )
    private val constructor = domainKlass.constructors.first()
    private val columnNamesCache = constructor.parameters.associateWith { getColumnName(it) }

    override fun getById(id: K): T? {
        val query = "SELECT * FROM $tableName WHERE $pKColumnName = ?"
        connection.prepareStatement(query).use { statement ->
            statement.setObject(1, id)
            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                return mapResultSetToEntity(resultSet)
            }
        }
        return null
    }

    override fun getAll(): List<T> {
        val query = "SELECT * FROM $tableName"
        return connection.prepareStatement(query).use { statement ->
            statement.executeQuery().use { rs ->
                val resultList = mutableListOf<T>()
                while (rs.next()) {
                    resultList.add(mapResultSetToEntity(rs))
                }
                resultList
            }
        }
    }

    override fun update(entity: T) {
        val idProperty = getIdProperty()
        val columns = getColumns()
        val setClause = getSetClause(columns)
        val query = "UPDATE $tableName SET $setClause WHERE ${idProperty.name} = ?"
        connection.prepareStatement(query).use { statement ->
            setStatementParameters(statement, columns, entity)
            statement.setObject(columns.size + 1, idProperty.get(entity))
            statement.executeUpdate()
        }
    }

    override fun deleteById(id: K) {
        val query = "DELETE FROM $tableName WHERE $pKColumnName = ?"
        connection.prepareStatement(query).use { statement ->
            statement.setObject(1, id)
            statement.executeUpdate()
        }
    }

    override fun findAll(): Queryable<T> {
        val query = "SELECT * FROM $tableName"
        val properties = domainKlass.memberProperties.toList()
        val paramByName = domainKlass.constructors.first().parameters.associateBy { it.name }
        val propertyToColumn =
            properties.associateWith { prop ->
                val param = paramByName[prop.name]
                param?.let { getColumnName(it) } ?: prop.name
            }
        return QueryableBuilder(connection, query, ::mapResultSetToEntity, propertyToColumn)
    }

    // Auxiliary functions

    /**
     * Auxiliary function to get the primary key property.
     * @throws IllegalArgumentException if no suitable primary key is found
     */
    private fun getIdProperty(): KProperty1<T, *> =
        domainKlass.memberProperties.firstOrNull { property ->
            property.annotations.any { it is Pk }
        } ?: throw IllegalArgumentException("No suitable primary key found in class ${domainKlass.simpleName}")

    /**
     * Auxiliary function to get the columns of the table.
     * @throws IllegalArgumentException if no suitable column name is found for a property
     */
    private fun getColumns(): List<KProperty1<T, *>> =
        domainKlass.memberProperties.filter { property ->
            property.annotations.none { it is Pk }
        }

    /**
     * Auxiliary function to get the set clause for the update statement.
     * @throws IllegalArgumentException if no suitable column name is found for a property
     */
    private fun getSetClause(columns: List<KProperty1<T, *>>): String =
        columns.joinToString(", ") { property ->
            domainKlass.constructors
                .first()
                .parameters
                .firstOrNull { param ->
                    param.name == property.name
                }?.let { param ->
                    val columnName = getColumnName(param)
                    "$columnName = ?"
                } ?: throw IllegalArgumentException("No suitable column name found for property ${property.name}")
        }

    /**
     * Auxiliary function to set the parameters of a prepared statement.
     * @throws IllegalArgumentException if no suitable column name is found for a parameter
     */
    private fun setStatementParameters(
        statement: PreparedStatement,
        columns: List<KProperty1<T, *>>,
        entity: T,
    ) {
        columns.forEachIndexed { index, property ->
            val value = property.get(entity)
            val sqlValue =
                if (value != null && value::class.annotations.any { it is Table }) {
                    val associatedIdProperty =
                        value::class.memberProperties.firstOrNull { it.annotations.any { it is Pk } }
                            ?: throw IllegalArgumentException(
                                "No suitable primary key found in associated class ${value::class.simpleName}",
                            )
                    associatedIdProperty.call(value)
                } else {
                    value
                }
            statement.setObject(index + 1, sqlValue, getSqlType(sqlValue))
        }
    }

    /**
     * Auxiliary function to map the result set to an entity.
     * @throws IllegalArgumentException if no suitable column name is found for a parameter
     */
    open fun mapResultSetToEntity(resultSet: java.sql.ResultSet): T {
        val params =
            constructor.parameters
                .map { param ->
                    val columnName =
                        columnNamesCache[param] ?: throw IllegalArgumentException("No column name found for parameter ${param.name}")
                    val value = resultSet.getObject(columnName)
                    val paramType = param.type.classifier as? KClass<*>
                    if (paramType != null && paramType.annotations.any { it is Table }) {
                        loadAssociation(paramType, value) ?: getEnumTypeValue(param, value)
                    } else {
                        getEnumTypeValue(param, value)
                    }
                }.toTypedArray()
        return constructor.call(*params)
    }

    /**
     * Auxiliary function to get the SQL type of a value.
     * @throws IllegalArgumentException if the value type is not supported
     */
    private fun getSqlType(value: Any?): Int =
        when (value) {
            is String -> java.sql.Types.VARCHAR
            is Int -> java.sql.Types.INTEGER
            is Long -> java.sql.Types.BIGINT
            is Boolean -> java.sql.Types.BOOLEAN
            is Enum<*> -> java.sql.Types.OTHER
            is java.sql.Date -> java.sql.Types.DATE
            else -> {
                throw IllegalArgumentException("Unsupported type: ${value?.javaClass?.name}")
            }
        }

    /**
     * Auxiliary function to get the column name from the parameter.
     * @throws IllegalArgumentException if no name is found for the parameter
     */
    private fun getColumnName(param: KParameter): String {
        val columnAnnotation = param.findAnnotations<Column>().firstOrNull()
        val name = columnAnnotation?.name ?: param.name
        return name ?: throw IllegalArgumentException("No name found for parameter ${param.name}")
    }

    /**
     * Auxiliary function to get the enum type value from the parameter.
     */
    private fun getEnumTypeValue(
        param: KParameter,
        value: Any,
    ): Any =
        if (param.type.classifier is KClass<*> && (param.type.classifier as KClass<*>).java.isEnum) {
            val enumClass = param.type.classifier as KClass<Enum<*>>
            enumClass.java.enumConstants.first { it.name == value }
        } else {
            value
        }

    /**
     * Auxiliary function to load associated entities.
     */
    private fun loadAssociation(
        paramType: KClass<*>,
        value: Any?,
    ): Any? {
        if (value == null) return null

        val associatedTableName = paramType.simpleName?.lowercase()?.plus("s")
        val associatedPk =
            paramType.memberProperties.firstOrNull { it.annotations.any { it is Pk } }
                ?: throw IllegalArgumentException("No suitable primary key found in associated class ${paramType.simpleName}")

        val associatedQuery = "SELECT * FROM $associatedTableName WHERE ${associatedPk.name} = ?"
        connection.prepareStatement(associatedQuery).use { assocStmt ->
            assocStmt.setObject(1, value)
            val assocResultSet = assocStmt.executeQuery()
            if (assocResultSet.next()) {
                val assocConstructor = paramType.constructors.first()
                val assocParams =
                    assocConstructor.parameters.map { assocParam ->
                        val assocColumnAnnot = assocParam.findAnnotations<Column>().firstOrNull()
                        val assocValue =
                            if (assocColumnAnnot != null) {
                                assocResultSet.getObject(assocColumnAnnot.name)
                            } else {
                                assocResultSet.getObject(assocParam.name)
                            }
                        getEnumTypeValue(assocParam, assocValue)
                    }
                return assocConstructor.call(*assocParams.toTypedArray())
            }
        }
        return null
    }
}
