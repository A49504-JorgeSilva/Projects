-- Enum for Permission
CREATE TYPE permission AS ENUM ('READ_ONLY', 'READ_WRITE');

-- Enum for ChannelType
CREATE TYPE channel_type AS ENUM ('PUBLIC', 'PRIVATE');

-- User table
CREATE TABLE users
(
    id        SERIAL PRIMARY KEY,
    name      VARCHAR(255)        NOT NULL,
    email     VARCHAR(255) UNIQUE NOT NULL,
    birthdate DATE                NOT NULL
);

-- Channel table
CREATE TABLE channels
(
    name                   VARCHAR(32) PRIMARY KEY,
    type                   channel_type NOT NULL,
    created_at             BIGINT       NOT NULL,
    is_archived            BOOLEAN      NOT NULL DEFAULT FALSE,
    max_message_length     INT          NOT NULL CHECK (max_message_length > 0),
    max_members            INT          NOT NULL CHECK (max_members > 0),
    is_read_only           BOOLEAN      NOT NULL DEFAULT FALSE,
    last_message_timestamp BIGINT
);

-- Message table
CREATE TABLE messages
(
    id           SERIAL PRIMARY KEY,
    content      TEXT        NOT NULL,
    timestamp    BIGINT      NOT NULL,
    user_id      INT         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    channel_name VARCHAR(32) NOT NULL REFERENCES channels (name) ON DELETE CASCADE
);

-- Member table
CREATE TABLE members
(
    id           SERIAL PRIMARY KEY,
    user_id      INT         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    channel_name VARCHAR(32) NOT NULL REFERENCES channels (name) ON DELETE CASCADE,
    permission   permission  NOT NULL,
    UNIQUE (user_id, channel_name) -- Ensures a user is a member only once per channel
);
