package com.elizabeth.s36639095.medtrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elizabeth.s36639095.medtrack.ui.theme.MedTrackTheme
import java.time.LocalDate
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.AlignVerticalBottom
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elizabeth.s36639095.medtrack.data.Medication
import com.elizabeth.s36639095.medtrack.data.Patient
import com.elizabeth.s36639095.medtrack.data.network.UiState
import com.elizabeth.s36639095.medtrack.viewmodel.AIPatternViewModel
import com.elizabeth.s36639095.medtrack.viewmodel.DrugViewModel
import com.elizabeth.s36639095.medtrack.viewmodel.AITipViewModel
import com.elizabeth.s36639095.medtrack.viewmodel.HealthReportViewModel
import com.elizabeth.s36639095.medtrack.viewmodel.HomePageViewModel
import com.elizabeth.s36639095.medtrack.viewmodel.MedCoachViewModel
import com.elizabeth.s36639095.medtrack.viewmodel.MedicationPageViewModel
import com.elizabeth.s36639095.medtrack.viewmodel.SymptomsViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class HomePage : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedTrackTheme {
                val homePageViewModel: HomePageViewModel = ViewModelProvider(
                    this, HomePageViewModel.HomePageViewModelFactory(applicationContext)
                )[HomePageViewModel::class.java]

                val medicationViewModel: MedicationPageViewModel = ViewModelProvider(
                    this, MedicationPageViewModel.MedicationPageViewModelFactory(applicationContext)
                )[MedicationPageViewModel::class.java]

                val symptomsViewModel: SymptomsViewModel = ViewModelProvider(
                    this, SymptomsViewModel.SymptomsViewModelFactory(applicationContext)
                )[SymptomsViewModel::class.java]

                val drugViewModel: DrugViewModel = ViewModelProvider(
                    this, DrugViewModel.DrugViewModelFactory(applicationContext)
                )[DrugViewModel::class.java]

                val medCoachViewModel: MedCoachViewModel = ViewModelProvider(
                    this, MedCoachViewModel.MedCoachViewModelFactory(applicationContext)
                )[MedCoachViewModel::class.java]

                val aiTipViewModel: AITipViewModel = ViewModelProvider(
                    this, AITipViewModel.AITipViewModelFactory()
                )[AITipViewModel::class.java]

                val aiPatternViewModel: AIPatternViewModel = ViewModelProvider(
                    this, AIPatternViewModel.AIPatternViewModelFactory()
                )[AIPatternViewModel::class.java]

                val healthReportViewModel: HealthReportViewModel = ViewModelProvider(
                    this, HealthReportViewModel.HealthReportViewModelFactory(applicationContext)
                )[HealthReportViewModel::class.java]

                val navController: NavHostController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomBar(navController) },
                    topBar = { TopAppBarHome(navController)}
                ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        NavHost(navController,
                            homePageViewModel,
                            medicationViewModel,
                            symptomsViewModel,
                            drugViewModel,
                            medCoachViewModel,
                            aiTipViewModel,
                            aiPatternViewModel,
                            healthReportViewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarHome(navController: NavHostController) {
    val context = LocalContext.current
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = {
            Text(
                "MedTrack",
                maxLines = 1,
            )
        },

        navigationIcon = {
            IconButton(onClick = {
                val sharePref = context.getSharedPreferences("MedTrack_sp", Context.MODE_PRIVATE).edit()
                sharePref.remove("logged_in_patient_id").apply()
                navController.navigate("home"){
                    popUpTo("home"){ inclusive = true}
                }

                //All activity back stack are removed and WelcomePage(MainActivity) will become the new root activity of the new stack
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)

            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout",
                )

            }
        },
    )

}

