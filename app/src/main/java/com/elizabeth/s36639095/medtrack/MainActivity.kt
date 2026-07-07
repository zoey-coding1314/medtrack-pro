package com.elizabeth.s36639095.medtrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.ViewModelProvider
import com.elizabeth.s36639095.medtrack.data.Medication
import com.elizabeth.s36639095.medtrack.data.Patient
import com.elizabeth.s36639095.medtrack.data.Symptom
import com.elizabeth.s36639095.medtrack.ui.theme.MedTrackTheme
import com.elizabeth.s36639095.medtrack.viewmodel.HomePageViewModel
import com.elizabeth.s36639095.medtrack.viewmodel.MedicationPageViewModel
import com.elizabeth.s36639095.medtrack.viewmodel.SymptomsViewModel
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.sequences.forEach

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedTrackTheme {
                val context = LocalContext.current
                val homePageViewModel: HomePageViewModel = ViewModelProvider(
                    this, HomePageViewModel.HomePageViewModelFactory(applicationContext)
                )[HomePageViewModel::class.java]

                val medicationViewModel: MedicationPageViewModel = ViewModelProvider(
                    this, MedicationPageViewModel.MedicationPageViewModelFactory(applicationContext)
                )[MedicationPageViewModel::class.java]

                val symptomsViewModel: SymptomsViewModel = ViewModelProvider(
                    this, SymptomsViewModel.SymptomsViewModelFactory(applicationContext)
                )[SymptomsViewModel::class.java]

                LaunchedEffect(Unit) {
                    databaseSeeding(context,homePageViewModel, medicationViewModel, symptomsViewModel)
                }

                //Checking sharedPreference if the user has logged in
                val sharedPref = context.getSharedPreferences("MedTrack_sp",Context.MODE_PRIVATE)
                val loginID = sharedPref.getString("logged_in_patient_id", null)
                //If loginID is available, start HomePage directly without WelcomePage.
                if (loginID != null){
                    context.startActivity(Intent(context, HomePage::class.java))
                    (context as? ComponentActivity)?.finish()

                } else {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        WelcomePage(innerPadding)
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomePage(innerPadding: PaddingValues) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.medtrack_logo),
            contentDescription = "MedTrack Logo",
            modifier = Modifier.size(200.dp)
        )

        Text(
            text = "MedTrack",
            style = TextStyle(fontSize = 30.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "Medication clarity, every day.",
            style = TextStyle(fontSize = 22.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                context.startActivity(Intent(context, LoginPage::class.java))
            }) {
                Text("Log in")
            }

            Button(onClick = {
                context.startActivity(Intent(context, SignupPage::class.java))
            }) {
                Text("Sign up")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            buildAnnotatedString {
                append("Visit ")
                withLink(
                    LinkAnnotation.Url(
                        url = "https://monashhealth.org/",
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = Color.Blue,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    )
                ) {
                    append("Monash Health Clinic")
                }
            }
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "Elizabeth Oang Zhu Yin (36639095)",
            style = TextStyle(fontSize = 15.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(10.dp)
        )
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter){
        Text(
            text = "This app is for tracking purposes only and does not replace professional medical advice.",
            style = TextStyle(fontSize = 15.sp),
            modifier = Modifier.padding(14.dp)
        )
    }

}

//Functions used across different activity

/**
 * TimePickerDial is a basic time picker that does not use dialog and modal, but
 * solely time picker in 24hours format
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDial(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    Column {
        TimePicker(
            state = timePickerState,
        )
        Button(onClick = onDismiss) {
            Text("Dismiss picker")
        }
        Button(onClick = { onConfirm(timePickerState) }) {
            Text("Confirm selection")
        }
    }
}

/**
 * A date picker that opens a docked date picker below the input field,
 * without using modal and dialog.
 * @return string selectedDate for future use
 */
@Composable
fun datePickerDocked(): String {
    var showDatePicker by remember { mutableStateOf(false)}
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    Box(
        modifier = Modifier.padding(8.dp)
    ){
        OutlinedTextField(
            value = selectedDate,
            onValueChange = {},
            label = { Text("Select Date")},
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker}) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select Date"
                    )
                }
            },
            modifier = Modifier
                .padding(8.dp)
                .height(64.dp)
        )

        if(showDatePicker){
            Popup(
                onDismissRequest = { showDatePicker = false},
                alignment = Alignment.TopStart
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 64.dp)
                        .shadow(elevation = 4.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ){
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false
                    )
                }
            }
        }
    }
    return selectedDate
}

/**
 * Convert function for date selected
 */
fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(Date(millis))
}

fun loadAllPatientsFromCsv(context: Context): List<Patient> {
    val patients = mutableListOf<Patient>()
    val assets = context.assets
    try{
        val inputStream = assets.open("patients.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.useLines{ lines ->
            lines.drop(1).forEach { line ->
                val values = line.split(",")
                val patient = Patient(
                    patientId = values[0].trim(),
                    phoneNumber = values[1].trim(),
                    name = values[2].trim(),
                    password = ""
                )
                patients.add(patient)
            }
        }
    }catch (e: Exception){}

    return patients
}

fun loadAllMedicationsFromCsv(context: Context): List<Medication> {
    val medications = mutableListOf<Medication>()
    val assets = context.assets
    try{
        val inputStream = assets.open("medications.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.useLines{ lines ->
            lines.drop(1).forEach { line ->
                val values = line.split(",")
                val medication = Medication(
                    patientId = values[0].trim(),
                    name = values[1].trim(),
                    dosage = values[2].trim(),
                    frequency = values[3].trim(),
                    time = values[4].trim(),
                    type = values[5].trim(),
                    taken = false,
                    notes = values[6].trim(),
                    takenDate = ""
                )
                medications.add(medication)
            }
        }
    }catch (e: Exception){}

    return medications
}

fun loadAllSymptomsFromCsv(context: Context): List<Symptom> {
    val symptoms = mutableListOf<Symptom>()
    val assets = context.assets
    try{
        val inputStream = assets.open("symptoms.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.useLines{ lines ->
            lines.drop(1).forEach { line ->
                val values = line.split(",")
                val symptom = Symptom(
                    patientId = values[0].trim(),
                    category = values[1].trim(),
                    severity = values[2].toInt(),
                    notes = values
                        .drop(3)
                        .dropLast(1)
                        .joinToString(",")
                        .trim()
                        .trim('"'),
                    dateTime = values.last().trim()
                )
                symptoms.add(symptom)
            }
        }
    }catch (e: Exception){}

    return symptoms
}

fun databaseSeeding(context: Context, homePageViewModel: HomePageViewModel, medicationPageViewModel: MedicationPageViewModel, symptomsViewModel: SymptomsViewModel){
    val sharePref = context.getSharedPreferences("MedTrack_sp", Context.MODE_PRIVATE)
    if (!sharePref.getBoolean("db_seeded", false)){

        val patientList = loadAllPatientsFromCsv(context)
        val medicationList = loadAllMedicationsFromCsv(context)
        val symptomList = loadAllSymptomsFromCsv(context)

        homePageViewModel.insertPatientList(patientList)
        medicationPageViewModel.insertMedicationList(medicationList)
        symptomsViewModel.insertSymptomList(symptomList)

        sharePref.edit().putBoolean("db_seeded", true).apply()
    }
}