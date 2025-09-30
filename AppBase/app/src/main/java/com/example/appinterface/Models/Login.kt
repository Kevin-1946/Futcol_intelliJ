package com.example.appinterface.Models

data class Login(
    val email: String? = null,
    val password: String? = null,

    val message: String? = null,
    val userId: Int? = null,
    val role: String? = null
)