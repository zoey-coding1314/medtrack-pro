package com.elizabeth.s36639095.medtrack.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.elizabeth.s36639095.medtrack.data.Patient
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Insert
    suspend fun insertPatient(patient: Patient)

    @Insert
    suspend fun insertPatients(patientList: List<Patient>)

    @Update
    suspend fun updatePatient(patient: Patient)

    @Delete
    suspend fun deletePatient(patient: Patient)

    @Query("SELECT * FROM patients ORDER BY patientId ASC")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("SELECT * FROM patients WHERE patientId = :patientId")
    fun getPatientById(patientId: String?): Flow<Patient?>

    @Query("DELETE FROM patients WHERE patientId = :patientId")
    suspend fun deletePatientById(patientId: String)

    @Query("SELECT COUNT(*) FROM patients")
    fun getPatientTotal(): Flow<Int>
}