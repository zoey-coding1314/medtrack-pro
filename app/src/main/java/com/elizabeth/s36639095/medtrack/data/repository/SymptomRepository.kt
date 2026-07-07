package com.elizabeth.s36639095.medtrack.data.repository

import android.content.Context
import com.elizabeth.s36639095.medtrack.data.MedTrackDatabase
import com.elizabeth.s36639095.medtrack.data.Symptom
import com.elizabeth.s36639095.medtrack.data.dao.SymptomDao
import kotlinx.coroutines.flow.Flow

class SymptomRepository {

    var symptomDao: SymptomDao

    constructor(context: Context) {
        symptomDao = MedTrackDatabase.getDatabase(context).symptomDao()
    }

    suspend fun insertSymptom(symptom: Symptom){
        symptomDao.insertSymptom(symptom)
    }
    suspend fun insertSymptoms(symptomList: List<Symptom>){
        symptomDao.insertSymptoms(symptomList)
    }
    suspend fun deleteSymptom(symptom: Symptom) {
        symptomDao.deleteSymptom(symptom)
    }
    suspend fun updateSymptom(symptom: Symptom) {
        symptomDao.updateSymptom(symptom)
    }
    fun getAllSymptoms(): Flow<List<Symptom>> = symptomDao.getAllSymptoms()

    fun getSymptomsById(patientId: String?): Flow<List<Symptom>> = symptomDao.getSymptomsById(patientId)

    fun averageSeverity(): Flow<Float>? = symptomDao.averageSeverity()

    fun averageSeverityByPatientId(patientId: String?): Flow<Float>? = symptomDao.averageSeverityByPatientId(patientId)


    fun countedCategory(): Flow<String> = symptomDao.countedCategory()

    fun getCommonSymptomById(patientId: String?): Flow<String?> = symptomDao.mostCommonSymptom(patientId)
}