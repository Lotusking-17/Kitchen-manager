package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ExpiryStatus

@Entity(tableName = "pantry_items")
data class PantryItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val category: String, // from FoodCategory.title
    val location: String, // Fridge, Pantry, Freezer, Spices
    val quantity: Double,
    val unit: String, // pcs, packs, g, kg, ml, L
    val expiryTimestamp: Long, // timestamp in ms
    val openedTimestamp: Long = 0L,
    val isConsumed: Boolean = false,
    val isWasted: Boolean = false,
    val estimatedPrice: Double = 3.50,
    val notes: String = "",
    val barcode: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val expiryStatusInfo: Pair<ExpiryStatus, Int>
        get() = ExpiryStatus.calculate(expiryTimestamp)
}
