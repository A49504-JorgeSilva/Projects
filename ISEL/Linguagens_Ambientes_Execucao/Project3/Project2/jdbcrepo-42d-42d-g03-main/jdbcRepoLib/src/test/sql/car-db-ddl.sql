-- Enum for Fuel
CREATE TYPE fuel AS ENUM ('GASOLINE','DIESEL');

-- Enum for TransmissionType
CREATE TYPE transmission_type AS ENUM ('MANUAL','AUTOMATIC');

-- Engine table
CREATE TABLE engines
(
    id          SERIAL PRIMARY KEY,
    brand       VARCHAR(255) NOT NULL,
    power       INT NOT NULL CHECK (power > 0),
    fuel        fuel NOT NULL
);

-- Transmission table
CREATE TABLE transmissions
(
    id              SERIAL PRIMARY KEY,
    brand           VARCHAR(255) NOT NULL,
    type            transmission_type NOT NULL,
    gears           INT NOT NULL CHECK (gears > 0)
);

-- Car table
CREATE TABLE cars
(
    id              SERIAL PRIMARY KEY,
    brand           VARCHAR(255) NOT NULL,
    year            INT NOT NULL CHECK (year > 1885),
    engine_id       INT NOT NULL REFERENCES engines (id) ON DELETE CASCADE,
    transmission_id INT NOT NULL REFERENCES transmissions (id) ON DELETE CASCADE
);
