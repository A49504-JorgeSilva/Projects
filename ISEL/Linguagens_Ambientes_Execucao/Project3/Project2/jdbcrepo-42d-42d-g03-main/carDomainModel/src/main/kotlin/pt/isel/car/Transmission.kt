package pt.isel.car

import pt.isel.Pk
import pt.isel.Table

@Table("transmissions")
data class Transmission(
    @Pk val id: Long,
    val brand: String,
    val type: TransmissionType,
    val gears: Int,
)
