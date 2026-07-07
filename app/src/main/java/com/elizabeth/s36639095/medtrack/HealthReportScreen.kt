package com.elizabeth.s36639095.medtrack

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elizabeth.s36639095.medtrack.data.HealthReport
import com.elizabeth.s36639095.medtrack.data.Patient
import com.elizabeth.s36639095.medtrack.data.network.UiState
import com.elizabeth.s36639095.medtrack.viewmodel.HealthReportViewModel
import com.elizabeth.s36639095.medtrack.viewmodel.HomePageViewModel
import com.elizabeth.s36639095.medtrack.viewmodel.MedicationPageViewModel
import com.elizabeth.s36639095.medtrack.viewmodel.SymptomsViewModel
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserReport(homePageViewModel: HomePageViewModel,
               medicationPageViewModel: MedicationPageViewModel,
               symptomsViewModel: SymptomsViewModel,
               healthReportViewModel: HealthReportViewModel) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("MedTrack_sp", Context.MODE_PRIVATE)
    val patientID = sharedPref.getString("logged_in_patient_id", "")

    val medications by medicationPageViewModel.medicationsById(patientID).collectAsState(initial = emptyList())
    val symptoms by symptomsViewModel.symptomsById(patientID).collectAsState(initial = emptyList())
    val averageSeverity by symptomsViewModel.averageSeverityByPatientId(patientID)!!.collectAsState(initial = null)
    val commonSymptom by symptomsViewModel.commonSymptomById(patientID)!!.collectAsState(initial = null)

    val patient by homePageViewModel.getPatientById(patientID).collectAsState(initial = Patient("","","",""))
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")

    // Format data into string for AI prompt to generate better summaries
    val medicationNamesForAI = medications.joinToString(",") { it.name }
    val symptomTypeForAI = symptoms.joinToString(",") { it.category }
    val symptomsInfo = symptoms.joinToString("\n") { symptom ->

        "Category: ${symptom.category}" +
                "Severity: ${symptom.severity}" +
                "Notes: ${symptom.notes}" +
                "Date: ${symptom.dateTime}".trimIndent()
    }
    val prompt =
        "Generate a structured, short report summary notes based on the stats given,for a patient called ${patient?.name} who is taking $medicationNamesForAI " +
                "and has symptoms: $symptomTypeForAI ." +
                "The patient has average symptom severity (1-10) of $averageSeverity" +
                "The most common symptom is $commonSymptom" +
                "Symptoms info: $symptomsInfo" +
                "Keep the summary notes short and simple, no need to list out symptoms, make it readable as directly show the summary to the patient."
    val uiState by healthReportViewModel.reportUiState.collectAsState()
    val placeholderResult = stringResource(R.string.results_placeholder)
    var result by rememberSaveable { mutableStateOf(placeholderResult) }

    val allReports by healthReportViewModel.allReports.collectAsState(initial = emptyList())
    val pastReports = allReports.filter { it.patientId==patientID }.sortedByDescending { it.dateTime }
    var saveReport by remember { mutableStateOf(false) }
    var showModal by remember {mutableStateOf(false)}

    val symptomCategory = symptoms.groupBy { it.category }.mapValues{it.value.size}
    val pieColor = listOf(Color(0xFF7ED57F),Color(0xFFF3C281),Color(0xFFF3E9A6),Color(0xFFE7665E)
        ,Color(0xFF6BCCF3),Color(0xFF425BF5),Color(0xFF6947E1)
    )

    var pieData by remember(symptomCategory) {
        mutableStateOf(
            // Convert symptom categories into pie chart data
            symptomCategory.entries.map { (category, number) ->
                Pie(
                    label = category,
                    data = number.toDouble(),
                    color = pieColor.random(),
                    selectedColor = Color(0xFF2196F3)
                )
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Health Report",
                fontSize = 23.sp,
                fontWeight = FontWeight.ExtraBold
            )

            //Card showing the user's symptoms statistics
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Statistics",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(8.dp),
                )

                Text(
                    "Total Number of Symptom: ${symptoms.size}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Average Symptom Severity: ${averageSeverity?:"No Data Available"}",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                    averageSeverity?.let {
                        Text(
                            text = when {
                                it <= 3 -> "Mild"
                                it <= 6 -> "Moderate"
                                else -> "Severe"
                            },
                            color = when (it.toInt()) {
                                in 0..3 -> {
                                    Color(0xFF2AAB2C)
                                }

                                in 4..6 -> {
                                    Color(0xFFFFB909)
                                }

                                else -> Color.Red
                            },
                            fontSize = 16.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                Slider(
                    value = averageSeverity?:0f,
                    onValueChange = {},
                    enabled = false,
                    valueRange = 0f..10f,
                    steps = 9
                )

                Text(
                    "Your Most Common Symptom: ${commonSymptom?:"No Data Available"}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(8.dp)
                )

            }
            Spacer(modifier = Modifier.height(20.dp))

        }

        // Show pie chart if there is symptom data
        item {

            if (symptoms.isNotEmpty()) {

                PieChart(
                    modifier = Modifier.size(270.dp),
                    data = pieData,
                    onPieClick = {
                        println("${it.label} Clicked")
                        val pieIndex = pieData.indexOf(it)
                        pieData =
                            pieData.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
                    },
                    selectedScale = 1.2f,
                    scaleAnimEnterSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    colorAnimEnterSpec = tween(300),
                    colorAnimExitSpec = tween(300),
                    scaleAnimExitSpec = tween(300),
                    spaceDegreeAnimExitSpec = tween(300),
                    style = Pie.Style.Fill
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    pieData.forEach { pie ->

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Spacer(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(pie.color)
                            )

                            Spacer(modifier = Modifier.size(8.dp))

                            Text(
                                text = "${pie.label} (${pie.data.toInt()})"
                            )
                        }
                    }
                }

            } else {
                Text(
                    "No symptom data available yet.\nStart tracking symptoms to generate reports.",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        //GenAI Summary
        item {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 20.dp)
            )

            Text(
                text = "Generated Summary",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                //Generate Summary Button
                Button(
                    onClick = {
                        healthReportViewModel.sendReportPrompt(prompt)
                        saveReport = false
                    },
                    enabled = prompt.isNotEmpty(),
                    modifier = Modifier,
                ) {
                    Text(text = "Generate Summary")
                }

                Button(onClick = {
                    showModal = true
                }) {
                    Text("Summaries History")
                }

            }


            if (uiState is UiState.Loading) {
                CircularProgressIndicator(modifier = Modifier)
            } else {
                var textColor = MaterialTheme.colorScheme.onSurface

                // When generation is failed, an error message will appear
                if (uiState is UiState.Error) {
                    textColor = MaterialTheme.colorScheme.error
                    result =  "AI service temporarily unavailable. Please try again later."
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

                if(uiState is UiState.Success){
                    val newReport = HealthReport(
                        patientId = patientID ?: "",
                        summary = result,
                        dateTime = LocalDateTime.now().format(formatter).toString(),
                        averageSeverity = averageSeverity,
                        mostCommonSymptom = commonSymptom
                    )

                    //Share button: Appear when the new summary is generated successfully
                    Button(onClick = {
                        val message = "Date Time: ${newReport.dateTime}\n" +
                                "-------- My Report --------\n" +
                                "${newReport.summary}\n"
                        val intent = Intent(Intent.ACTION_SEND).apply{
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, message)
                        }

                        context.startActivity(
                            Intent.createChooser(
                                intent,
                                "Share summary"
                            )
                        )
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share")
                        Text("Share this Summary")
                    }


                    LaunchedEffect(result) {
                        if (result.isNotEmpty() && !saveReport){
                            healthReportViewModel.insertReport(newReport)
                            saveReport = true
                        }
                    }
                }

            }

            //Show Modal for summary history
            if(showModal) {
                AlertDialog(
                    onDismissRequest = { showModal = false},
                    title = { Text("Report Summaries History")},
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
                            if (pastReports.isEmpty()) {
                                item {
                                    Text(
                                        "No past summary, Generate a Report!",
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }

                            } else {
                                items(pastReports) { report ->
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
                                                text = report.dateTime,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                            Text(
                                                text = report.summary,
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