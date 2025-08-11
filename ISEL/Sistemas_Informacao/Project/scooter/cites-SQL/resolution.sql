/*
 *   ISEL-DEETC-SisInf
 *   ND 2022-2025
 *
 *   
 *   Information Systems Project - Active Databases
 *   
 */

/* ### DO NOT REMOVE THE QUESTION MARKERS ### */


-- region Question 1.a 
CREATE OR REPLACE FUNCTION check_scooter_in_dock()
RETURNS TRIGGER AS $$
BEGIN
	-- Check if the scooter is already in a dock
	IF NOT EXISTS (
		SELECT 1
		FROM DOCK
		WHERE scooter = NEW.scooter
			AND state = 'occupy'
	) THEN
		RAISE EXCEPTION 'Scooter not available in any dock';
	END IF;
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;
-- endregion

-- region Question 1.b
CREATE OR REPLACE FUNCTION check_unique_trip()
RETURNS TRIGGER AS $$
BEGIN
	-- Check if the scooter already has an active trip
	IF EXISTS (
		SELECT 1
		FROM TRAVEL
		WHERE scooter = NEW.scooter
			AND dfinal IS NULL
	) THEN
		RAISE EXCEPTION 'Scooter is on an active trip';
	END IF;

	-- Check if the client is on a trip
	IF EXISTS (
		SELECT 1
		FROM TRAVEL
		WHERE client = NEW.client
			AND dfinal IS NULL
	) THEN
		RAISE EXCEPTION 'Client already has an active trip';
	END IF;

	RETURN NEW;
END;
$$ LANGUAGE plpgsql;
-- endregion

-- region Question 2
CREATE OR REPLACE FUNCTION fx_dock_occupancy(stationkid integer) RETURNS NUMERIC(3,2) AS $$
DECLARE
	total_docks INTEGER;
	occupied_docks INTEGER;
BEGIN
	-- Count active docks in the station
	SELECT COUNT(*) INTO total_docks
	FROM DOCK
	WHERE station = stationkid
		AND state IN ('free', 'occupy');

	-- Return occupancy percentage
	IF total_docks = 0 THEN
		RETURN 0.00;
	END IF;

	SELECT COUNT(*) INTO occupied_docks
	FROM DOCK
	WHERE station = stationkid
	AND state = 'occupy';

	RETURN ROUND(occupied_docks::NUMERIC / total_docks, 2);
END;	
$$ LANGUAGE plpgsql;
-- endregion
 
-- region Question 3
CREATE OR REPLACE VIEW RIDER
AS
SELECT 
	p.*,
	c.dtregister,
	cd.id AS cardid,
	cd.credit,
	cd.typeofcard
FROM CLIENT c 
	INNER JOIN PERSON p ON (c.person=p.id)
	INNER JOIN CARD cd ON (cd.client = c.person);

CREATE OR REPLACE FUNCTION rider_insert()
RETURNS TRIGGER AS $$
DECLARE 
	new_person_id INTEGER;
	new_card_id INTEGER;
BEGIN
	INSERT INTO PERSON (name, email, taxnumber)
		VALUES (NEW.name, NEW.email, NEW.taxnumber)
		RETURNING id INTO new_person_id;

	INSERT INTO CLIENT (person, dtregister)
		VALUES (new_person_id, NEW.dtregister);

	INSERT INTO CARD (client, credit, typeofcard)
		VALUES (new_person_id, NEW.credit, NEW.typeofcard)
		RETURNING id INTO new_card_id;

	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER rider_insert_trigger
INSTEAD OF INSERT ON RIDER
FOR EACH ROW EXECUTE FUNCTION rider_insert();

CREATE OR REPLACE FUNCTION rider_update()
RETURNS TRIGGER AS $$
BEGIN
	UPDATE PERSON
	SET name = NEW.name, email = NEW.email, taxnumber = NEW.taxnumber
	WHERE id = OLD.id;

	UPDATE CLIENT
	SET dtregister = NEW.dtregister
	WHERE person = OLD.id;

	UPDATE CARD
	SET credit = NEW.credit, typeofcard = NEW.typeofcard
	WHERE client = OLD.cardid;

	RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER rider_update_trigger
INSTEAD OF UPDATE ON RIDER
FOR EACH ROW EXECUTE FUNCTION rider_update();
-- endregion

-- region Question 4
CREATE OR REPLACE PROCEDURE startTrip(dockid integer, clientid  integer)
LANGUAGE plpgsql AS $$
DECLARE
	v_scooter INTEGER;
	v_station INTEGER;
	v_now TIMESTAMP := NOW();
BEGIN
	-- Check if the dock exists and is occupied
	SELECT scooter, station INTO v_scooter, v_station
	FROM DOCK
	WHERE number = dockid AND state = 'occupy'
	FOR UPDATE;

	IF v_scooter IS NULL  THEN
		RAISE EXCEPTION 'Dock % not found or not occupied', dockid;
	END IF;

	-- Update the dock state to 'free' and remove the scooter
	UPDATE DOCK
	SET state = 'free', scooter = NULL
	WHERE number = dockid;

	-- Insert a new travel record
	INSERT INTO TRAVEL (dinitial, client, scooter, stinitial, stfinal, comment, evaluation, dfinal)
	VALUES (v_now, clientid, v_scooter, v_station, NULL, NULL, NULL, NULL);
	-- stfinal, comment, evaluation, dfinal are set to NULL for the start of the trip
END;
$$;
-- endregion