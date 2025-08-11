package pt.isel.chat

data class Member(
    val id: Long,
    val user: User,
    val channel: Channel,
    val permission: Permission,
)
