package com.example.proyectoidnpregistro.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoidnpregistro.data.local.AsistenteEntity
import com.example.proyectoidnpregistro.data.model.TipoAsistente
import com.example.proyectoidnpregistro.data.repository.AsistenteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ListaUiState(
    val asistentes: List<AsistenteEntity> = emptyList(),
    val totalCount: Int = 0,
    val countProfesionales: Int = 0,
    val countEstudiantes: Int = 0,
    val countPublico: Int = 0,
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val filtroTipo: String = "TODOS"
)

class ListaViewModel(
    private val repository: AsistenteRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filtroTipo = MutableStateFlow("TODOS")
    val filtroTipo: StateFlow<String> = _filtroTipo.asStateFlow()

    val uiState: StateFlow<ListaUiState> = combine(
        repository.getAllAsistentes(),
        _searchQuery,
        _filtroTipo
    ) { asistentes, query, filtro ->
        val total = asistentes.size
        val countProf = asistentes.count { it.tipoAsistente.equals(TipoAsistente.PROFESIONAL.displayName, ignoreCase = true) }
        val countEst = asistentes.count { it.tipoAsistente.equals(TipoAsistente.ESTUDIANTE.displayName, ignoreCase = true) }
        val countPub = asistentes.count { it.tipoAsistente.equals(TipoAsistente.PUBLICO_GENERAL.displayName, ignoreCase = true) }

        val filtrados = asistentes.filter { asistente ->
            val matchQuery = query.isBlank() ||
                    asistente.nombres.contains(query, ignoreCase = true) ||
                    asistente.apellidos.contains(query, ignoreCase = true) ||
                    asistente.distrito.contains(query, ignoreCase = true) ||
                    asistente.provincia.contains(query, ignoreCase = true)

            val matchTipo = when (filtro) {
                "TODOS" -> true
                else -> asistente.tipoAsistente.equals(filtro, ignoreCase = true)
            }

            matchQuery && matchTipo
        }

        ListaUiState(
            asistentes = filtrados,
            totalCount = total,
            countProfesionales = countProf,
            countEstudiantes = countEst,
            countPublico = countPub,
            isLoading = false,
            searchQuery = query,
            filtroTipo = filtro
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ListaUiState(isLoading = true)
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onFiltroTipoChanged(tipo: String) {
        _filtroTipo.value = tipo
    }

    fun eliminarAsistente(asistente: AsistenteEntity) {
        viewModelScope.launch {
            repository.deleteAsistente(asistente)
        }
    }
}
