package com.elizabeth.s36639095.medtrack.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elizabeth.s36639095.medtrack.BuildConfig
import com.elizabeth.s36639095.medtrack.data.HealthReport
import com.elizabeth.s36639095.medtrack.data.network.UiState
import com.elizabeth.s36639095.medtrack.data.repository.HealthReportRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HealthReportViewModel(context: Context) : ViewModel() {
    private val healthReportRepo = HealthReportRepository(context)
    
    val allReports: Flow<List<HealthReport>> = healthReportRepo.getAllReports()
    
    fun insertReport(report: HealthReport) = viewModelScope.launch { 
        healthReportRepo.insertReport(report)
    }
    
    fun deleteReport(report: HealthReport) = viewModelScope.launch {
        healthReportRepo.deleteReport(report)
    }

    fun updateReport(report: HealthReport) = viewModelScope.launch {
        healthReportRepo.updateReport(report)
    }

    class HealthReportViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HealthReportViewModel(context) as T
    }
    
    private val _reportUiState: MutableStateFlow<UiState> = 
        MutableStateFlow(UiState.Initial)

    val reportUiState: StateFlow<UiState> =
        _reportUiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-3-flash-preview",
        apiKey= BuildConfig.apiKey
    )

    fun sendReportPrompt(
        prompt: String
    ){
        _reportUiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content{
                        text(prompt)
                    }
                )
                response.text?.let{ outputContent ->
                    _reportUiState.value = UiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _reportUiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}