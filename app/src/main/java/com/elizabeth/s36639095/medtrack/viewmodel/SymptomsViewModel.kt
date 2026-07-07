package com.elizabeth.s36639095.medtrack.viewmodel

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elizabeth.s36639095.medtrack.data.Symptom
import com.elizabeth.s36639095.medtrack.data.repository.SymptomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SymptomsViewModel(context: Context) : ViewModel() {
    private val symptomRepo = SymptomRepository(context)

    val allSymptoms: Flow<List<Symptom>> = symptomRepo.getAllSymptoms()

    fun symptomsById(patientId: String?): Flow<List<Symptom>> = symptomRepo.getSymptomsById(patientId)

    var averageSeverity: Flow<Float>? = symptomRepo.averageSeverity()

    fun averageSeverityByPatientId(patientId: String?): Flow<Float>? = symptomRepo.averageSeverityByPatientId(patientId)

    var mostCategory: Flow<String> = symptomRepo.countedCategory()

    fun commonSymptomById(patientId: String?): Flow<String?> = symptomRepo.getCommonSymptomById(patientId)

    fun insertSymptom(symptom: Symptom) = viewModelScope.launch {
        symptomRepo.insertSymptom(symptom)
    }

    fun insertSymptomList(symptomList: List<Symptom>) = viewModelScope.launch {
        symptomRepo.insertSymptoms(symptomList)
    }

    fun deleteSymptom(symptom: Symptom) = viewModelScope.launch {
        symptomRepo.deleteSymptom(symptom)
    }

    fun updateSymptom(symptom: Symptom) = viewModelScope.launch {
        symptomRepo.updateSymptom(symptom)
    }

    class SymptomsViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private  val context = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SymptomsViewModel(context) as T
    }

    var newCategory by mutableStateOf("")
        private set

    fun updateCategory(category: String) {
        newCategory = category
    }

    var newSeverity by mutableFloatStateOf(5f)
        private set

    fun updateSeverity(severity: Float) {
        newSeverity = severity
    }

    var newNotes by mutableStateOf("")
        private set

    fun updateNotes(notes: String) {
        newNotes = notes
    }

    var newDate by mutableStateOf("")
        private set

    fun updateDate(date: String) {
        newDate = date
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
}