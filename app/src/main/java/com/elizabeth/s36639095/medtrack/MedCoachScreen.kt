package com.elizabeth.s36639095.medtrack

import android.content.Context
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elizabeth.s36639095.medtrack.data.MedCoachTip
import com.elizabeth.s36639095.medtrack.data.Patient
import com.elizabeth.s36639095.medtrack.data.network.UiState
import com.elizabeth.s36639095.medtrack.viewmodel.DrugViewModel
import com.elizabeth.s36639095.medtrack.viewmodel.AITipViewModel
import com.elizabeth.s36639095.medtrack.viewmodel.HomePageViewModel
import com.elizabeth.s36639095.medtrack.viewmodel.MedCoachViewModel
import com.elizabeth.s36639095.medtrack.viewmodel.MedicationPageViewModel
import com.elizabeth.s36639095.medtrack.viewmodel.SymptomsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedCoach(medicationPageViewModel: MedicationPageViewModel,
             symptomsViewModel: SymptomsViewModel,
             homePageViewModel: HomePageViewModel,
             drugViewModel: DrugViewModel,
             aiTipViewModel: AITipViewModel= viewModel(),
             medCoachViewModel: MedCoachViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val sharedPref = context.getSharedPreferences("MedTrack_sp", Context.MODE_PRIVATE)
    val patientID = sharedPref.getString("logged_in_patient_id", "")
    val allMedication by medicationPageViewModel.medicationsById(patientID).collectAsState(initial = emptyList())
    val allSymptom by symptomsViewModel.symptomsById(patientID).collectAsState(initial = emptyList())

    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    val patient by homePageViewModel.getPatientById(patientID).collectAsState(initial = Patient("","","",""))
    val searchInput = medCoachViewModel.searchInput
    val searchResult by drugViewModel.drugInfo.collectAsState(initial = emptyList())
    val matchedMedic = allMedication.filter {
        searchInput.isNotEmpty() && it.name.contains(
            searchInput,
            ignoreCase = true
        )
    }
    var isOnline by remember { mutableStateOf(false) }
    var hasSearched by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")

    // Format information into string for AI tips generation
    val medicationNamesForAI = allMedication.joinToString("\n") { med ->
        "Name: ${med.name}" +
                "Dosage: ${med.dosage}" +
                "Notes: ${med.notes}" +
                "Frequency: ${med.frequency}".trimIndent()
    }
    val symptomTypeForAI = allSymptom.joinToString("\n") { symptom ->
        "Category: ${symptom.category}" +
                "Severity: ${symptom.severity}" +
                "Notes: ${symptom.notes}" +
                "Date: ${symptom.dateTime}".trimIndent()
    }

    val prompt =
        "Generate a short encouraging message for a patient called ${patient?.name} who is taking $medicationNamesForAI and has symptoms: $symptomTypeForAI ." +
                "Keep the message supportive, short and simple."
    val uiState by aiTipViewModel.tipUiState.collectAsState()
    val placeholderResult = stringResource(R.string.results_placeholder)
    var result by rememberSaveable { mutableStateOf(placeholderResult) }

    var saveTip by remember { mutableStateOf(false) }
    val allTips by medCoachViewModel.allTips.collectAsState(initial = emptyList())
    val pastTips = allTips.filter { it.patientId==patientID }.sortedByDescending { it.dateTime }
    var showModal by remember {mutableStateOf(false)}
    LaunchedEffect(Unit) {
        while (true) {
            isOnline = drugViewModel.isNetworkAvailable()
            delay(5000)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "MedCoach",
                fontSize = 23.sp
            )

            Box(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                OutlinedTextField(
                    value = searchInput,
                    onValueChange = { medCoachViewModel.updateSearchInput(it)
                                    expanded = matchedMedic.isNotEmpty()},
                    modifier = Modifier
                        .padding(8.dp)
                        .onGloballyPositioned { coordinates ->
                            textFieldSize = coordinates.size
                        },
                    label = { Text("Insert Medication Name") },
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
                                textFieldSize.width.toDp()
                            }
                        )
                ) {
                    matchedMedic.forEach { label ->
                        DropdownMenuItem(
                            text = { Text(text = label.name) },
                            onClick = {
                                medCoachViewModel.updateSearchInput(label.name)
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                drugViewModel.searchedDrug(searchInput)
                hasSearched = true
                if (!isOnline) {
                    scope.launch { snackBarHostState.showSnackbar("Need Connection to Search") }
                }
            }) {
                Icon(Icons.Outlined.Search, contentDescription = "Find")
                Text("Find")
            }
        }
        if (!isOnline) {
            item {
                Text(
                    text = "Offline",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(20.dp)
                )
            }

        } else if (hasSearched && searchResult.isEmpty()) {
            item {
                Text(
                    text = "'$searchInput' Not Found.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(20.dp)
                )
            }

        } else if (searchResult.isNotEmpty()) {

            item {
                Text(
                    text = "Search Result:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            items(searchResult) { drug ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column {

                        Text(
                            text = drug.name ?: searchInput,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(8.dp)
                        )


                        Text(
                            "Purpose: ${
                                drug.purpose?.firstOrNull()
                                    ?.take(200) ?: "No purpose available"
                            }...",
                            modifier = Modifier.padding(8.dp)
                        )
                        Text(
                            "Warnings: ${
                                drug.warnings?.firstOrNull()
                                    ?.take(200) ?: "No warning available"
                            }...",
                            modifier = Modifier.padding(8.dp)
                        )
                        Text(
                            "Dosage: ${
                                drug.dosage_and_administration?.firstOrNull()
                                    ?.take(200) ?: "No dosage available"
                            }...",
                            modifier = Modifier.padding(8.dp)
                        )

                        // If some of the main information fields are empty, show other fields
                        if(drug.purpose.isNullOrEmpty() || drug.warnings.isNullOrEmpty() || drug.dosage_and_administration.isNullOrEmpty()){
                            if(!drug.indications_and_usage.isNullOrEmpty()){
                                Text(
                                    "Usage: ${
                                        drug.indications_and_usage?.firstOrNull()
                                            ?.take(200) ?: "Not available"
                                    }...",
                                    modifier = Modifier.padding(8.dp)
                                )
                            }

                            if(!drug.do_not_use.isNullOrEmpty()){
                                Text(
                                    "Do not use: ${
                                        drug.do_not_use?.firstOrNull()
                                            ?.take(200) ?: "Not available"
                                    }...",
                                    modifier = Modifier.padding(8.dp)
                                )
                            }

                            if(!drug.when_using.isNullOrEmpty())
                                Text(
                                    "When Using: ${
                                        drug.when_using?.firstOrNull()
                                            ?.take(200) ?: "Not available"
                                    }...",
                                    modifier = Modifier.padding(8.dp)
                                )

                            if(!drug.stop_use.isNullOrEmpty())
                                Text(
                                    "When Using: ${
                                        drug.stop_use?.firstOrNull()
                                            ?.take(200) ?: "Not available"
                                    }...",
                                    modifier = Modifier.padding(8.dp)
                                )
                        }

                    }
                }
            }
        }

        //GenAI Medication Tips
        item {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 20.dp)
            )

            Text(
                text = "Medication Tips",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                //Generate Tip Button
                Button(
                    onClick = {
                        aiTipViewModel.sendTipPrompt(prompt)
                        saveTip = false
                    },
                    enabled = prompt.isNotEmpty(),
                    modifier = Modifier

                ) {
                    Text(text = "Generate Tip")
                }

                Button(onClick = {
                    showModal = true
                }) {
                    Text("Tips History")
                }

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

                Text(
                    text = result,
                    textAlign = TextAlign.Start,
                    color = textColor,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                )

                LaunchedEffect(result) {
                    if(!saveTip && uiState is UiState.Success && result.isNotEmpty()){
                        val newTip = MedCoachTip(
                            patientId = patientID?:"",
                            message = result,
                            dateTime = LocalDateTime.now().format(formatter).toString()
                        )
                        medCoachViewModel.insertTip(newTip)
                        saveTip = true
                    }
                }


            }

            //Show Modal for tips history
            if(showModal) {
                AlertDialog(
                    onDismissRequest = { showModal = false},
                    title = { Text("Tips History")},
                    confirmButton = {
                        Button(onClick = {
                            showModal = false
                        }) {
                            Text("Done")
                        }
                    },
                    text = {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            if (pastTips.isEmpty()) {
                                item {
                                    Text(
                                        "No past tips, Generate a tip!",
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }

                            } else {
                                items(pastTips) { tip ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 4.dp
                                        ),
                                    ) {
                                        Column {
                                            Text(
                                                text = tip.dateTime,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                            Text(
                                                text = tip.message,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }


    }
}