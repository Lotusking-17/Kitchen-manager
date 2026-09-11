package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM shopping_items ORDER BY isChecked ASC, category ASC, name ASC")
    fun getAllShoppingItems(): Flow<List<ShoppingItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ShoppingItemEntity>)

    @Update
    suspend fun updateItem(item: ShoppingItemEntity)

    @Delete
    suspend fun deleteItem(item: ShoppingItemEntity)

    @Query("DELETE FROM shopping_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE shopping_items SET isChecked = :isChecked WHERE id = :id")
    suspend fun setChecked(id: Long, isChecked: Boolean)

    @Query("DELETE FROM shopping_items WHERE isChecked = 1")
    suspend fun clearCheckedItems()

    @Query("SELECT * FROM shopping_items WHERE isChecked = 1")
    suspend fun getCheckedItems(): List<ShoppingItemEntity>
}
