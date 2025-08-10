insert into PERSON (email, taxnumber, name) values
('john.doe@example.com', 123456789, 'John Doe'),
('jane.smith@example.com', 987654321, 'Jane Smith'),
('alice.wonderland@example.com', 112233445, 'Alice Wonderland');

insert into CLIENT (person, dtregister) values
(1, '2024-11-01 10:00:00'),
(2, '2024-11-02 12:30:00'),
(3, '2024-11-03 15:45:00');

insert into EMPLOYEE (person) values
(1),
(2);

insert into STATION (latitude, longitude) values
(38.7223, -9.1393), -- Lisboa
(41.1579, -8.6291), -- Porto
(37.0161, -7.9304); -- Faro

insert into TYPEOF (reference, nodays, price) values
('resident', 30, 25.00),
('tourist', 7, 10.00);

insert into CARD (credit, typeof, client) values
(50.00, 'resident', 1),
(20.00, 'tourist', 2),
(15.00, 'tourist', 3);

insert into TOPUP (dttopup, card, value) values
('2024-12-01 09:00:00', 1, 10.00),
('2024-12-02 11:30:00', 2, 15.50),
('2024-12-03 14:00:00', 3, 5.75);

insert into SCOOTERMODEL (designation, autonomy) values
('Model X', 25),
('Model Y', 30),
('Model Z', 20);

insert into SCOOTER (weight, maxvelocity, battery, model) values
(12.50, 25.00, 20, 1),
(14.00, 30.00, 25, 2),
(13.00, 20.00, 18, 3);

insert into DOCK (station, state, scooter) values
(1, 'free', NULL),       -- Doca livre, sem trotineta
(2, 'occupy', 1),        -- Doca ocupada, associada à trotineta ID 1
(3, 'under maintenance', NULL); -- Doca em manutenção

insert into REPLACEMENTORDER (dtorder, dtreplacement, roccupation, station) values
('2024-12-01 08:00:00', '2024-12-02 10:00:00', 75, 1),
('2024-12-03 09:00:00', '2024-12-04 11:00:00', 50, 2);

insert into REPLACEMENT (dtreporder, dtreplacement, action, repstation, employee) values
('2024-12-01 08:00:00', '2024-12-02 10:00:00', 'inplace', 1, 1),
('2024-12-03 09:00:00', NULL, 'remove', 2, 2);

insert into TRAVEL (dtinitial, comment, evaluation, dtfinal, client, scooter, stinitial, stfinal) values
('2024-12-01 12:00:00', NULL, 4, '2024-12-01 13:00:00', 1, 1, 1, 2),
('2024-12-02 14:00:00', 'Great experience!', 5, '2024-12-02 15:30:00', 2, 2, 2, 3);

insert into SERVICECOST (unlock, usable) values
(1.00, 0.15);
