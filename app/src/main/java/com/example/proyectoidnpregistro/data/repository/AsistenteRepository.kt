package com.example.proyectoidnpregistro.data.repository

import com.example.proyectoidnpregistro.data.local.AsistenteDao
import com.example.proyectoidnpregistro.data.local.AsistenteEntity
import kotlinx.coroutines.flow.Flow

interface AsistenteRepository {
    fun getAllAsistentes(): Flow<List<AsistenteEntity>>
    fun getAsistenteById(id: Long): Flow<AsistenteEntity?>
    fun searchAsistentes(query: String): Flow<List<AsistenteEntity>>
    fun getTotalCount(): Flow<Int>
    fun getCountByTipo(tipo: String): Flow<Int>
    suspend fun insertAsistente(asistente: AsistenteEntity): Long
    suspend fun updateAsistente(asistente: AsistenteEntity)
    suspend fun deleteAsistente(asistente: AsistenteEntity)
    suspend fun deleteAsistenteById(id: Long)
}

class AsistenteRepositoryImpl(
    private val dao: AsistenteDao
) : AsistenteRepository {

    override fun getAllAsistentes(): Flow<List<AsistenteEntity>> = dao.getAllAsistentes()

    override fun getAsistenteById(id: Long): Flow<AsistenteEntity?> = dao.getAsistenteById(id)

    override fun searchAsistentes(query: String): Flow<List<AsistenteEntity>> =
        dao.searchAsistentes(query)

    override fun getTotalCount(): Flow<Int> = dao.getTotalCount()

    override fun getCountByTipo(tipo: String): Flow<Int> = dao.getCountByTipo(tipo)

    override suspend fun insertAsistente(asistente: AsistenteEntity): Long =
        dao.insertAsistente(asistente)

    override suspend fun updateAsistente(asistente: AsistenteEntity) =
        dao.updateAsistente(asistente)

    override suspend fun deleteAsistente(asistente: AsistenteEntity) =
        dao.deleteAsistente(asistente)

    override suspend fun deleteAsistenteById(id: Long) =
        dao.deleteAsistenteById(id)
}
