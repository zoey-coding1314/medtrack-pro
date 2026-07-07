package com.elizabeth.s36639095.medtrack.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.elizabeth.s36639095.medtrack.data.Medication
import com.elizabeth.s36639095.medtrack.data.Symptom
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Insert
    suspend fun insertMedication(medication: Medication)

    @Insert
    suspend fun insertMedications(medicationList: List<Medication>)

    @Update
    suspend fun updateMedication(medication: Medication)

    @Delete
    suspend fun deleteMedication(medication: Medication)

    @Query("SELECT * FROM medications ORDER BY patientId ASC")
    fun getAllMedications(): Flow<List<Medication>>

    @Query("SELECT * FROM  medications WHERE patientId = :patientId")
    fun getMedicationsById(patientId: String?): Flow<List<Medication>>

    @Query("SELECT COUNT(*) FROM medications")
    fun getMedicationTotal(): Flow<Int>
}