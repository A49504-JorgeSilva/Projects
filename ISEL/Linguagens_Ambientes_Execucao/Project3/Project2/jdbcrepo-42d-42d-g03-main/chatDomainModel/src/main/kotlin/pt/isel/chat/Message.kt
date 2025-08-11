package pt.isel.chat

import pt.isel.Column
import pt.isel.Pk
import pt.isel.Table

@Table("messages")
data class Message(
    @Pk val id: Long,
    val content: String,
    val timestamp: Long,
    @Column("user_id") val user: User,
    @Column("channel_name")val channel: Channel,
)
