package com.elizabeth.s36639095.medtrack.viewmodel

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elizabeth.s36639095.medtrack.data.Medication
import com.elizabeth.s36639095.medtrack.data.Patient
import com.elizabeth.s36639095.medtrack.data.repository.MedicationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MedicationPageViewModel(context: Context) : ViewModel() {
    private val medicationRepo = MedicationRepository(context)

    //Medication
    val allMedications: Flow<List<Medication>> = medicationRepo.getAllMedications()

    fun medicationsById(patientID : String?): Flow<List<Medication>> = medicationRepo.getMedicationsById(patientID)

    val totalNumMedication: Flow<Int> = medicationRepo.getMedicationTotal()
    fun insertMedication(medication: Medication) = viewModelScope.launch {
        medicationRepo.insertMedication(medication)
    }

    fun insertMedicationList(medicationList: List<Medication>) = viewModelScope.launch {
        medicationRepo.insertMedications(medicationList)
    }

    fun deleteMedication(medication: Medication) = viewModelScope.launch {
        medicationRepo.deleteMedication(medication)
    }

    fun updateMedication(medication: Medication) = viewModelScope.launch {
        medicationRepo.updateMedication(medication)
    }


    class MedicationPageViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MedicationPageViewModel(context) as T
    }

    var newMedName by mutableStateOf("")
        private set

    fun updateMedName(name: String) {
        newMedName = name
    }

    var newDosage by mutableStateOf("")
        private set

    fun updateDosage(dosage: String) {
        newDosage = dosage
    }

    var newNotes by mutableStateOf("")
        private set

    fun updateNotes(notes: String) {
        newNotes = notes
    }

    @OptIn(ExperimentalMaterial3Api::class)
    var newTime: TimePickerState? by mutableStateOf(null)
        private set

    @OptIn(ExperimentalMaterial3Api::class)
    fun updateTime (time: TimePickerState?) {
        newTime = time
    }

    var newTimeString by mutableStateOf("")
        private set

    fun updateTimeString(time: String) {
        newTimeString = time
    }

    var newFrequency by mutableStateOf("")
        private set

    fun updateFrequency(frequency: String) {
        newFrequency = frequency
    }

    var newType by mutableStateOf("")
        private set

    fun updateType(type: String) {
        newType = type
    }
}