@Composable
fun HomeScreen(navController: NavHostController, medicationViewModel: MedicationPageViewModel, homePageViewModel: HomePageViewModel) {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")
    val formattedDate = today.format(formatter)
    val context = LocalContext.current

    val sharedPref = context.getSharedPreferences("MedTrack_sp", Context.MODE_PRIVATE)
    val patientID = sharedPref.getString("logged_in_patient_id", "")
    val allMedication by medicationViewModel.medicationsById(patientID).collectAsState(initial = emptyList())

    val patient by homePageViewModel.getPatientById(patientID).collectAsState(initial = Patient("","","",""))
    val medications = allMedication
        .distinctBy { it.name + it.time }
        .sortedBy { it.time }

    val totalMed = medications.size
    val takenCount = medications.count{it.taken}
    val todayDate = LocalDate.now()
//        .plusDays(1)     //Test: Simulate the next day
    LaunchedEffect(medications) {
        medications.forEach { medication ->
            if(medication.taken && medication.takenDate != todayDate.toString()){
                val updatedMedication = medication.copy(
                    taken = false,
                    takenDate = ""
                )
                medicationViewModel.updateMedication(updatedMedication)
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Hello, ${patient?.name}",
                fontSize = 23.sp
            )

            Text(
                text = "$formattedDate    Patient ID: $patientID",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly)
            {
                // Add medication button
                Button(onClick = {
                    navController.navigate("addMedication")
                }) {
                    Icon(Icons.Outlined.AddCircle, contentDescription = "Add Medication")
                    Text("Add Medication")
                }

                //Original Feature Entry: Generate User Report

                Button(onClick = {
                    navController.navigate("userReport")
                }) {
                    Icon(Icons.Outlined.AlignVerticalBottom, contentDescription = "Generate Report")
                    Text("Generate AI Report")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

        }
        if (medications.isEmpty()) {
            item {
                Text(
                    text = "No medications scheduled.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(20.dp)
                )
            }
        } else {
            item {
                Text(
                    text = "Medications Scheduled",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            items(medications) { medication ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (medication.taken) {
                            MaterialTheme.colorScheme.onTertiary
                        } else {
                            MaterialTheme.colorScheme.tertiaryContainer
                        }
                    )
                ) {
                    Column {
                        Text(
                            medication.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Dosage: ${medication.dosage}",
                                modifier = Modifier.padding(8.dp)
                            )
                            Text(
                                "Frequency: ${medication.frequency}",
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Time: ${medication.time}", modifier = Modifier.padding(8.dp))
                            Checkbox(
                                checked = medication.taken,
                                onCheckedChange = { checked ->
                                    val updatedMedication = medication.copy(
                                        taken = checked,
                                        takenDate = if (checked) {
                                            todayDate.toString()
                                        } else ""
                                    )
                                    medicationViewModel.updateMedication(updatedMedication)
                                }

                            )
                        }

                    }
                }
            }

            //Display summary of the medication scheduled
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        thickness = 2.dp
                    )

                    Text(
                        text = "Summary: $takenCount of $totalMed medications taken today.",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationScreen(navController: NavHostController, medicationViewModel: MedicationPageViewModel) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val medicationName = medicationViewModel.newMedName
    val dosage = medicationViewModel.newDosage
    var dosageError by remember { mutableStateOf(false)}
    val dosageRegex by remember { mutableStateOf("""^\d+(\.\d+)?(mg|ml|g)$""".toRegex()) }
    val notes = medicationViewModel.newNotes

    val selectedTime = medicationViewModel.newTime
    val timeString = medicationViewModel.newTimeString
    var showMenu by remember { mutableStateOf(true) }
    var showDialExample by remember { mutableStateOf(false) }
    val formatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    var expanded by remember{ mutableStateOf(false)}
    val frequency = listOf("Once daily", "Twice daily", "Three times daily", "As needed")
    val selectedFrequency = medicationViewModel.newFrequency
    var textFieldSize1 by remember { mutableStateOf(IntSize.Zero)}

    var expanded2 by remember{ mutableStateOf(false)}
    val medicationType = listOf("Tablet", "Capsule", "Liquid", "Injection", "Topical", "Other")
    val selectedType = medicationViewModel.newType
    var textFieldSize2 by remember { mutableStateOf(IntSize.Zero)}

    val icon = if(expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("MedTrack_sp", Context.MODE_PRIVATE)
    val patientID = sharedPref.getString("logged_in_patient_id", "") ?: ""

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState)}
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        "Add Medication",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    TextField(
                        value = medicationName,
                        onValueChange = { medicationViewModel.updateMedName(it) },
                        label = { Text("Medication Name") },
                        modifier = Modifier.padding(8.dp)
                    )

                    OutlinedTextField(
                        value = dosage,
                        onValueChange = {
                            medicationViewModel.updateDosage(it)
                            dosageError = !dosageRegex.matches(it)
                        },
                        label = { Text(text = "Dosage") },
                        isError = dosageError,
                        modifier = Modifier.padding(8.dp),
                        singleLine = true
                    )

                    if (dosageError) {
                        Text(
                            text = "Dosage must include units e.g. ml, g, mg",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    //Frequency Dropdown menu
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        OutlinedTextField(
                            value = selectedFrequency,
                            onValueChange = { medicationViewModel.updateFrequency(it) },
                            readOnly = true,
                            modifier = Modifier
                                .padding(8.dp)
                                .onGloballyPositioned { coordinates ->
                                    textFieldSize1 = coordinates.size
                                },
                            label = { Text("Select Frequency") },
                            trailingIcon = {
                                Icon(
                                    icon, "contentDescription",
                                    Modifier.clickable { expanded = !expanded })
                            }
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .width(
                                    with(LocalDensity.current) {
                                        textFieldSize1.width.toDp()
                                    }
                                )
                        ) {
                            frequency.forEach { label ->
                                DropdownMenuItem(
                                    text = { Text(text = label) },
                                    onClick = {
                                        medicationViewModel.updateFrequency(label)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    //Medication type Dropdown menu
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        OutlinedTextField(
                            value = selectedType,
                            onValueChange = { medicationViewModel.updateType(it) },
                            readOnly = true,
                            modifier = Modifier
                                .padding(8.dp)
                                .onGloballyPositioned { coordinates ->
                                    textFieldSize2 = coordinates.size
                                },
                            label = { Text("Select Medication Type") },
                            trailingIcon = {
                                Icon(
                                    icon, "contentDescription",
                                    Modifier.clickable { expanded2 = !expanded2 })
                            }
                        )

                        DropdownMenu(
                            expanded = expanded2,
                            onDismissRequest = { expanded2 = false },
                            modifier = Modifier
                                .width(
                                    with(LocalDensity.current) {
                                        textFieldSize2.width.toDp()
                                    }
                                )
                        ) {
                            medicationType.forEach { label ->
                                DropdownMenuItem(
                                    text = { Text(text = label) },
                                    onClick = {
                                        medicationViewModel.updateType(label)
                                        expanded2 = false
                                    }
                                )
                            }
                        }
                    }

                    //TimePicker
                    Box(
                        Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ){
                        if (showMenu){
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                            ) {
                                Button(onClick = {
                                    showDialExample = true
                                    showMenu = false
                                }) {
                                    Text("Select Time")
                                }
                            }
                        }
                        when {
                            showDialExample -> TimePickerDial(
                                onDismiss = {
                                    showDialExample = false
                                    showMenu = true
                                },
                                onConfirm = { time ->
                                    medicationViewModel.updateTime(time)
                                    showDialExample = false
                                    showMenu = true
                                },
                            )
                        }
                    }
                    if (selectedTime != null) {
                        val cal = Calendar.getInstance()
                        cal.set(Calendar.HOUR_OF_DAY, selectedTime.hour)
                        cal.set(Calendar.MINUTE, selectedTime.minute)
                        cal.isLenient = false
                        medicationViewModel.updateTimeString(formatter.format(cal.time))
                        Text("Selected time: $timeString")
                    } else {
                        Text("No time selected.")
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    TextField(
                        value = notes,
                        onValueChange = { medicationViewModel.updateNotes(it) },
                        label = { Text("Notes (Optional)") },
                        modifier = Modifier.padding(8.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        //Save Button
                        Button(onClick = {
                            if (medicationName.isEmpty() || dosage.isEmpty() || timeString.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Error. The required fields should not be blank.",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                val newMedication = Medication(
                                    patientId = patientID,
                                    name = medicationName,
                                    dosage = dosage,
                                    frequency = selectedFrequency,
                                    time = timeString,
                                    type = selectedType,
                                    taken = false,
                                    notes = notes,
                                    takenDate = ""
                                )

                                medicationViewModel.insertMedication(newMedication)
                                medicationViewModel.updateMedName("")
                                medicationViewModel.updateDosage("")
                                medicationViewModel.updateNotes("")
                                medicationViewModel.updateTimeString("")
                                medicationViewModel.updateFrequency("")
                                medicationViewModel.updateType("")
                                scope.launch { snackBarHostState.showSnackbar("Medication Saved!") }
                                navController.navigate("home")
                            }

                        }) {
                            Text("Save")
                        }

                        //Clear Button
                        Button(onClick = {
                            medicationViewModel.updateMedName("")
                            medicationViewModel.updateDosage("")
                            medicationViewModel.updateNotes("")
                            medicationViewModel.updateTimeString("")
                            medicationViewModel.updateFrequency("")
                            medicationViewModel.updateType("")
                        }) {
                            Text("Clear")
                        }
                    }
                }
            }

        }
    }
}


@Composable
fun SettingScreen(navController: NavHostController, homePageViewModel: HomePageViewModel) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("MedTrack_sp", Context.MODE_PRIVATE)
    val patientID = sharedPref.getString("logged_in_patient_id", "")
    val patient by homePageViewModel.getPatientById(patientID).collectAsState(initial = Patient("","","",""))

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = patient?.name ?: "User",
            fontSize = 25.sp,
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = "Phone: ${patient?.phoneNumber}",
            fontSize = 19.sp
        )

        Text(
            text = "Patient ID: ${patient?.patientId}",
            fontSize = 19.sp
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 20.dp),
            thickness = 2.dp
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                val sharePref = context.getSharedPreferences("MedTrack_sp", Context.MODE_PRIVATE).edit()
                sharePref.remove("logged_in_patient_id").apply()
                navController.navigate("home"){
                    popUpTo("home"){ inclusive = true}
                }

                val intent = Intent(context, LoginPage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)

            }){
                Text("Logout")
            }

            Button(onClick = {
                navController.navigate("clinicianDashboard")
            }) {
                Text("Clinician Login")
            }
        }
    }
}

