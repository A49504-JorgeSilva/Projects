package pt.isel.chat

import pt.isel.Column
import pt.isel.Pk
import pt.isel.Table

@Table("channels")
data class Channel(
    @Pk val name: String,
    val type: ChannelType,
    // Timestamp for when the channel was created
    @Column("created_at") val createdAt: Long,
    // Whether the channel is archived or active
    @Column("is_archived") val isArchived: Boolean,
    // Limit for message size in this channel
    @Column("max_message_length") val maxMessageLength: Int,
    // Maximum number of members allowed in the channel
    @Column("max_members") val maxMembers: Int,
    // Whether messages can be sent or it's read-only
    @Column("is_read_only")val isReadOnly: Boolean,
    // Timestamp of the last message sent in the channel
    @Column("last_message_timestamp") val lastMessageTimestamp: Long,
)
