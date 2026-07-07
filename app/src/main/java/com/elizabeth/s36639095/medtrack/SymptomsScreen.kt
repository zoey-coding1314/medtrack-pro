package com.elizabeth.s36639095.medtrack

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elizabeth.s36639095.medtrack.data.Symptom
import com.elizabeth.s36639095.medtrack.viewmodel.SymptomsViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@SuppressLint("FlowOperatorInvokedInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomsScreen(symptomsViewModel: SymptomsViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    var expanded by remember { mutableStateOf(false) }
    val category =
        listOf("Pain", "Nausea", "Dizziness", "Fatigue", "Headache", "Skin reaction", "Other")
    val selectedCategory = symptomsViewModel.newCategory
    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    val severityRating = symptomsViewModel.newSeverity
    val notes = symptomsViewModel.newNotes
    var lengthCheck by remember { mutableStateOf(false) }

    val selectedDate = symptomsViewModel.newDate
    val selectedTime: TimePickerState? = symptomsViewModel.newTime
    val selectedTimeString = symptomsViewModel.newTimeString
    var showMenu by remember { mutableStateOf(true) }
    var showDialExample by remember { mutableStateOf(false) }
    val formatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    val sharedPref = context.getSharedPreferences("MedTrack_sp", Context.MODE_PRIVATE)
    val patientID = sharedPref.getString("logged_in_patient_id", "")
    val allSymptom by symptomsViewModel.symptomsById(patientID).collectAsState(initial = emptyList())
    val symptoms =
        allSymptom.sortedByDescending { it.dateTime }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Log Symptom",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    //Symptom Category Dropdown menu
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = { symptomsViewModel.updateCategory(it) },
                            readOnly = true,
                            modifier = Modifier
                                .padding(8.dp)
                                .onGloballyPositioned { coordinates ->
                                    textFieldSize = coordinates.size
                                },
                            label = { Text("Select Symptom Category") },
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
                            category.forEach { label ->
                                DropdownMenuItem(
                                    text = { Text(text = label) },
                                    onClick = {
                                        symptomsViewModel.updateCategory(label)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Text(text = "Severity Rating: ${severityRating.toInt()}")
                    Slider(
                        value = severityRating,
                        onValueChange = { symptomsViewModel.updateSeverity(it)},
                        valueRange = 0f..10f,
                        colors = SliderDefaults.colors(
                            //Color of the slider track changes between green, amber, and red
                            activeTrackColor = when (severityRating.toInt()) {
                                in 1..3 -> {
                                    Color(0xff4caf50)
                                }

                                in 4..6 -> {
                                    Color(0xffffc107)
                                }

                                else -> Color.Red
                            }
                        ),
                        steps = 9
                    )

                    Spacer(Modifier.height(15.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = {
                            symptomsViewModel.updateNotes(it)
                            lengthCheck = it.length > 200
                        },
                        label = { Text(text = "Notes (Max 200 Characters)") },
                        isError = lengthCheck,
                        modifier = Modifier.padding(8.dp),
                        )

                    //Show error message when the notes length is more than 200 characters
                    if (lengthCheck) {
                        Text(
                            text = "Cannot more than 200 Characters",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    } else {
                        //Show the character count instantly
                        Text(
                            text = "${notes.length}/200",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 12.dp, top = 2.dp)
                        )
                    }

                    //DatePicker
                    Spacer(modifier = Modifier.height(15.dp))
                    val date = datePickerDocked()
                    symptomsViewModel.updateDate(date)
                    if (selectedDate != "") {
                        Text("Selected Date: $selectedDate")
                    } else {
                        Text("No date selected.")
                    }

                    //TimePicker
                    Box(
                        Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (showMenu) {
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
                                    symptomsViewModel.updateTime(time)
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
                        symptomsViewModel.updateTimeString(formatter.format(cal.time))
                        Text("Selected time: $selectedTimeString")
                    } else {
                        Text("No time selected.")
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    //Save Button
                    Button(onClick = {
                        if (selectedCategory.isEmpty() || selectedDate == "" || selectedTime == null)
                            Toast.makeText(
                                context,
                                "Error. The required fields should not be blank.",
                                Toast.LENGTH_LONG
                            ).show()
                        else if (lengthCheck)
                            Toast.makeText(
                                context,
                                "Error. The notes cannot have more than 200 characters.",
                                Toast.LENGTH_LONG
                            ).show()
                        else {
                            val newSymptom = Symptom(
                                patientId = patientID ?: "",
                                category = selectedCategory,
                                severity = severityRating.toInt(),
                                notes = notes,
                                dateTime = "$selectedDate $selectedTimeString"
                            )

                            symptomsViewModel.insertSymptom(newSymptom)
                            scope.launch { snackBarHostState.showSnackbar("Symptom Saved!") }

                            symptomsViewModel.updateCategory("")
                            symptomsViewModel.updateSeverity(5f)
                            symptomsViewModel.updateNotes("")
                            symptomsViewModel.updateDate("")
                            symptomsViewModel.updateTime(null)
                        }


                    }) {
                        Text("Save")
                    }

                }

            }

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

                    if (symptoms.isEmpty()) {
                        Text(
                            text = "No symptom logged yet.",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                    } else {
                        Text(
                            text = "Symptoms History",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }

            }

            //Symptom list
            items(symptoms) { symptom ->
                val severity = symptom.severity
                Card(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                ) {
                    Column {
                        Text(
                            "Symptom Category: ${symptom.category}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(8.dp)
                        )

                        Text(
                            "Date/Time: ${symptom.dateTime}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(8.dp)
                        )

                        Text(
                            "Severity: $severity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = when (severity) {
                                in 1..3 -> {
                                    Color(0xff4caf50)
                                }

                                in 4..6 -> {
                                    Color(0xffffc107)
                                }

                                else -> {
                                    Color.Red
                                }
                            },
                            modifier = Modifier.padding(8.dp)
                        )
                        Text(
                            "Notes: ${symptom.notes}",
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