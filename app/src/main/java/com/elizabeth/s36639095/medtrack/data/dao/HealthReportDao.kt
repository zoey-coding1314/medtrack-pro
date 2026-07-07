package com.elizabeth.s36639095.medtrack.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.elizabeth.s36639095.medtrack.data.HealthReport
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthReportDao {
    @Insert
    suspend fun insertReport(report: HealthReport)

    @Update
    suspend fun updateReport(report: HealthReport)

    @Delete
    suspend fun deleteReport(report: HealthReport)

    @Query("SELECT * FROM healthReport ORDER BY patientId ASC")
    fun getAllReports(): Flow<List<HealthReport>>

}