package com.elizabeth.s36639095.medtrack.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SignupViewModel(): ViewModel() {

    var fullName by mutableStateOf("")
        private set
    var phoneNumber by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var confirmPassword by mutableStateOf("")
        private set

    fun updateFullName(name: String){
        fullName = name
    }

    fun updatePhoneNumber(number: String) {
        phoneNumber = number
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
    }

    fun updateConfirmPassword(newPassword: String) {
        confirmPassword = newPassword
    }

    class SignUpViewModelFactory(context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SignupViewModel() as T
    }
}