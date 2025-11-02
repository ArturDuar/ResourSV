package com.example.resoursv.data.network

import com.example.resoursv.data.model.Recurso
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("/recursos")
    suspend fun getRecursos(): List<Recurso>


    @GET("/recursos/{id}")
    suspend fun getRecurso(@Path("id") id: String): Recurso


    @POST("/recursos")
    suspend fun addRecurso(@Body recurso: Recurso): Recurso


    @PUT("/recursos/{id}")
    suspend fun updateRecurso(@Path("id") id: String, @Body recurso: Recurso): Recurso


    @DELETE("/recursos/{id}")
    suspend fun deleteRecurso(@Path("id") id: String)
}