package com.elizabeth.s36639095.medtrack

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.elizabeth.s36639095.medtrack.data.Patient
import com.elizabeth.s36639095.medtrack.ui.theme.MedTrackTheme
import com.elizabeth.s36639095.medtrack.viewmodel.LoginViewModel
import kotlin.jvm.java

class ClaimPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedTrackTheme {
                val loginViewModel: LoginViewModel = ViewModelProvider(
                    this, LoginViewModel.LoginViewModelFactory(applicationContext)
                )[LoginViewModel::class.java]
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ClaimAccountScreen(modifier = Modifier, loginViewModel, innerPadding)
                }
            }
        }
    }
}

@Composable
fun ClaimAccountScreen(modifier: Modifier = Modifier, loginViewModel: LoginViewModel, paddingValues: PaddingValues) {
    val patientId = loginViewModel.patientId
    val phoneNumber = loginViewModel.phoneNumber
    val password = loginViewModel.password
    val confirmPassword = loginViewModel.confirmPassword

    var idError by remember {mutableStateOf(false)}
    var phoneNumberError by remember {mutableStateOf(false)}
    var passwordError by remember { mutableStateOf(false)}

    val context = LocalContext.current
    val allPatient by loginViewModel.allPatients.collectAsState(initial = emptyList())
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Claim Account",
                style = TextStyle(fontSize = 20.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )

            OutlinedTextField(
                value = patientId,
                onValueChange = {
                    loginViewModel.updateID(it)
                    idError = !(it.startsWith("P"))},
                label = { Text(text = "Patient ID") },
                isError = idError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            //Display error message for phone number error
            if (idError) {
                Text(
                    text = "Invalid ID, must start with 'P'.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    loginViewModel.updatePhoneNumber(it)
                    phoneNumberError = !isValidPhoneNumber(it)},
                label = { Text(text = "Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = phoneNumberError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            //Display error message for phone number error
            if (phoneNumberError) {
                Text(
                    text = "Invalid number, must start with 04.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            OutlinedTextField(
                value = password,
                onValueChange = {
                    loginViewModel.updatePassword(it)
                    passwordError = !isValidPassword(it)},
                label = { Text(text = "Set Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = passwordError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            //Display error message for password error
            if(passwordError) {
                Text(
                    text = "Password must be at least 8 characters.\nWith at least 1 letter and 1 number.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    loginViewModel.updateConfirmPassword(it)
                },
                label = { Text(text = "Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = passwordError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (password != confirmPassword){
                Text(
                    text = "Password does not match.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    phoneNumberError = !isValidPhoneNumber(phoneNumber)
                    passwordError = !isValidPassword(password)

                    if (patientId.isEmpty()||phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                        Toast.makeText(
                            context,
                            "All field should be filled",
                            Toast.LENGTH_LONG
                        ).show()

                        //Validate phone number and password
                    } else if (!phoneNumberError && !passwordError && !idError) {
                        var foundId = false
                        var foundPhone = false
                        var claimed = false
                        val passwordMatch = password == confirmPassword

                        allPatient.forEach { user ->
                            if ( user.phoneNumber == phoneNumber && user.patientId == patientId
                                && user.password == "" && passwordMatch ) {
                                foundPhone = true
                                foundId = true

                                val updatedPatient = Patient(
                                    patientId = user.patientId,
                                    phoneNumber = user.phoneNumber,
                                    name = user.name,
                                    password = confirmPassword
                                )
                                loginViewModel.updatePatient(updatedPatient)
                            } else if (user.phoneNumber == phoneNumber && user.patientId == patientId && user.password != "") {
                                claimed = true
                                foundPhone = true
                                foundId = true
                            } else if (user.phoneNumber == phoneNumber && user.patientId == patientId) {
                                foundPhone = true
                                foundId = true
                            } else if (user.phoneNumber == phoneNumber && user.patientId!= patientId ) {
                                foundPhone = true
                            }else if (user.phoneNumber != phoneNumber && user.patientId == patientId ) {
                                foundId = true
                            }
                        }

                        if (!foundId){
                            Toast.makeText(
                                context,
                                "Patient ID does not exist",
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (!foundPhone) {
                            Toast.makeText(
                                context,
                                "Phone Number Not Match",
                                Toast.LENGTH_LONG
                            ).show()
                        }else if (!passwordMatch){
                            Toast.makeText(
                                context,
                                "Confirm Password does not match, please try again.",
                                Toast.LENGTH_LONG
                            ).show()

                        }

                        if (claimed) {
                            Toast.makeText(
                                context,
                                "Account has been claimed",
                                Toast.LENGTH_LONG
                            ).show()
                            loginViewModel.updatePassword("")
                            loginViewModel.updatePhoneNumber("")
                            loginViewModel.updateConfirmPassword("")
                            loginViewModel.updateID("")
                            context.startActivity(Intent(context, LoginPage::class.java))


                        } else if (foundPhone && foundId && passwordMatch) {
                            Toast.makeText(
                                context,
                                "Claim Account Successful.",
                                Toast.LENGTH_LONG
                            ).show()
                            loginViewModel.updatePassword("")
                            loginViewModel.updatePhoneNumber("")
                            loginViewModel.updateConfirmPassword("")
                            context.startActivity(Intent(context, LoginPage::class.java))

                        }

                    }

                }
            ) {
                Text("Claim")
            }
            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = {
                    context.startActivity(Intent(context, SignupPage::class.java))
                }
            ) {
                Text("Don't have an account? Sign Up")
            }

            TextButton(
                onClick = {
                    context.startActivity(Intent(context, LoginPage::class.java))
                }

            ) {
                Text("Back to login")
            }
        }
    }
}


