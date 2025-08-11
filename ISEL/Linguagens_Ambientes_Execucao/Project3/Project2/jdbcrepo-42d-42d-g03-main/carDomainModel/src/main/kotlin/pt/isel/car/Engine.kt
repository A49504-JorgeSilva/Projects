package pt.isel.car

import pt.isel.Pk
import pt.isel.Table

@Table("engines")
data class Engine(
    @Pk val id: Long,
    val brand: String,
    val power: Int,
    val fuel: Fuel,
)
