package pt.isel.car.dao

import pt.isel.car.Car
import pt.isel.car.Transmission
import java.sql.Connection

class CarDBInserterJdbc(
    private val connection: Connection,
) {
    fun insertEngine(newEngine: pt.isel.car.Engine) {
        val sql =
            """
            INSERT INTO engines (id, brand, power, fuel)
            VALUES (?, ?, ?, ?::fuel)
            """
        connection.prepareStatement(sql).use { statement ->
            statement.setLong(1, newEngine.id)
            statement.setString(2, newEngine.brand)
            statement.setInt(3, newEngine.power)
            statement.setString(4, newEngine.fuel.name)
            statement.executeUpdate()
        }
    }

    fun insertTransmission(newTransmission: Transmission) {
        val sql =
            """
            INSERT INTO transmissions (id, brand, type, gears)
            VALUES (?, ?, ?::transmission_type, ?)
            """
        connection.prepareStatement(sql).use { statement ->
            statement.setLong(1, newTransmission.id)
            statement.setString(2, newTransmission.brand)
            statement.setString(3, newTransmission.type.name)
            statement.setInt(4, newTransmission.gears)
            statement.executeUpdate()
        }
    }

    fun insertCar(newCar: Car) {
        insertEngine(newCar.engine)
        insertTransmission(newCar.transmission)

        val sql =
            """
            INSERT INTO cars (id, brand, year, engine_id, transmission_id)
            VALUES (?, ?, ?, ?, ?)
            """
        connection.prepareStatement(sql).use { stmt ->
            stmt.setLong(1, newCar.id)
            stmt.setString(2, newCar.brand)
            stmt.setInt(3, newCar.year)
            stmt.setLong(4, newCar.engine.id)
            stmt.setLong(5, newCar.transmission.id)
            stmt.executeUpdate()
        }
    }
}
