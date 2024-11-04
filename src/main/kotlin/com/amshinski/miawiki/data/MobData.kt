package com.amshinski.miawiki.data

data class MobData(
    val name: String,
    val health: Int,
    val damage: Int,
    val drops: List<DropData>,
    val image: String
)

data class DropData(
    val item: String,
    val quantity: String,
    val chance: Double?
)