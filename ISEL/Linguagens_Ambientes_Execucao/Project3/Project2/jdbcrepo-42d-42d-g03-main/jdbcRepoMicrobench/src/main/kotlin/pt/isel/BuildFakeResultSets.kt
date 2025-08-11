package pt.isel

import java.sql.Date

private val engines =
    listOf(
        mapOf(
            "id" to 1L,
            "brand" to "Ford EcoBoost",
            "power" to 250,
            "fuel" to "GASOLINE",
        ),
        mapOf(
            "id" to 2L,
            "brand" to "Chevrolet Duramax",
            "power" to 300,
            "fuel" to "DIESEL",
        ),
        mapOf(
            "id" to 3L,
            "brand" to "Toyota Hybrid Synergy Drive",
            "power" to 200,
            "fuel" to "GASOLINE",
        ),
        mapOf(
            "id" to 4L,
            "brand" to "Honda i-VTEC",
            "power" to 150,
            "fuel" to "GASOLINE",
        ),
        mapOf(
            "id" to 5L,
            "brand" to "BMW TwinPower Turbo",
            "power" to 400,
            "fuel" to "GASOLINE",
        ),
    )

private val transmissions =
    listOf(
        mapOf(
            "id" to 1L,
            "brand" to "Ford 10R80",
            "type" to "AUTOMATIC",
            "gears" to 10,
        ),
        mapOf(
            "id" to 2L,
            "brand" to "Chevrolet 6L80",
            "type" to "AUTOMATIC",
            "gears" to 6,
        ),
        mapOf(
            "id" to 3L,
            "brand" to "Toyota CVT",
            "type" to "AUTOMATIC",
            "gears" to 1,
        ),
        mapOf(
            "id" to 4L,
            "brand" to "Honda 6MT",
            "type" to "MANUAL",
            "gears" to 6,
        ),
        mapOf(
            "id" to 5L,
            "brand" to "BMW ZF 8HP",
            "type" to "AUTOMATIC",
            "gears" to 8,
        ),
    )

private val cars =
    listOf(
        mapOf(
            "id" to 1L,
            "brand" to "Ford EcoBoost",
            "year" to 2020,
            "engine_id" to 1L,
            "transmission_id" to 1L,
        ),
        mapOf(
            "id" to 2L,
            "brand" to "Chevrolet Silverado",
            "year" to 2021,
            "engine_id" to 2L,
            "transmission_id" to 2L,
        ),
        mapOf(
            "id" to 3L,
            "brand" to "Toyota Camry",
            "year" to 2022,
            "engine_id" to 3L,
            "transmission_id" to 3L,
        ),
        mapOf(
            "id" to 4L,
            "brand" to "Honda Accord",
            "year" to 2023,
            "engine_id" to 4L,
            "transmission_id" to 4L,
        ),
        mapOf(
            "id" to 5L,
            "brand" to "BMW M3",
            "year" to 2024,
            "engine_id" to 5L,
            "transmission_id" to 5L,
        ),
    )

fun buildEnginesFakeResultSet(id: Int?): FakeResultSet {
    val list: List<Map<String, Any>> =
        if (id == null) {
            engines.map { it } // Copying the list
        } else {
            engines.filter { it["id"] == id }
        }

    class EnginesFakeResultSet : FakeResultSet() {
        private var cursor = -1
        private val data: List<Map<String, Any>> = list // Copying the list

        override fun next(): Boolean {
            cursor++
            return cursor < data.size
        }

        override fun getInt(columnLabel: String): Int = data[cursor][columnLabel] as Int

        override fun getLong(columnLabel: String): Long = data[cursor][columnLabel] as Long

        override fun getString(columnLabel: String): String = data[cursor][columnLabel] as String

        override fun getDate(columnLabel: String): Date = data[cursor][columnLabel] as Date

        override fun getBoolean(columnLabel: String?): Boolean = data[cursor][columnLabel] as Boolean

        override fun getObject(columnLabel: String?): Any? = data[cursor][columnLabel]
    }

    return EnginesFakeResultSet()
}

fun buildTransmissionsFakeResultSet(id: Int?): FakeResultSet {
    val list: List<Map<String, Any>> =
        if (id == null) {
            transmissions.map { it } // Copying the list
        } else {
            transmissions.filter { it["id"] == id }
        }

    class TransmissionsFakeResultSet : FakeResultSet() {
        private var cursor = -1
        private val data: List<Map<String, Any>> = list // Copying the list

        override fun next(): Boolean {
            cursor++
            return cursor < data.size
        }

        override fun getInt(columnLabel: String): Int = data[cursor][columnLabel] as Int

        override fun getLong(columnLabel: String): Long = data[cursor][columnLabel] as Long

        override fun getString(columnLabel: String): String = data[cursor][columnLabel] as String

        override fun getDate(columnLabel: String): Date = data[cursor][columnLabel] as Date

        override fun getBoolean(columnLabel: String?): Boolean = data[cursor][columnLabel] as Boolean

        override fun getObject(columnLabel: String?): Any? = data[cursor][columnLabel]
    }

    return TransmissionsFakeResultSet()
}

fun buildCarsFakeResultSet(id: Int?): FakeResultSet {
    val list: List<Map<String, Any>> =
        if (id == null) {
            cars.map { it } // Copying the list
        } else {
            cars.filter { it["id"] == id }
        }

    class CarsFakeResultSet : FakeResultSet() {
        private var cursor = -1
        private val data: List<Map<String, Any>> = list // Copying the list

        override fun next(): Boolean {
            cursor++
            return cursor < data.size
        }

        override fun getInt(columnLabel: String): Int = data[cursor][columnLabel] as Int

        override fun getLong(columnLabel: String): Long = data[cursor][columnLabel] as Long

        override fun getString(columnLabel: String): String = data[cursor][columnLabel] as String

        override fun getDate(columnLabel: String): Date = data[cursor][columnLabel] as Date

        override fun getBoolean(columnLabel: String?): Boolean = data[cursor][columnLabel] as Boolean

        override fun getObject(columnLabel: String?): Any? = data[cursor][columnLabel]
    }

    return CarsFakeResultSet()
}
