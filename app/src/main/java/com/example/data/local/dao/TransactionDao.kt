package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC, id DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE partyId = :partyId ORDER BY date DESC, id DESC")
    fun getTransactionsByParty(partyId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionFlowById(id: Long): Flow<TransactionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT COUNT(*) FROM transactions WHERE type = :type")
    suspend fun countTransactionsByType(type: String): Int

    @Query("SELECT SUM(grandTotal) FROM transactions WHERE type = 'SALE'")
    fun getTotalSales(): Flow<Double?>

    @Query("SELECT SUM(grandTotal) FROM transactions WHERE type = 'PURCHASE'")
    fun getTotalPurchases(): Flow<Double?>

    @Query("SELECT SUM(grandTotal) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpenses(): Flow<Double?>

    @Query("SELECT SUM(paidAmount) FROM transactions WHERE type = 'SALE' OR type = 'PAYMENT_IN'")
    fun getTotalMoneyIn(): Flow<Double?>

    @Query("SELECT SUM(paidAmount) FROM transactions WHERE type = 'PURCHASE' OR type = 'EXPENSE' OR type = 'PAYMENT_OUT'")
    fun getTotalMoneyOut(): Flow<Double?>

    @Query("SELECT * FROM transactions WHERE date >= :startTimestamp AND date <= :endTimestamp ORDER BY date DESC")
    fun getTransactionsBetween(startTimestamp: Long, endTimestamp: Long): Flow<List<TransactionEntity>>
}
