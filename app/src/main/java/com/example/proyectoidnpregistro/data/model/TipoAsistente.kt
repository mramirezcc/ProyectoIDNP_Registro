package com.example.proyectoidnpregistro.data.model

enum class TipoAsistente(val displayName: String) {
    PROFESIONAL("Profesional"),
    ESTUDIANTE("Estudiante"),
    PUBLICO_GENERAL("Público en general");

    companion object {
        fun fromString(value: String): TipoAsistente {
            return entries.firstOrNull { it.displayName.equals(value, ignoreCase = true) }
                ?: PUBLICO_GENERAL
        }
    }
}
