package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.PartyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PartyDao {
    @Query("SELECT * FROM parties ORDER BY name ASC")
    fun getAllParties(): Flow<List<PartyEntity>>

    @Query("SELECT * FROM parties WHERE type = :type ORDER BY name ASC")
    fun getPartiesByType(type: String): Flow<List<PartyEntity>>

    @Query("SELECT * FROM parties WHERE id = :id")
    suspend fun getPartyById(id: Long): PartyEntity?

    @Query("SELECT * FROM parties WHERE id = :id")
    fun getPartyFlowById(id: Long): Flow<PartyEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParty(party: PartyEntity): Long

    @Update
    suspend fun updateParty(party: PartyEntity)

    @Delete
    suspend fun deleteParty(party: PartyEntity)

    @Query("UPDATE parties SET currentBalance = currentBalance + :delta WHERE id = :partyId")
    suspend fun updatePartyBalance(partyId: Long, delta: Double)

    @Query("SELECT SUM(currentBalance) FROM parties WHERE type = 'CUSTOMER' AND currentBalance > 0")
    fun getTotalReceivables(): Flow<Double?>

    @Query("SELECT SUM(currentBalance) FROM parties WHERE type = 'SUPPLIER' AND currentBalance > 0")
    fun getTotalPayables(): Flow<Double?>
}
