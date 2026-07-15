package pt.isel.ls.houses.webapi.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(val name: String, val email: String, val password: String)

@Serializable
data class CreateUserResponse(val id: Int, val token: String)

@Serializable
data class GetUserResponse(val id: Int, val name: String, val email: String)

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoginResponse(val id: Int, val token: String)