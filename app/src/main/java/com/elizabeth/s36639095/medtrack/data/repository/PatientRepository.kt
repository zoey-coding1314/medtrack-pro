package com.elizabeth.s36639095.medtrack.data.repository

import android.content.Context
import com.elizabeth.s36639095.medtrack.data.MedTrackDatabase
import com.elizabeth.s36639095.medtrack.data.Patient
import com.elizabeth.s36639095.medtrack.data.dao.PatientDao
import kotlinx.coroutines.flow.Flow

class PatientRepository {

    var patientDao: PatientDao

    constructor(context: Context) {
        patientDao = MedTrackDatabase.getDatabase(context).patientDao()
    }

    suspend fun insertPatient(patient: Patient){
        patientDao.insertPatient(patient)
    }
    suspend fun insertPatients(patientList: List<Patient>){
        patientDao.insertPatients(patientList)
    }
    suspend fun deletePatient(patient: Patient) {
        patientDao.deletePatient(patient)
    }
    suspend fun updatePatient(patient: Patient) {
        patientDao.updatePatient(patient)
    }
    suspend fun deletePatientById(id: String) {
        patientDao.deletePatientById(id)
    }
    fun getPatientById(id: String?) : Flow<Patient?> = patientDao.getPatientById(id)

    fun getAllPatients(): Flow<List<Patient>> = patientDao.getAllPatients()

    fun getPatientTotal(): Flow<Int> = patientDao.getPatientTotal()
}