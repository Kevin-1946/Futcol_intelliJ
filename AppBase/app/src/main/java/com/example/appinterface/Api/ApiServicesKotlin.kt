package com.example.appinterface.Api

import com.example.appinterface.Models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiServicesKotlin {

    @POST("auth/login")
    suspend fun login(@Body body: Login): Response<Login>

    @GET("torneos")
    suspend fun getTorneos(): Response<List<Torneo>>

    @GET("torneos/{id}")
    suspend fun getTorneo(@Path("id") id: Int): Response<Any>

    @POST("torneos")
    suspend fun crearTorneo(@Body body: Torneo): Response<String>

    @PUT("torneos/{id}")
    suspend fun actualizarTorneo(@Path("id") id: Int, @Body body: Torneo): Response<String>

    @DELETE("torneos/{id}")
    suspend fun eliminarTorneo(@Path("id") id: Int): Response<String>


    @GET("jugadores")
    suspend fun getJugadores(): Response<List<Jugador>>

    @POST("jugadores")
    suspend fun crearJugador(@Body body: Jugador): Response<String>

    @PUT("jugadores/{id}")
    suspend fun actualizarJugador(@Path("id") id: Int, @Body body: Jugador): Response<String>

    @DELETE("jugadores/{id}")
    suspend fun eliminarJugador(@Path("id") id: Int): Response<String>
}