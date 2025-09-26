package com.example.appinterface.Models

data class Jugador(
    val id: Int,
    val nombre: String,
    val n_documento: String,
    val fecha_nacimiento: String,
    val email: String,
    val password: String,
    val genero: String,
    val edad: Int,
    val user_id: Int,
    val equipo_id: Int
)