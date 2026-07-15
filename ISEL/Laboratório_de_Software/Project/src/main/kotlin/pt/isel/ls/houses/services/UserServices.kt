package pt.isel.ls.houses.services

import org.mindrot.jbcrypt.BCrypt
import pt.isel.ls.houses.data.UserRepository
import pt.isel.ls.houses.domain.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UserServices(
    private val userRepository: UserRepository
) {

    fun createUser(name: String, email: String, password: String): User  {
        if (name.isBlank()) throw InvalidInputException("Name cannot be blank")
        if (email.isBlank()) throw InvalidInputException("Email cannot be blank")
        if (!email.contains('@')) throw InvalidInputException("Invalid email format")

        val trimmedEmail = email.trim().lowercase()
        val existingUser = userRepository.findByEmail(trimmedEmail)

        if (existingUser != null) {
                throw ConflictException("Email already in use")
        }

        if (password.isBlank()) throw IllegalArgumentException("Password cannot be blank")
        if (password.length < 6) throw IllegalArgumentException("Password must be at least 6 characters long")

        val hashedPw = BCrypt.hashpw(password, BCrypt.gensalt())
        return userRepository.create(name.trim(), trimmedEmail, hashedPw, Uuid.random())
    }

    fun getUserById(id: Int): User {
        return userRepository.getUserById(id)
            ?: throw NotFoundException("User with id $id not found")
    }

    fun getUserByToken(token: Uuid): User? {
        return userRepository.findByToken(token)
    }

    fun validateToken(token: Uuid): Boolean {
        return userRepository.findByToken(token) != null
    }

    fun login(email: String, passwordRaw: String): User {
        val trimmedEmail = email.trim().lowercase()
        val user = userRepository.findByEmail(trimmedEmail)
            ?: throw NotFoundException("User not found")

        val hashedPw = userRepository.findPasswordByEmail(trimmedEmail)
        if (!BCrypt.checkpw(passwordRaw, hashedPw)) {
            throw AuthenticationException("Invalid email or password")
        }

        return user
    }
}