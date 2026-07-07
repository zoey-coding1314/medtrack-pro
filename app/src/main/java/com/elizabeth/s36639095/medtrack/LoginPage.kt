package com.elizabeth.s36639095.medtrack

import android.content.Context
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
import com.elizabeth.s36639095.medtrack.ui.theme.MedTrackTheme
import com.elizabeth.s36639095.medtrack.viewmodel.LoginViewModel
import kotlin.jvm.java

class LoginPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedTrackTheme {
                val loginViewModel: LoginViewModel = ViewModelProvider(
                    this, LoginViewModel.LoginViewModelFactory(applicationContext)
                )[LoginViewModel::class.java]
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(modifier = Modifier,loginViewModel, innerPadding)
                }
            }
        }
    }
}


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel,
    paddingValues: PaddingValues
    ) {
    val patientID = loginViewModel.patientId
    val password = loginViewModel.password

    //Error flags for phone number and password validation
    var idError by remember {mutableStateOf(false)}
    var passwordError by remember { mutableStateOf(false)}

    val context = LocalContext.current
    val sharedPref =
        context.getSharedPreferences("MedTrack_sp", Context.MODE_PRIVATE)
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
                text = "Login",
                style = TextStyle(fontSize = 20.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )

            OutlinedTextField(
                value = patientID,
                onValueChange = {
                    loginViewModel.updateID(it)
                    idError = !it.startsWith('P')},
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

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    loginViewModel.updatePassword(it)
                    passwordError = !isValidPassword(it)},
                label = { Text(text = "Password") },
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

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    passwordError = !isValidPassword(password)

                    if (patientID.isEmpty() || password.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Error the fields should not be blank",
                            Toast.LENGTH_LONG
                            ).show()

                    //Validate phone number and password in both CSV and SharePreference users
                    } else if (!idError && !passwordError) {
                        var foundID = false
                        var foundPassword = false
                        var notClaim = false
                        allPatient.forEach { user ->
                            //If both phone and password are found in CSV file, record the user patientID in sharedPreference
                            if (user.patientId == patientID && user.password == ""){
                                notClaim = true
                            } else if ( user.patientId == patientID && user.password == password ) {
                                foundID = true
                                foundPassword = true

                                sharedPref.edit()
                                    .putString("logged_in_patient_id", user.patientId)
                                    .apply()

                            } else if (user.patientId == patientID) {
                                foundID = true
                            }
                        }

                        //If phone number and password are matched, display login successful and go to home page
                        if(notClaim) {
                            Toast.makeText(
                                context,
                                "Account Not Claimed yet",
                                Toast.LENGTH_LONG
                            ).show()
                            context.startActivity(Intent(context, ClaimPage::class.java))

                        }else if (foundID && foundPassword) {
                            Toast.makeText(
                                context,
                                "Login Successful",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(context, HomePage::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)

                        //If only phoneNumber is found but password does not match, display password incorrect message
                        }else if (foundID) {
                            Toast.makeText(
                                context,
                                "Incorrect password",
                                Toast.LENGTH_LONG
                            ).show()

                        //If phone number does not match, display phone number not found message
                        } else {
                            Toast.makeText(
                                context,
                                "No account found with this Patient ID",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }

                }
            ) {
                Text("Login")
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
                    context.startActivity(Intent(context, ClaimPage::class.java))
                }
            ) {
                Text("First-time login? Claim Account")
            }
        }
    }
}


/**
 * Function that check if the phone number is in a valid format.
 * The format of phone is it must start from '04' with length exactly 10
 * @return boolean value
 */
fun isValidPhoneNumber(phoneNumber: String) : Boolean {
    val format = android.util.Patterns.PHONE.matcher(phoneNumber as CharSequence).matches()
            && phoneNumber.startsWith("04") && (phoneNumber.length == 10)

    return format
}

/**
 * Function that check if the password is in a valid format.
 * The format is the password must have more than 8 characters with at least 1 digit and 1 letter.
 * @return boolean value
 */
fun isValidPassword(password : String) : Boolean {
    return (password.length >=8) && password.any { char -> char.isLetter() } && password.any { char -> char.isDigit()}
}

