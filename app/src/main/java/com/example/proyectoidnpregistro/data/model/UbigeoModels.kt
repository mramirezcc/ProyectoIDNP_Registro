package com.example.proyectoidnpregistro.data.model

data class Departamento(
    val id: String,
    val nombre: String,
    val provincias: List<Provincia>
)

data class Provincia(
    val id: String,
    val nombre: String,
    val distritos: List<Distrito>
)

data class Distrito(
    val id: String,
    val nombre: String
)
