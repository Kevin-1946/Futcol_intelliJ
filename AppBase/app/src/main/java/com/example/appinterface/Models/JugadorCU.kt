package com.example.appinterface.Models

data class JugadorCU(
    val nombre: String,
    val n_documento: String,
    val fecha_nacimiento: String, // "YYYY-MM-DD"
    val email: String,
    val password: String,
    val genero: String,
    val edad: Int,
    val user_id: Int,
    val equipo_id: Int
)