package com.example.appinterface.Models

data class Login(
    // Request
    val email: String? = null,
    val password: String? = null,

    // Response
    val message: String? = null,
    val userId: Int? = null,
    val role: String? = null
)