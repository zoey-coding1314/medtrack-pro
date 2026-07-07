package com.elizabeth.s36639095.medtrack.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elizabeth.s36639095.medtrack.data.network.UiState
import com.elizabeth.s36639095.medtrack.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AITipViewModel: ViewModel() {
    private val _tipUiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)

    val tipUiState: StateFlow<UiState> =
        _tipUiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-3-flash-preview",
        apiKey= BuildConfig.apiKey

    )

    fun sendTipPrompt(
        prompt: String
    ){
        _tipUiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content{
                        text(prompt)
                    }
                )
                response.text?.let{ outputContent ->
                    _tipUiState.value = UiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _tipUiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }


    class AITipViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AITipViewModel() as T
    }
}