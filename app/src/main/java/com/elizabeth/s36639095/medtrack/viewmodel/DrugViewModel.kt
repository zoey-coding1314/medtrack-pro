package com.elizabeth.s36639095.medtrack.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.elizabeth.s36639095.medtrack.data.network.Drug
import com.elizabeth.s36639095.medtrack.data.repository.DrugRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DrugViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DrugRepository = DrugRepository(application.applicationContext)
    var drugInfo = MutableStateFlow<List<Drug>>(emptyList())

    fun searchedDrug(search: String){
        viewModelScope.launch {
            drugInfo.value = repository.getDrugInfo(search)
        }
    }

    fun isNetworkAvailable(): Boolean {
        return repository.isNetworkAvailable()
    }

    class DrugViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DrugViewModel(context as Application) as T
    }
}