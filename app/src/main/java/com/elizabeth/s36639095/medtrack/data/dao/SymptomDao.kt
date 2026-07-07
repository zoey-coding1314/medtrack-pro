package com.elizabeth.s36639095.medtrack.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.elizabeth.s36639095.medtrack.data.Symptom
import kotlinx.coroutines.flow.Flow

@Dao
interface SymptomDao {
    @Insert
    suspend fun insertSymptom(symptom: Symptom)

    @Insert
    suspend fun insertSymptoms(symptomList: List<Symptom>)

    @Update
    suspend fun updateSymptom(symptom: Symptom)

    @Delete
    suspend fun deleteSymptom(symptom: Symptom)

    @Query("SELECT * FROM symptoms ORDER BY patientId ASC")
    fun getAllSymptoms(): Flow<List<Symptom>>

    @Query("SELECT * FROM symptoms WHERE patientId = :patientId")
    fun getSymptomsById(patientId: String?): Flow<List<Symptom>>

    @Query("DELETE FROM symptoms WHERE patientId = :patientId")
    suspend fun deleteSymptomById(patientId: String)

    @Query(" SELECT AVG(severity) FROM symptoms ")
    fun averageSeverity() : Flow<Float>?

    @Query(" SELECT AVG(severity) FROM symptoms WHERE patientId = :patientId")
    fun averageSeverityByPatientId(patientId: String?) : Flow<Float>?

    @Query("SELECT category FROM symptoms GROUP BY category ORDER BY COUNT(*) DESC " +
            "LIMIT 1")
    fun countedCategory() : Flow<String>

    @Query("SELECT category FROM symptoms WHERE patientId = :patientId GROUP BY category " +
            "ORDER BY COUNT(*) DESC LIMIT 1")
    fun mostCommonSymptom(patientId: String?) : Flow<String?>
}