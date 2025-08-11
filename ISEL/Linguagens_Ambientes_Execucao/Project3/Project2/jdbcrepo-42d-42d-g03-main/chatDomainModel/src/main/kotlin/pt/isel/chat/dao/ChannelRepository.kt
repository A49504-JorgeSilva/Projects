package pt.isel.chat.dao

import pt.isel.Repository
import pt.isel.chat.Channel
import pt.isel.chat.ChannelType

interface ChannelRepository : Repository<String, Channel> {
    fun insert(
        name: String,
        type: ChannelType,
        createdAt: Long,
        isArchived: Boolean,
        maxMessageLength: Int,
        maxMembers: Int,
        isReadOnly: Boolean,
        lastMessageTimestamp: Long,
    )

    fun insert(channel: Channel) {
        insert(
            channel.name,
            channel.type,
            channel.createdAt,
            channel.isArchived,
            channel.maxMessageLength,
            channel.maxMembers,
            channel.isReadOnly,
            channel.lastMessageTimestamp,
        )
    }
}
