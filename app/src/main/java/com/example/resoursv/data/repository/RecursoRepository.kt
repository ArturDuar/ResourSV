package com.example.resoursv.data.repository

import com.example.resoursv.data.model.Recurso
import com.example.resoursv.data.network.ApiService

class RecursoRepository(private val api: ApiService) {
    suspend fun getAll(): List<Recurso> = api.getRecursos()
    suspend fun getById(id: String): Recurso = api.getRecurso(id)
    suspend fun add(recurso: Recurso): Recurso = api.addRecurso(recurso)
    suspend fun update(id: String, recurso: Recurso): Recurso = api.updateRecurso(id, recurso)
    suspend fun delete(id: String) = api.deleteRecurso(id)
}