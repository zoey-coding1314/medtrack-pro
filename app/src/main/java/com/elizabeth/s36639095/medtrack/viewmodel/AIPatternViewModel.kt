package com.elizabeth.s36639095.medtrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elizabeth.s36639095.medtrack.BuildConfig
import com.elizabeth.s36639095.medtrack.data.network.UiState
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AIPatternViewModel: ViewModel() {
    private val _patternUiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)

    val patternUiState: StateFlow<UiState> =
        _patternUiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-3-flash-preview",
        apiKey= BuildConfig.apiKey

    )

    fun sendPatternPrompt(
        prompt: String
    ){
        _patternUiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content{
                        text(prompt)
                    }
                )
                response.text?.let{ outputContent ->
                    _patternUiState.value = UiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _patternUiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }


    class AIPatternViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AIPatternViewModel() as T
    }
}