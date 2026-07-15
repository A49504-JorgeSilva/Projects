package pt.isel.ls.houses.data.mem

import pt.isel.ls.houses.data.UserRepository
import pt.isel.ls.houses.domain.User
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UserRepositoryMem : UserRepository {

    override fun create(name: String, email: String, passwordHash: String, token: Uuid): User {
        val id = DbMem.nextUserId++
        val user = User(id, name, email, token)
        DbMem.users[id] = user
        DbMem.userPasswordHashes[id] = passwordHash
        return user
    }

    override fun getUserById(id: Int): User? {
        return DbMem.users[id]
    }

    override fun findByToken(token: Uuid): User? {
        return DbMem.users.values.find { it.token == token }
    }

    override fun findByEmail(email: String): User? {
        return DbMem.users.values.find { it.email == email }
    }

    override fun findPasswordByEmail(email: String): String? {
        val user = findByEmail(email) ?: return null
        return DbMem.userPasswordHashes[user.id]
    }
}