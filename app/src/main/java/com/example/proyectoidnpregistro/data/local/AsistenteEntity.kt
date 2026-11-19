package com.example.proyectoidnpregistro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asistentes")
data class AsistenteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombres: String,
    val apellidos: String,
    val departamento: String,
    val provincia: String,
    val distrito: String,
    val direccion: String,
    val grupoSanguineo: String,
    val tipoAsistente: String,
    val fechaRegistro: Long = System.currentTimeMillis()
) {
    val nombreCompleto: String
        get() = "$nombres $apellidos".trim()

    val ubicacionCompleta: String
        get() = "$distrito, $provincia - $departamento"
}
