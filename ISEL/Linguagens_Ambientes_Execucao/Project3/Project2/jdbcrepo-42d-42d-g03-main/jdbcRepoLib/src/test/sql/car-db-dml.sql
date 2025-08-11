-- Insert Engines
INSERT INTO engines (brand, power, fuel)
VALUES ('Ford EcoBoost', 250, 'GASOLINE'),
       ('Chevrolet Duramax', 300, 'DIESEL'),
       ('Toyota Hybrid Synergy Drive', 200, 'GASOLINE'),
       ('Honda i-VTEC', 150, 'GASOLINE'),
       ('BMW TwinPower Turbo', 400, 'GASOLINE');

-- Insert Transmissions
INSERT INTO transmissions (brand, type, gears)
VALUES ('Ford 10R80', 'AUTOMATIC', 10),
       ('Chevrolet 6L80', 'AUTOMATIC', 6),
       ('Toyota CVT', 'AUTOMATIC', 1),
       ('Honda 6MT', 'MANUAL', 6),
       ('BMW ZF 8HP', 'AUTOMATIC', 8);

-- Insert Cars
INSERT INTO cars (brand, year, engine_id, transmission_id)
VALUES ('Ford Mustang', 2020, 1, 1),
       ('Chevrolet Silverado', 2021, 2, 2),
       ('Toyota Camry', 2022, 3, 3),
       ('Honda Accord', 2023, 4, 4),
       ('BMW M3', 2024, 5, 5);
