package com.example.appinterface.Api

import com.example.appinterface.Models.LoginRequest
import com.example.appinterface.Models.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>
}