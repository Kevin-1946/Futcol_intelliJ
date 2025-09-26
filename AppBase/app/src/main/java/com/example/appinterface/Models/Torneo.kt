package com.example.appinterface.Models

data class Torneo(
    val id: Int,
    val nombre: String,
    val fecha_inicio: String,
    val fecha_fin: String,
    val categoria: String,
    val modalidad: String,
    val organizador: String,
    val precio: Double,
    val sedes: String
)
