package com.example.resoursv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resoursv.data.model.Recurso
import com.example.resoursv.data.network.RetrofitClient
import com.example.resoursv.data.repository.RecursoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RecursoUiState {
    object Loading : RecursoUiState()
    data class Success(val recursos: List<Recurso>) : RecursoUiState()
    data class Error(val message: String) : RecursoUiState()
}

enum class SortOption {
    TITULO_ASC,
    TITULO_DESC,
    TIPO_ASC,
    TIPO_DESC,
    RECIENTE
}

class RecursoViewModel : ViewModel() {
    private val repository = RecursoRepository(RetrofitClient.apiService)

    private val _uiState = MutableStateFlow<RecursoUiState>(RecursoUiState.Loading)
    val uiState: StateFlow<RecursoUiState> = _uiState.asStateFlow()

    private val _allRecursos = MutableStateFlow<List<Recurso>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTipo = MutableStateFlow<String?>(null)
    val selectedTipo: StateFlow<String?> = _selectedTipo.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.RECIENTE)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    init {
        loadRecursos()
    }

    fun loadRecursos() {
        viewModelScope.launch {
            _uiState.value = RecursoUiState.Loading
            try {
                val recursos = repository.getAll()
                _allRecursos.value = recursos
                applyFiltersAndSort()
            } catch (e: Exception) {
                _uiState.value = RecursoUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun searchRecursos(query: String) {
        _searchQuery.value = query
        applyFiltersAndSort()
    }

    fun filterByTipo(tipo: String?) {
        _selectedTipo.value = tipo
        applyFiltersAndSort()
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
        applyFiltersAndSort()
    }

    private fun applyFiltersAndSort() {
        var filtered = _allRecursos.value

        // Aplicar búsqueda
        if (_searchQuery.value.isNotBlank()) {
            val query = _searchQuery.value.lowercase()
            filtered = filtered.filter {
                it.id.lowercase().contains(query) ||
                        it.titulo.lowercase().contains(query) ||
                        it.tipo.lowercase().contains(query) ||
                        it.descripcion.lowercase().contains(query)
            }
        }

        // Aplicar filtro por tipo
        _selectedTipo.value?.let { tipo ->
            filtered = filtered.filter { it.tipo.equals(tipo, ignoreCase = true) }
        }

        // Aplicar ordenamiento
        filtered = when (_sortOption.value) {
            SortOption.TITULO_ASC -> filtered.sortedBy { it.titulo.lowercase() }
            SortOption.TITULO_DESC -> filtered.sortedByDescending { it.titulo.lowercase() }
            SortOption.TIPO_ASC -> filtered.sortedBy { it.tipo.lowercase() }
            SortOption.TIPO_DESC -> filtered.sortedByDescending { it.tipo.lowercase() }
            SortOption.RECIENTE -> filtered.reversed()
        }

        _uiState.value = RecursoUiState.Success(filtered)
    }

    fun getTiposUnicos(): List<String> {
        return _allRecursos.value.map { it.tipo }.distinct().sorted()
    }

    fun addRecurso(recurso: Recurso, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.add(recurso)
                loadRecursos()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error al agregar recurso")
            }
        }
    }

    fun updateRecurso(id: String, recurso: Recurso, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.update(id, recurso)
                loadRecursos()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error al actualizar recurso")
            }
        }
    }

    fun deleteRecurso(id: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.delete(id)
                loadRecursos()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error al eliminar recurso")
            }
        }
    }

    fun getRecursoById(id: String, onSuccess: (Recurso) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val recurso = repository.getById(id)
                onSuccess(recurso)
            } catch (e: Exception) {
                onError(e.message ?: "Error al obtener recurso")
            }
        }
    }
}