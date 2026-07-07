package com.elizabeth.s36639095.medtrack.data.repository

import android.content.Context
import com.elizabeth.s36639095.medtrack.data.MedTrackDatabase
import com.elizabeth.s36639095.medtrack.data.Medication
import com.elizabeth.s36639095.medtrack.data.dao.MedicationDao
import kotlinx.coroutines.flow.Flow

class MedicationRepository {

    var medicationDao: MedicationDao

    constructor(context: Context) {
        medicationDao = MedTrackDatabase.getDatabase(context).medicationDao()
    }

    suspend fun insertMedication(medication: Medication){
        medicationDao.insertMedication(medication)
    }
    suspend fun insertMedications(medicationList: List<Medication>){
        medicationDao.insertMedications(medicationList)
    }
    suspend fun deleteMedication(medication: Medication) {
        medicationDao.deleteMedication(medication)
    }
    suspend fun updateMedication(medication: Medication) {
        medicationDao.updateMedication(medication)
    }
    fun getAllMedications(): Flow<List<Medication>> = medicationDao.getAllMedications()

    fun getMedicationsById(patientId: String?): Flow<List<Medication>> = medicationDao.getMedicationsById(patientId)

    fun getMedicationTotal(): Flow<Int> = medicationDao.getMedicationTotal()

}