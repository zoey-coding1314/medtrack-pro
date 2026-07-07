package com.elizabeth.s36639095.medtrack.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.elizabeth.s36639095.medtrack.data.MedCoachTip
import kotlinx.coroutines.flow.Flow

@Dao
interface MedCoachTipDao {
    @Insert
    suspend fun insertTip(tip: MedCoachTip)

    @Insert
    suspend fun insertTips(tipList: List<MedCoachTip>)

    @Update
    suspend fun updateTip(tip: MedCoachTip)

    @Delete
    suspend fun deleteTip(tip: MedCoachTip)

    @Query("SELECT * FROM medcoachtip ORDER BY patientId ASC")
    fun getAllTips(): Flow<List<MedCoachTip>>
}