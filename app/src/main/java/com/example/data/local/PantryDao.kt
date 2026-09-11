package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PantryDao {
    @Query("SELECT * FROM pantry_items WHERE isConsumed = 0 AND isWasted = 0 ORDER BY expiryTimestamp ASC")
    fun getActivePantryItems(): Flow<List<PantryItemEntity>>

    @Query("SELECT * FROM pantry_items WHERE isConsumed = 1 OR isWasted = 1 ORDER BY createdAt DESC")
    fun getHistoryItems(): Flow<List<PantryItemEntity>>

    @Query("SELECT * FROM pantry_items WHERE isConsumed = 0 AND isWasted = 0 AND location = :location ORDER BY expiryTimestamp ASC")
    fun getActiveItemsByLocation(location: String): Flow<List<PantryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: PantryItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PantryItemEntity>)

    @Update
    suspend fun updateItem(item: PantryItemEntity)

    @Delete
    suspend fun deleteItem(item: PantryItemEntity)

    @Query("DELETE FROM pantry_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("UPDATE pantry_items SET isConsumed = 1 WHERE id = :id")
    suspend fun markConsumed(id: Long)

    @Query("UPDATE pantry_items SET isWasted = 1 WHERE id = :id")
    suspend fun markWasted(id: Long)

    @Query("UPDATE pantry_items SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: Long, quantity: Double)
}
