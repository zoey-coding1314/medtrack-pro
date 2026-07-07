package com.elizabeth.s36639095.medtrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.elizabeth.s36639095.medtrack.data.dao.HealthReportDao
import com.elizabeth.s36639095.medtrack.data.dao.MedCoachTipDao
import com.elizabeth.s36639095.medtrack.data.dao.MedicationDao
import com.elizabeth.s36639095.medtrack.data.dao.PatientDao
import com.elizabeth.s36639095.medtrack.data.dao.SymptomDao

@Database(entities = [Patient::class, Medication::class, Symptom::class, MedCoachTip::class, HealthReport::class], version = 9, exportSchema = false)
abstract class MedTrackDatabase: RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun medicationDao(): MedicationDao
    abstract fun symptomDao(): SymptomDao
    abstract fun medCoachTipDao(): MedCoachTipDao
    abstract fun healthReportDao(): HealthReportDao

    companion object {
        @Volatile
        private var Instance: MedTrackDatabase? = null

        fun getDatabase(context: Context): MedTrackDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, MedTrackDatabase::class.java, "medtrack_database")
                    .build().also {Instance = it}
            }
        }
    }
}