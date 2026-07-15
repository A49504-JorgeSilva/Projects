package pt.isel.ls.houses.services

import pt.isel.ls.houses.data.mem.DbMem
import pt.isel.ls.houses.data.mem.UserRepositoryMem
import pt.isel.ls.houses.domain.AuthenticationException
import pt.isel.ls.houses.domain.ConflictException
import pt.isel.ls.houses.domain.InvalidInputException
import pt.isel.ls.houses.domain.NotFoundException
import kotlin.test.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UserServiceTests {

    private var userRepositoryMem = UserRepositoryMem()
    private var userService = UserServices(userRepositoryMem)

    @BeforeTest
    fun setup() {
        DbMem.clearAll()
    }

    // --- createUser ---

    @Test
    fun `createUser returns user with correct name and email`() {
        val user = userService.createUser("Alice", "alice@example.com", "alice123")

        assertEquals("Alice", user.name)
        assertEquals("alice@example.com", user.email)
    }

    @Test
    fun `createUser trims and lowercases email`() {
        val user = userService.createUser("Alice", "  ALICE@EXAMPLE.COM  ", "alice123")

        assertEquals("alice@example.com", user.email)
    }

    @Test
    fun `createUser assigns sequential ids`() {
        val user1 = userService.createUser("Alice", "alice@example.com", "alice123")
        val user2 = userService.createUser("Bob", "bob@example.com", "bob123")

        assertNotEquals(user1.id, user2.id)
    }

    @Test
    fun `createUser generates a token`() {
        val user = userService.createUser("Alice", "alice@example.com", "alice123")

        assertNotNull(user.token)
    }

    @Test
    fun `createUser throws ConflictException for duplicate email`() {
        userService.createUser("Alice", "alice@example.com", "alice123")

        assertFailsWith<ConflictException> {
            userService.createUser("Alice2", "alice@example.com", "alice123")
        }
    }

    @Test
    fun `createUser throws InvalidInputException for blank name`() {
        assertFailsWith<InvalidInputException> {
            userService.createUser("  ", "alice@example.com", "alice123")
        }
    }

    @Test
    fun `createUser throws InvalidInputException for blank email`() {
        assertFailsWith<InvalidInputException> {
            userService.createUser("Alice", "  ", "alice123")
        }
    }

    @Test
    fun `createUser throws InvalidInputException for email without @`() {
        assertFailsWith<InvalidInputException> {
            userService.createUser("Alice", "invalidemail.com", "alice123")
        }
    }

    // --- getUserById ---

    @Test
    fun `getUserById returns correct user`() {
        val created = userService.createUser("Alice", "alice@example.com", "alice123")

        val found = userService.getUserById(created.id)

        assertEquals(created.id, found.id)
        assertEquals(created.name, found.name)
        assertEquals(created.email, found.email)
    }

    @Test
    fun `getUserById throws NotFoundException for unknown id`() {
        assertFailsWith<NotFoundException> {
            userService.getUserById(999)
        }
    }

    // --- getUserByToken ---

    @Test
    fun `getUserByToken returns correct user`() {
        val created = userService.createUser("Alice", "alice@example.com", "alice123")

        val found = userService.getUserByToken(created.token)

        assertNotNull(found)
        assertEquals(created.id, found.id)
    }

    @Test
    fun `getUserByToken returns null for unknown token`() {
        val fakeToken = Uuid.random()

        val found = userService.getUserByToken(fakeToken)

        assertNull(found)
    }

    // --- validateToken ---

    @Test
    fun `validateToken returns true for valid token`() {
        val created = userService.createUser("Alice", "alice@example.com", "alice123")

        assertTrue(userService.validateToken(created.token))
    }

    @Test
    fun `validateToken returns false for unknown token`() {
        val fakeToken = Uuid.random()

        assertFalse(userService.validateToken(fakeToken))
    }

    @Test
    fun `createUser throws IllegalArgumentException for blank password`() {
        assertFailsWith<IllegalArgumentException> {
            userService.createUser("Alice", "alice@example.com", "  ")
        }
    }

    @Test
    fun `createUser throws IllegalArgumentException for password shorter than 6 characters`() {
        assertFailsWith<IllegalArgumentException> {
            userService.createUser("Alice", "alice@example.com", "12345")
        }
    }

    // --- login ---

    @Test
    fun `login returns user when credentials are correct`() {
        userService.createUser("Alice", "alice@example.com", "correctPassword")

        val loggedInUser = userService.login("alice@example.com", "correctPassword")

        assertNotNull(loggedInUser)
        assertEquals("alice@example.com", loggedInUser.email)
        assertEquals("Alice", loggedInUser.name)
    }

    @Test
    fun `login normalizes email case`() {
        userService.createUser("Alice", "Alice@Example.com", "password123")

        val user = userService.login("ALICE@EXAMPLE.COM", "password123")

        assertNotNull(user)
        assertEquals("alice@example.com", user.email)
    }

    @Test
    fun `login throws NotFoundException for non-existent email`() {
        assertFailsWith<NotFoundException> {
            userService.login("nonexistent@example.com", "anypass")
        }
    }

    @Test
    fun `login throws AuthenticationException for wrong password`() {
        userService.createUser("Alice", "alice@example.com", "correctPassword")

        assertFailsWith<AuthenticationException> {
            userService.login("alice@example.com", "wrongPassword")
        }
    }

    @Test
    fun `login throws NotFoundException for blank email`() {
        assertFailsWith<NotFoundException> {
            userService.login("   ", "anypass")
        }
    }

    @Test
    fun `login throws AuthenticationException for blank password`() {
        val user = userService.createUser("Alice", "alice@example.com", "alice123")
        assertFailsWith<AuthenticationException> {
            userService.login("alice@example.com", "  ")
        }
    }

}