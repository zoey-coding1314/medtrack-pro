package com.elizabeth.s36639095.medtrack.data.repository

import android.content.Context
import com.elizabeth.s36639095.medtrack.data.MedCoachTip
import com.elizabeth.s36639095.medtrack.data.dao.MedCoachTipDao
import com.elizabeth.s36639095.medtrack.data.MedTrackDatabase
import kotlinx.coroutines.flow.Flow

class MedCoachTipRepository {

    var medCoachTipDao: MedCoachTipDao

    constructor(context: Context) {
        medCoachTipDao = MedTrackDatabase.getDatabase(context).medCoachTipDao()
    }

    suspend fun insertTip(tip: MedCoachTip){
        medCoachTipDao.insertTip(tip)
    }
    suspend fun insertTips(tipList: List<MedCoachTip>){
        medCoachTipDao.insertTips(tipList)
    }
    suspend fun deleteTip(tip: MedCoachTip) {
        medCoachTipDao.deleteTip(tip)
    }
    suspend fun updateTip(tip: MedCoachTip) {
        medCoachTipDao.updateTip(tip)
    }
    fun getAllTips(): Flow<List<MedCoachTip>> = medCoachTipDao.getAllTips()
}