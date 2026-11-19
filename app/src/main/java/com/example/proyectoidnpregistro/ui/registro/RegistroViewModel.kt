package com.example.proyectoidnpregistro.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoidnpregistro.data.local.AsistenteEntity
import com.example.proyectoidnpregistro.data.model.TipoAsistente
import com.example.proyectoidnpregistro.data.provider.UbigeoProvider
import com.example.proyectoidnpregistro.data.repository.AsistenteRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormularioState(
    val id: Long = 0,
    val nombres: String = "",
    val apellidos: String = "",
    // Campos de ubicación autocompletados por defecto con la sede del congreso
    val departamento: String = UbigeoProvider.DEFAULT_DEPARTAMENTO,
    val provincia: String = UbigeoProvider.DEFAULT_PROVINCIA,
    val distrito: String = UbigeoProvider.DEFAULT_DISTRITO,
    val direccion: String = "",
    val grupoSanguineo: String = "",
    val tipoAsistente: String = TipoAsistente.ESTUDIANTE.displayName,
    val isEditMode: Boolean = false,

    // Errores de validación
    val nombresError: String? = null,
    val apellidosError: String? = null,
    val departamentoError: String? = null,
    val provinciaError: String? = null,
    val distritoError: String? = null,
    val direccionError: String? = null,
    val grupoSanguineoError: String? = null,

    val isSaving: Boolean = false
) {
    // Cálculo reactivo del porcentaje de completado (0 a 100%)
    val porcentajeCompletado: Int
        get() {
            var camposValidos = 0
            val totalCampos = 6

            // 1. Nombres
            if (nombres.trim().length >= 2 && nombresError == null) camposValidos++
            // 2. Apellidos
            if (apellidos.trim().length >= 2 && apellidosError == null) camposValidos++
            // 3. Ubicación (los 3 presentes)
            if (departamento.isNotBlank() && provincia.isNotBlank() && distrito.isNotBlank()) camposValidos++
            // 4. Dirección
            if (direccion.trim().length >= 4 && direccionError == null) camposValidos++
            // 5. Grupo Sanguíneo
            if (grupoSanguineo.isNotBlank()) camposValidos++
            // 6. Tipo de Asistente
            if (tipoAsistente.isNotBlank()) camposValidos++

            return (camposValidos * 100) / totalCampos
        }

    val totalCamposValidos: Int
        get() {
            var validos = 0
            if (nombres.trim().length >= 2 && nombresError == null) validos++
            if (apellidos.trim().length >= 2 && apellidosError == null) validos++
            if (departamento.isNotBlank() && provincia.isNotBlank() && distrito.isNotBlank()) validos++
            if (direccion.trim().length >= 4 && direccionError == null) validos++
            if (grupoSanguineo.isNotBlank()) validos++
            if (tipoAsistente.isNotBlank()) validos++
            return validos
        }
}

