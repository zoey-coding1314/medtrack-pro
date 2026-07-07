package com.elizabeth.s36639095.medtrack.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "symptoms",
    foreignKeys = [ForeignKey(Patient::class,
        arrayOf("patientId"),
        arrayOf("patientId"),
        onUpdate = ForeignKey.CASCADE, //When a row in the parent table is deleted,
        onDelete = ForeignKey.CASCADE)],
    indices = [Index("patientId")]) //the child table also deletes associated rows
data class Symptom (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val patientId: String,
    val category: String,
    val severity: Int,
    val notes: String,
    val dateTime: String,
)