@Composable
fun ClinicianDashboard(
    medicationPageViewModel: MedicationPageViewModel,
    homePageViewModel: HomePageViewModel,
    symptomsViewModel: SymptomsViewModel,
    aiPatternViewModel: AIPatternViewModel) {

    var accessKey by remember { mutableStateOf("dollar-entry-apples") }
    val keyInput = homePageViewModel.accessKey
    var successLogin by remember { mutableStateOf(false) }
    val context = LocalContext.current

    //Aggregation
    val numPatient by homePageViewModel.totalNumPatient.collectAsState(initial = 1)
    val numMedication by medicationPageViewModel.totalNumMedication.collectAsState(initial = 1)
    val averageMed = numMedication/numPatient.toFloat()
    val averageSeverity by symptomsViewModel.averageSeverity!!.collectAsState(initial = null)
    val mostCategory by symptomsViewModel.mostCategory.collectAsState(initial = "")
    val symptomsInfo = symptomsViewModel.allSymptoms.collectAsState(emptyList()).value.joinToString("\n") { symptom ->

        "Category: ${symptom.category}" +
                "Severity: ${symptom.severity}" +
                "Notes: ${symptom.notes}" +
                "Date: ${symptom.dateTime}"
            .trimIndent()
    }
    val prompt =
        "Give 3 interesting simple patterns, observations, or insights about the data (not the app) in a list separated using ',' for easier splitting. " +
                "\nData: 1. Number of Patient in this MedTrack App: $numPatient \n" +
                "2. Average medication must be taken daily per patient: $averageMed \n" +
                "3. Average severity rate(1-10) of all patient symptoms: $averageSeverity \n" +
                "4. The most common symptom is $mostCategory \n" +
                "5. All symptoms info: $symptomsInfo"
    val uiState by aiPatternViewModel.patternUiState.collectAsState()
    val placeholderResult = stringResource(R.string.results_placeholder)
    var result by rememberSaveable { mutableStateOf(placeholderResult) }


    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Clinician Login",
                    style = TextStyle(fontSize = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )

                OutlinedTextField(
                    value = keyInput,
                    onValueChange = { homePageViewModel.updateAccessKey(it) },
                    label = { Text(text = "Access Key") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (keyInput.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Error the field should not be blank",
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (keyInput == accessKey) {
                            Toast.makeText(
                                context,
                                "Login Successful",
                                Toast.LENGTH_LONG
                            ).show()
                            successLogin = true
                            homePageViewModel.updateAccessKey("")

                        } else {
                            Toast.makeText(
                                context,
                                "Incorrect Access Key",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                ) {
                    Text("Login")
                }
                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 20.dp),
                    thickness = 2.dp
                )

                if (successLogin) {
                    Text(
                        text = "Clinician Dashboard",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column {

                            Text(
                                text = "Total Number of Patient: $numPatient",
                                style = TextStyle(fontSize = 15.sp),
                                modifier = Modifier.padding(16.dp)
                            )

                            Text(
                                text = "Average Number of Medications per Patient: $averageMed",
                                style = TextStyle(fontSize = 15.sp),
                                modifier = Modifier.padding(16.dp)
                            )
                            Text(
                                text = "Most Common Symptom: $mostCategory",
                                style = TextStyle(fontSize = 15.sp),
                                modifier = Modifier.padding(16.dp)
                            )
                            Text(
                                text = "Average Symptom Severity: $averageSeverity",
                                style = TextStyle(fontSize = 15.sp),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    Text(
                        text = "Pattern Analysis",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(16.dp)
                    )

                    //Find Pattern Button
                    Button(
                        onClick = {
                            aiPatternViewModel.sendPatternPrompt(prompt)
                        },
                        enabled = prompt.isNotEmpty(),
                        modifier = Modifier
                    ) {
                        Text(text = "Find Patterns")
                    }

                    if (uiState is UiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier)
                    } else {
                        var textColor = MaterialTheme.colorScheme.onSurface

                        if (uiState is UiState.Error) {
                            textColor = MaterialTheme.colorScheme.error
                            result = (uiState as UiState.Error).errorMessage
                        } else if (uiState is UiState.Success) {
                            textColor = MaterialTheme.colorScheme.onSurface
                            result = (uiState as UiState.Success).outputText

                        }

                        if(result.isNotEmpty()){

                            val splitResult = result.split(',')
                            splitResult.forEachIndexed { index, string ->
                                Text(
                                    text = "${index+1}. $string",
                                    textAlign = TextAlign.Start,
                                    color = textColor,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxSize()
                                )

                            }

                        }


                    }


                }

            }

        }

    }

}



@Composable
fun BottomBar(navController: NavHostController) {
    var selectedItem by remember { mutableIntStateOf(0) }

    val items = listOf(
        "home",
        "symptomPage",
        "medCoachPage",
        "settingsPage"
    )
    NavigationBar{
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    when(item) {
                        "home" -> Icon(Icons.Filled.Home, contentDescription = "Home")
                        "symptomPage" -> Icon(Icons.Filled.Medication, contentDescription = "Add Symptoms")
                        "medCoachPage" -> Icon(Icons.Filled.AccessibilityNew, contentDescription = "MedCoach")
                        "settingsPage" -> Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                label = {
                    Text(
                        when (item) {
                            "home" -> "Home"
                            "symptomPage" -> "Add Symptom"
                            "medCoachPage" -> "MedCoach"
                            else -> ("Settings")
                        }
                    ) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    navController.navigate(item)

                }
            )
        }
    }
}

