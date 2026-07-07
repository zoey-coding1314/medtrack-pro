package com.elizabeth.s36639095.medtrack.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "healthReport",
    foreignKeys = [ForeignKey(Patient::class,
        arrayOf("patientId"),
        arrayOf("patientId"),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE)],
    indices = [Index("patientId")])
data class HealthReport (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val patientId: String,
    val summary: String,
    val dateTime: String,
    val averageSeverity: Float?,
    val mostCommonSymptom: String?
)