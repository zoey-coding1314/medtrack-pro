package com.elizabeth.s36639095.medtrack.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elizabeth.s36639095.medtrack.data.Patient
import com.elizabeth.s36639095.medtrack.data.repository.PatientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HomePageViewModel(context: Context) : ViewModel(){

    private val patientRepo= PatientRepository(context)

    //Patient
    val allPatients: Flow<List<Patient>> = patientRepo.getAllPatients()

    fun getPatientById(patientId: String?): Flow<Patient?> = patientRepo.getPatientById(patientId)


    fun insertPatient(patient: Patient) = viewModelScope.launch {
        patientRepo.insertPatient(patient)
    }

    fun insertPatientList(patientList: List<Patient>) = viewModelScope.launch {
        patientRepo.insertPatients(patientList)
    }

    fun deletePatient(patient: Patient) = viewModelScope.launch {
        patientRepo.deletePatient(patient)
    }

    fun deletePatientById(patientId: String) = viewModelScope.launch {
        patientRepo.deletePatientById(patientId)
    }

    val totalNumPatient: Flow<Int> = patientRepo.getPatientTotal()

    class HomePageViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomePageViewModel(context) as T
    }

    var accessKey by mutableStateOf("")
        private set

    fun updateAccessKey(key: String) {
        accessKey = key
    }

}