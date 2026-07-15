package pt.isel.ls.houses.data.postgres

import pt.isel.ls.houses.data.UserRepository
import pt.isel.ls.houses.domain.User
import kotlin.uuid.Uuid
import javax.sql.DataSource
import java.sql.ResultSet
import java.sql.Statement
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class UserRepositoryPostgres(private val dataSource: DataSource) : UserRepository {

    override fun create(name: String, email: String, password: String, token: Uuid): User {
        val sql = "INSERT INTO users (name, email, password, token) VALUES (?, ?, ?, ?)"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stm ->
                stm.setString(1, name)
                stm.setString(2, email)
                stm.setString(3, password)
                stm.setObject(4, java.util.UUID.fromString(token.toString()))
                stm.executeUpdate()

                val rs = stm.generatedKeys
                if (rs.next()) {
                    return User(rs.getInt(1), name, email, token)
                } else {
                    throw IllegalStateException("Failed to retrieve generated ID for user")
                }
            }
        }
    }

    override fun getUserById(id: Int): User? {
        val sql = "SELECT id, name, email, token FROM users WHERE id = ?"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stm ->
                stm.setInt(1, id)
                val rs = stm.executeQuery()
                return if (rs.next()) mapRow(rs) else null
            }
        }
    }

    override fun findByToken(token: Uuid): User? {
        val sql = "SELECT id, name, email, token FROM users WHERE token = ?"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stm ->
                stm.setObject(1, java.util.UUID.fromString(token.toString()))
                val rs = stm.executeQuery()
                return if (rs.next()) mapRow(rs) else null
            }
        }
    }

    override fun findByEmail(email: String): User? {
        val sql = "SELECT id, name, email, token FROM users WHERE email = ?"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stm ->
                stm.setString(1, email)
                val rs = stm.executeQuery()
                return if (rs.next()) mapRow(rs) else null
            }
        }
    }

    override fun findPasswordByEmail(email: String): String? {
        val sql = "SELECT password FROM users WHERE email = ?"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stm ->
                stm.setString(1, email)
                val rs = stm.executeQuery()
                return if (rs.next()) rs.getString("password") else null
            }
        }
    }

    private fun mapRow(rs: ResultSet): User {
        return User(
            id = rs.getInt("id"),
            name = rs.getString("name"),
            email = rs.getString("email"),
            token = Uuid.parse(rs.getString("token"))
        )
    }
}