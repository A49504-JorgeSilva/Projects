package pt.isel.ls.houses.data

import pt.isel.ls.houses.domain.User
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface UserRepository {
    fun create(name: String, email: String, passwordHash: String, token: Uuid): User
    fun getUserById(id: Int): User?
    fun findByToken(token: Uuid): User?
    fun findByEmail(email: String): User?
    fun findPasswordByEmail(email: String): String?
}