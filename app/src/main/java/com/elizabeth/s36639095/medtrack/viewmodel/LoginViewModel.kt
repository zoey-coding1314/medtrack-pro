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

class LoginViewModel(context: Context) : ViewModel() {
    private val patientRepo= PatientRepository(context)

    //Patient
    val allPatients: Flow<List<Patient>> = patientRepo.getAllPatients()

    var patientId by mutableStateOf("")
        private set

    var phoneNumber by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var confirmPassword by mutableStateOf("")
        private set

    fun updatePhoneNumber(number: String) {
        phoneNumber = number
    }

    fun updateID(number: String) {
        patientId = number
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
    }

    fun updateConfirmPassword(newPassword: String) {
        confirmPassword = newPassword
    }

    fun updatePatient(patient: Patient) = viewModelScope.launch {
        patientRepo.updatePatient(patient)
    }

    class LoginViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            LoginViewModel(context) as T
    }

}