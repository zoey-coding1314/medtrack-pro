package com.elizabeth.s36639095.medtrack.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elizabeth.s36639095.medtrack.data.MedCoachTip
import com.elizabeth.s36639095.medtrack.data.repository.MedCoachTipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MedCoachViewModel(context: Context) : ViewModel() {
    private val medCoachRepo = MedCoachTipRepository(context)
    
    val allTips: Flow<List<MedCoachTip>> = medCoachRepo.getAllTips()

    fun insertTip(tip: MedCoachTip) = viewModelScope.launch {
        medCoachRepo.insertTip(tip)
    }

    fun insertTipList(tipList: List<MedCoachTip>) = viewModelScope.launch {
        medCoachRepo.insertTips(tipList)
    }

    fun deleteTip(tip: MedCoachTip) = viewModelScope.launch {
        medCoachRepo.deleteTip(tip)
    }

    fun updateTip(tip: MedCoachTip) = viewModelScope.launch {
        medCoachRepo.updateTip(tip)
    }

    class MedCoachViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MedCoachViewModel(context) as T
    }

    var searchInput by mutableStateOf("")
        private set

    fun updateSearchInput(input: String) {
        searchInput = input
    }
}