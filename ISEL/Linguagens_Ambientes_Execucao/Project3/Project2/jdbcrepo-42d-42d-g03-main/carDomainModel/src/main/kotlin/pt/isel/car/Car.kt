package pt.isel.car

import pt.isel.Column
import pt.isel.Pk
import pt.isel.Table

@Table("cars")
data class Car(
    @Pk val id: Long,
    val brand: String,
    val year: Int,
    @Column("engine_id") val engine: Engine,
    @Column("transmission_id") val transmission: Transmission,
)
