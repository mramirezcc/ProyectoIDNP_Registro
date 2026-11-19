package com.example.proyectoidnpregistro.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AsistenteDao {

    @Query("SELECT * FROM asistentes ORDER BY fechaRegistro DESC")
    fun getAllAsistentes(): Flow<List<AsistenteEntity>>

    @Query("SELECT * FROM asistentes WHERE id = :id LIMIT 1")
    fun getAsistenteById(id: Long): Flow<AsistenteEntity?>

    @Query("""
        SELECT * FROM asistentes 
        WHERE (nombres LIKE '%' || :query || '%' OR apellidos LIKE '%' || :query || '%')
        ORDER BY fechaRegistro DESC
    """)
    fun searchAsistentes(query: String): Flow<List<AsistenteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsistente(asistente: AsistenteEntity): Long

    @Update
    suspend fun updateAsistente(asistente: AsistenteEntity)

    @Delete
    suspend fun deleteAsistente(asistente: AsistenteEntity)

    @Query("DELETE FROM asistentes WHERE id = :id")
    suspend fun deleteAsistenteById(id: Long)

    @Query("SELECT COUNT(*) FROM asistentes")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM asistentes WHERE tipoAsistente = :tipo")
    fun getCountByTipo(tipo: String): Flow<Int>
}
