-- drop tables in reverse order to avoid foreign key conflicts
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS houses;
DROP TABLE IF EXISTS locations;
DROP TABLE IF EXISTS users;

-- Users Table
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(100) NOT NULL,
                       token UUID NOT NULL -- Postgres has a native UUID type that matches Kotlin's Uuid
);

-- Locations Table
CREATE TABLE locations (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           type VARCHAR(50) NOT NULL, -- store LocationType enum
                           parent_id INT REFERENCES locations(id) -- self-referencing foreign key (can be NULL)
);

-- Houses Table
CREATE TABLE houses (
                        id SERIAL PRIMARY KEY,
                        title VARCHAR(100) NOT NULL,
                        location_id INT NOT NULL REFERENCES locations(id),
                        area_sqmt DOUBLE PRECISION NOT NULL,
                        price_per_night DOUBLE PRECISION NOT NULL,
                        description TEXT NOT NULL,
                        owner_id INT NOT NULL REFERENCES users(id)
);

-- Bookings Table
CREATE TABLE bookings (
                          id SERIAL PRIMARY KEY,
                          start_date DATE NOT NULL,
                          end_date DATE NOT NULL,
                          user_id INT NOT NULL REFERENCES users(id),
                          house_id INT NOT NULL REFERENCES houses(id)
);