class RegistroViewModel(
    private val repository: AsistenteRepository
) : ViewModel() {

    private val _formularioState = MutableStateFlow(FormularioState())
    val formularioState: StateFlow<FormularioState> = _formularioState.asStateFlow()

    private val _eventGuardadoExitoso = MutableSharedFlow<String>()
    val eventGuardadoExitoso: SharedFlow<String> = _eventGuardadoExitoso.asSharedFlow()

    private val regexSoloLetras = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ ]+$")

    fun cargarAsistente(id: Long) {
        if (id <= 0) return
        viewModelScope.launch {
            val asistente = repository.getAsistenteById(id).firstOrNull()
            asistente?.let { item ->
                _formularioState.update {
                    it.copy(
                        id = item.id,
                        nombres = item.nombres,
                        apellidos = item.apellidos,
                        departamento = item.departamento,
                        provincia = item.provincia,
                        distrito = item.distrito,
                        direccion = item.direccion,
                        grupoSanguineo = item.grupoSanguineo,
                        tipoAsistente = item.tipoAsistente,
                        isEditMode = true,
                        nombresError = null,
                        apellidosError = null,
                        departamentoError = null,
                        provinciaError = null,
                        distritoError = null,
                        direccionError = null,
                        grupoSanguineoError = null
                    )
                }
            }
        }
    }

    fun onNombresChanged(nombres: String) {
        val error = validarNombre(nombres, "El nombre")
        _formularioState.update { it.copy(nombres = nombres, nombresError = error) }
    }

    fun onApellidosChanged(apellidos: String) {
        val error = validarNombre(apellidos, "El apellido")
        _formularioState.update { it.copy(apellidos = apellidos, apellidosError = error) }
    }

    fun onDepartamentoChanged(departamento: String) {
        val deptoAnterior = _formularioState.value.departamento
        val error = if (departamento.isBlank()) "Selecciona un departamento" else null
        _formularioState.update {
            if (deptoAnterior != departamento) {
                it.copy(
                    departamento = departamento,
                    departamentoError = error,
                    provincia = "",
                    distrito = "",
                    provinciaError = null,
                    distritoError = null
                )
            } else {
                it.copy(departamento = departamento, departamentoError = error)
            }
        }
    }

    fun onProvinciaChanged(provincia: String) {
        val provAnterior = _formularioState.value.provincia
        val error = if (provincia.isBlank()) "Selecciona una provincia" else null
        _formularioState.update {
            if (provAnterior != provincia) {
                it.copy(
                    provincia = provincia,
                    provinciaError = error,
                    distrito = "",
                    distritoError = null
                )
            } else {
                it.copy(provincia = provincia, provinciaError = error)
            }
        }
    }

    fun onDistritoChanged(distrito: String) {
        val error = if (distrito.isBlank()) "Selecciona un distrito" else null
        _formularioState.update { it.copy(distrito = distrito, distritoError = error) }
    }

    fun onDireccionChanged(direccion: String) {
        val error = when {
            direccion.isBlank() -> "La dirección es obligatoria"
            direccion.trim().length < 4 -> "Debe ingresar una dirección más específica (mínimo 4 caracteres)"
            else -> null
        }
        _formularioState.update { it.copy(direccion = direccion, direccionError = error) }
    }

    fun onGrupoSanguineoChanged(grupo: String) {
        val error = if (grupo.isBlank()) "Selecciona un grupo sanguíneo" else null
        _formularioState.update { it.copy(grupoSanguineo = grupo, grupoSanguineoError = error) }
    }

    fun onTipoAsistenteChanged(tipo: String) {
        _formularioState.update { it.copy(tipoAsistente = tipo) }
    }

    private fun validarNombre(texto: String, campo: String): String? {
        val trimmed = texto.trim()
        return when {
            trimmed.isBlank() -> "$campo es obligatorio"
            trimmed.length < 2 -> "$campo debe tener al menos 2 caracteres"
            !regexSoloLetras.matches(trimmed) -> "$campo solo debe contener letras y espacios"
            else -> null
        }
    }

    fun validarTodosLosCampos(): Boolean {
        val current = _formularioState.value
        val nomErr = validarNombre(current.nombres, "El nombre")
        val apeErr = validarNombre(current.apellidos, "El apellido")
        val depErr = if (current.departamento.isBlank()) "Selecciona un departamento" else null
        val proErr = if (current.provincia.isBlank()) "Selecciona una provincia" else null
        val disErr = if (current.distrito.isBlank()) "Selecciona un distrito" else null
        val dirErr = when {
            current.direccion.isBlank() -> "La dirección es obligatoria"
            current.direccion.trim().length < 4 -> "Mínimo 4 caracteres"
            else -> null
        }
        val grpErr = if (current.grupoSanguineo.isBlank()) "Selecciona un grupo sanguíneo" else null

        _formularioState.update {
            it.copy(
                nombresError = nomErr,
                apellidosError = apeErr,
                departamentoError = depErr,
                provinciaError = proErr,
                distritoError = disErr,
                direccionError = dirErr,
                grupoSanguineoError = grpErr
            )
        }

        return nomErr == null && apeErr == null && depErr == null &&
                proErr == null && disErr == null && dirErr == null && grpErr == null
    }

    fun guardarAsistente() {
        if (!validarTodosLosCampos()) return

        val state = _formularioState.value
        viewModelScope.launch {
            _formularioState.update { it.copy(isSaving = true) }

            val asistente = AsistenteEntity(
                id = state.id,
                nombres = state.nombres.trim(),
                apellidos = state.apellidos.trim(),
                departamento = state.departamento,
                provincia = state.provincia,
                distrito = state.distrito,
                direccion = state.direccion.trim(),
                grupoSanguineo = state.grupoSanguineo,
                tipoAsistente = state.tipoAsistente
            )

            if (state.isEditMode) {
                repository.updateAsistente(asistente)
                _eventGuardadoExitoso.emit("Asistente actualizado correctamente")
            } else {
                repository.insertAsistente(asistente)
                _eventGuardadoExitoso.emit("Asistente registrado exitosamente")
            }

            _formularioState.update { it.copy(isSaving = false) }
        }
    }
}
