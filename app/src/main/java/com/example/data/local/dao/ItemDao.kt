package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY name ASC")
    fun getAllItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemById(id: Long): ItemEntity?

    @Query("SELECT * FROM items WHERE stockQuantity <= minStockAlert ORDER BY stockQuantity ASC")
    fun getLowStockItems(): Flow<List<ItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity): Long

    @Update
    suspend fun updateItem(item: ItemEntity)

    @Delete
    suspend fun deleteItem(item: ItemEntity)

    @Query("UPDATE items SET stockQuantity = stockQuantity + :delta, updatedAt = :timestamp WHERE id = :itemId")
    suspend fun adjustStock(itemId: Long, delta: Double, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT SUM(stockQuantity * purchasePrice) FROM items")
    fun getTotalStockValue(): Flow<Double?>
}
