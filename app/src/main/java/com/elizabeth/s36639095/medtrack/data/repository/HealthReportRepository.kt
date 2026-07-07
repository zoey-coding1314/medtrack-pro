package com.elizabeth.s36639095.medtrack.data.repository

import android.content.Context
import com.elizabeth.s36639095.medtrack.data.HealthReport
import com.elizabeth.s36639095.medtrack.data.MedTrackDatabase
import com.elizabeth.s36639095.medtrack.data.dao.HealthReportDao
import kotlinx.coroutines.flow.Flow

class HealthReportRepository {
    var healthReportDao: HealthReportDao

    constructor(context: Context) {
        healthReportDao = MedTrackDatabase.getDatabase(context).healthReportDao()
    }

    suspend fun insertReport(report: HealthReport){
        healthReportDao.insertReport(report)
    }

    suspend fun deleteReport(report: HealthReport) {
        healthReportDao.deleteReport(report)
    }

    suspend fun updateReport(report: HealthReport) {
        healthReportDao.updateReport(report)
    }

    fun getAllReports(): Flow<List<HealthReport>> = healthReportDao.getAllReports()

}