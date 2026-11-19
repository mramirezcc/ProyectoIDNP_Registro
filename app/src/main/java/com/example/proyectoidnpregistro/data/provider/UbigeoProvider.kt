package com.example.proyectoidnpregistro.data.provider

import com.example.proyectoidnpregistro.data.model.Departamento
import com.example.proyectoidnpregistro.data.model.Distrito
import com.example.proyectoidnpregistro.data.model.Provincia

object UbigeoProvider {

    // Valores por defecto de la sede del Congreso
    const val DEFAULT_DEPARTAMENTO = "Arequipa"
    const val DEFAULT_PROVINCIA = "Arequipa"
    const val DEFAULT_DISTRITO = "Yanahuara"

    val departamentos: List<Departamento> = listOf(
        Departamento(
            id = "04",
            nombre = "Arequipa",
            provincias = listOf(
                Provincia(
                    id = "0401",
                    nombre = "Arequipa",
                    distritos = listOf(
                        Distrito("040101", "Arequipa (Cercado)"),
                        Distrito("040102", "Alto Selva Alegre"),
                        Distrito("040103", "Cayma"),
                        Distrito("040104", "Cerro Colorado"),
                        Distrito("040105", "Characato"),
                        Distrito("040106", "Chiguata"),
                        Distrito("040109", "Jacobo Hunter"),
                        Distrito("040112", "Miraflores"),
                        Distrito("040114", "Paucarpata"),
                        Distrito("040121", "Sachaca"),
                        Distrito("040126", "Yanahuara"),
                        Distrito("040129", "José Luis Bustamante y Rivero")
                    )
                ),
                Provincia(
                    id = "0402",
                    nombre = "Camaná",
                    distritos = listOf(
                        Distrito("040201", "Camaná"),
                        Distrito("040202", "José María Quimper"),
                        Distrito("040206", "Ocoña"),
                        Distrito("040208", "Samuel Pastor")
                    )
                ),
                Provincia(
                    id = "0405",
                    nombre = "Caylloma",
                    distritos = listOf(
                        Distrito("040501", "Chivay"),
                        Distrito("040504", "Cabanaconde"),
                        Distrito("040510", "Majes"),
                        Distrito("040520", "Yanque")
                    )
                ),
                Provincia(
                    id = "0407",
                    nombre = "Islay",
                    distritos = listOf(
                        Distrito("040701", "Mollendo"),
                        Distrito("040702", "Cocachacra"),
                        Distrito("040704", "Islay"),
                        Distrito("040705", "Mejía")
                    )
                )
            )
        ),
        Departamento(
            id = "15",
            nombre = "Lima",
            provincias = listOf(
                Provincia(
                    id = "1501",
                    nombre = "Lima",
                    distritos = listOf(
                        Distrito("150101", "Lima (Cercado)"),
                        Distrito("150116", "Lince"),
                        Distrito("150118", "Los Olivos"),
                        Distrito("150122", "Miraflores"),
                        Distrito("150130", "San Borja"),
                        Distrito("150131", "San Isidro"),
                        Distrito("150140", "Santiago de Surco")
                    )
                ),
                Provincia(
                    id = "1508",
                    nombre = "Huaura",
                    distritos = listOf(
                        Distrito("150801", "Huacho"),
                        Distrito("150806", "Huaura")
                    )
                )
            )
        ),
        Departamento(
            id = "08",
            nombre = "Cusco",
            provincias = listOf(
                Provincia(
                    id = "0801",
                    nombre = "Cusco",
                    distritos = listOf(
                        Distrito("080101", "Cusco"),
                        Distrito("080106", "Santiago"),
                        Distrito("080107", "Wanchaq"),
                        Distrito("080108", "San Sebastián")
                    )
                ),
                Provincia(
                    id = "0813",
                    nombre = "Urubamba",
                    distritos = listOf(
                        Distrito("081301", "Urubamba"),
                        Distrito("081304", "Machupicchu"),
                        Distrito("081306", "Ollantaytambo")
                    )
                )
            )
        ),
        Departamento(
            id = "13",
            nombre = "La Libertad",
            provincias = listOf(
                Provincia(
                    id = "1301",
                    nombre = "Trujillo",
                    distritos = listOf(
                        Distrito("130101", "Trujillo"),
                        Distrito("130104", "Huanchaco"),
                        Distrito("130106", "Moche"),
                        Distrito("130110", "Víctor Larco Herrera")
                    )
                )
            )
        ),
        Departamento(
            id = "20",
            nombre = "Piura",
            provincias = listOf(
                Provincia(
                    id = "2001",
                    nombre = "Piura",
                    distritos = listOf(
                        Distrito("200101", "Piura"),
                        Distrito("200104", "Castilla"),
                        Distrito("200105", "Catacaos")
                    )
                ),
                Provincia(
                    id = "2007",
                    nombre = "Talara",
                    distritos = listOf(
                        Distrito("200701", "Pariñas (Talara)"),
                        Distrito("200705", "Máncora")
                    )
                )
            )
        )
    )

    val gruposSanguineos = listOf(
        "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"
    )

    fun getNombresDepartamentos(): List<String> = departamentos.map { it.nombre }

    fun getNombresProvincias(nombreDepartamento: String): List<String> {
        val depto = departamentos.firstOrNull { it.nombre.equals(nombreDepartamento, ignoreCase = true) }
        return depto?.provincias?.map { it.nombre } ?: emptyList()
    }

    fun getNombresDistritos(nombreDepartamento: String, nombreProvincia: String): List<String> {
        val depto = departamentos.firstOrNull { it.nombre.equals(nombreDepartamento, ignoreCase = true) }
        val prov = depto?.provincias?.firstOrNull { it.nombre.equals(nombreProvincia, ignoreCase = true) }
        return prov?.distritos?.map { it.nombre } ?: emptyList()
    }
}
