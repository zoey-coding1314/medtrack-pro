package com.elizabeth.s36639095.medtrack.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.arrayOf

@Entity(tableName = "medcoachtip",
    foreignKeys = [ForeignKey(Patient::class,
        arrayOf("patientId"),
        arrayOf("patientId"),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE)],
    indices = [Index("patientId")])
data class MedCoachTip (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val patientId: String,
    val message: String,
    val dateTime: String,
)