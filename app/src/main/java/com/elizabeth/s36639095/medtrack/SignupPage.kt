package com.elizabeth.s36639095.medtrack

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.elizabeth.s36639095.medtrack.viewmodel.HomePageViewModel
import com.elizabeth.s36639095.medtrack.viewmodel.SignupViewModel
import kotlinx.coroutines.launch


class SignupPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedTrackTheme {
                val homePageViewModel: HomePageViewModel = ViewModelProvider(
                    this, HomePageViewModel.HomePageViewModelFactory(applicationContext)
                )[HomePageViewModel::class.java]
                val signupViewModel: SignupViewModel = ViewModelProvider(
                    this, SignupViewModel.SignUpViewModelFactory(applicationContext)
                )[SignupViewModel::class.java]
                SignupScreen(modifier = Modifier, homePageViewModel, signupViewModel)
            }
        }
    }
}

@Composable
fun SignupScreen(modifier: Modifier = Modifier, homePageViewModel: HomePageViewModel, signupViewModel: SignupViewModel) {
    val context = LocalContext.current
    val allUser by homePageViewModel.allPatients.collectAsState(initial = emptyList())
    val fullName = signupViewModel.fullName
    val phoneNumber = signupViewModel.phoneNumber
    val password = signupViewModel.password
    val confirmPassword = signupViewModel.confirmPassword

    var phoneNumberError by remember {mutableStateOf(false)}
    var foundPhone by remember {mutableStateOf(false)}
    var passwordError by remember { mutableStateOf(false)}

    val scope = rememberCoroutineScope()
    val snackBarHostState = remember{SnackbarHostState()}

    val newPatientID = calculateNewID(allUser)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) }) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background,
            ) { 
            Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) { 
                Text(
                        text = "Sign Up",
                        style = TextStyle(fontSize = 20.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
    
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {signupViewModel.updateFullName(it)},
                    label = {Text(text = "Full Name (Required)")},
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if(fullName.isEmpty()) {
                    Text(
                        text = "This field is required.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        signupViewModel.updatePhoneNumber(it)
                        phoneNumberError = !isValidPhoneNumber(it)},
                    label = { Text(text = "Phone Number (Required)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = phoneNumberError,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (phoneNumberError) {
                    Text(
                        text = "Invalid number, must start with 04.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }else if(phoneNumber.isEmpty()){
                    Text(
                        text = "This field is required.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }else {
                    foundPhone = false

                    allUser.forEach { user ->
                        if(user.phoneNumber == phoneNumber){
                            foundPhone = true
                        }
                    }

                    if (foundPhone) {
                        Text(
                            text = "Phone number already exist.",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        signupViewModel.updatePassword(it)
                        passwordError = !isValidPassword(it)},
                    label = { Text(text = "Password (Required)") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = passwordError,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if(passwordError) {
                    Text(
                        text = "Password must be at least 8 characters.\nWith at least 1 letter and 1 number.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }else if(password.isEmpty()){
                    Text(
                        text = "This field is required.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        signupViewModel.updateConfirmPassword(it)
                    },
                    label = { Text(text = "Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = passwordError,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if(confirmPassword.isEmpty()){
                    Text(
                        text = "This field is required.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }else if (password != confirmPassword){
                    Text(
                        text = "Password does not match.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                //Sign up Button
                Button(
                    onClick = {
                        phoneNumberError = !isValidPhoneNumber(phoneNumber)
                        passwordError = !isValidPassword(password)

                        if (fullName.isEmpty()|| phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Error the fields should not be blank",
                                Toast.LENGTH_LONG
                            ).show()

                        //Validate all required fields
                        } else if (!phoneNumberError && !passwordError && !foundPhone && password == confirmPassword) {
                            val newPatient = com.elizabeth.s36639095.medtrack.data.Patient(
                                patientId = newPatientID,
                                phoneNumber = phoneNumber,
                                name = fullName,
                                password = password)
                            //Add new user into the user list in the sharedPreference
                            homePageViewModel.insertPatient(newPatient)

                            signupViewModel.updateFullName("")
                            signupViewModel.updatePhoneNumber("")
                            signupViewModel.updatePassword("")
                            signupViewModel.updateConfirmPassword("")
                            scope.launch {
                                snackBarHostState.showSnackbar("Account Created! Your ID: $newPatientID", duration = SnackbarDuration.Short)
                                context.startActivity(Intent(context, LoginPage::class.java))
                            }

                        }
                    }
                ) {
                    Text("Sign Up")
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(
                    onClick = {
                        signupViewModel.updateFullName("")
                        signupViewModel.updatePhoneNumber("")
                        signupViewModel.updatePassword("")
                        signupViewModel.updateConfirmPassword("")
                        context.startActivity(Intent(context, LoginPage::class.java))
                    }
                ) {
                    Text("Already have an account? Login")
                }
            }
        }
    }

}

/**
 * Function that get the latest Patient ID by going through both the CSV
 * and sharedPreference files and increment it.
 * @return the new patientID
 */
fun calculateNewID( allPatient: List<com.elizabeth.s36639095.medtrack.data.Patient>): String {
    var lastID = ""
    if(allPatient.isNotEmpty()) {
        lastID = allPatient.last().patientId
        val newID = "P" + (lastID.slice(1..4).toInt() + 1)
        return newID
    }

    return lastID
}

