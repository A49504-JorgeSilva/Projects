package pt.isel.chat

import pt.isel.Pk
import pt.isel.Table
import java.sql.Date

@Table("users")
data class User(
    @Pk val id: Long,
    val name: String,
    val email: String,
    val birthdate: Date,
)