@Composable
fun NavHost(navController: NavHostController,
            homePageViewModel: HomePageViewModel,
            medicationPageViewModel: MedicationPageViewModel,
            symptomsViewModel: SymptomsViewModel,
            drugViewModel: DrugViewModel,
            medCoachViewModel: MedCoachViewModel,
            aiTipViewModel: AITipViewModel,
            aiPatternViewModel: AIPatternViewModel,
            healthReportViewModel: HealthReportViewModel
            ) {
    androidx.navigation.compose.NavHost(
        navController = navController,
        //Set the starting destination to "home"
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController,medicationPageViewModel, homePageViewModel)
        }
        composable("addMedication") {
            AddMedicationScreen(navController, medicationPageViewModel )
        }
        composable("symptomPage") {
            SymptomsScreen(symptomsViewModel)
        }
        composable("settingsPage") {
            SettingScreen(navController, homePageViewModel)
        }
        composable("medCoachPage") {
            MedCoach(medicationPageViewModel,symptomsViewModel,homePageViewModel, drugViewModel, aiTipViewModel,medCoachViewModel)
        }
        composable("clinicianDashboard") {
            ClinicianDashboard(medicationPageViewModel,homePageViewModel, symptomsViewModel, aiPatternViewModel )
        }
        composable("userReport"){
            UserReport( homePageViewModel,medicationPageViewModel,symptomsViewModel, healthReportViewModel)
        }

    }
}



