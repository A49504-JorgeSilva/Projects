package pt.isel.ls.houses.domain

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val token: Uuid
)