-- Insert Users
INSERT INTO users (name, email, birthdate)
VALUES ('Alice Johnson', 'alice@example.com', '1990-05-12'),
       ('Bob Smith', 'bob@example.com', '1988-09-25'),
       ('Charlie Davis', 'charlie@example.com', '1995-12-10');


-- Insert Channels
INSERT INTO channels (name, type, created_at, is_archived, max_message_length, max_members, is_read_only,
                      last_message_timestamp)
VALUES ('General', 'PUBLIC', 1707720000, FALSE, 500, 100, FALSE, 1707722600),
       ('Development', 'PRIVATE', 1707720001, FALSE, 1000, 50, FALSE, 1707722500),
       ('Support', 'PUBLIC', 1707720002, FALSE, 500, 20, TRUE, 1707722000),
       ('Gaming Chat', 'PUBLIC', 1707720003, FALSE, 500, 200, FALSE, 1707722200),
       ('Esports Discussion', 'PRIVATE', 1707720004, FALSE, 800, 30, FALSE, 1707722700);

-- Insert Members (Users joining Channels)
INSERT INTO members (user_id, channel_name, permission)
VALUES (1, 'General', 'READ_WRITE'),
       (1, 'Development', 'READ_WRITE'),
       (2, 'General', 'READ_WRITE'),
       (2, 'Support', 'READ_ONLY'),
       (3, 'Gaming Chat', 'READ_WRITE'),
       (3, 'Esports Discussion', 'READ_WRITE'),
       (1, 'Esports Discussion', 'READ_ONLY'),
       (2, 'Development', 'READ_WRITE'),
       (3, 'Support', 'READ_ONLY'),
       (2, 'Gaming Chat', 'READ_WRITE');

-- Insert Messages
INSERT INTO messages (content, timestamp, user_id, channel_name)
VALUES ('Hello everyone!', 1707721000, 1, 'General'),
       ('Welcome to the channel!', 1707721100, 2, 'General'),
       ('How can I help you?', 1707721200, 1, 'Support'),
       ('This is a private discussion', 1707721300, 1, 'Development'),
       ('Gaming is awesome!', 1707721400, 3, 'Gaming Chat'),
       ('Any updates on the project?', 1707721500, 2, 'Development'),
       ('The support team is here to assist', 1707721600, 3, 'Support'),
       ('Esports tournament starts soon!', 1707721700, 1, 'Esports Discussion'),
       ('Join our game night!', 1707721800, 3, 'Gaming Chat'),
       ('What’s the best gaming setup?', 1707721900, 2, 'Esports Discussion'),
       ('Please keep the chat professional', 1707722000, 2, 'Support'),
       ('Any new code changes?', 1707722100, 1, 'Development'),
       ('Patch notes for the game are live', 1707722200, 3, 'Gaming Chat'),
       ('This is a pinned message', 1707722300, 1, 'General'),
       ('Happy to be part of this channel!', 1707722400, 2, 'General'),
       ('Need help with debugging', 1707722500, 3, 'Development'),
       ('Tech talk happening today!', 1707722600, 2, 'General'),
       ('Looking for teammates!', 1707722700, 3, 'Esports Discussion'),
       ('Support requests should go here', 1707722800, 1, 'Support'),
       ('Excited for the upcoming features!', 1707722900, 2, 'Development');
