package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
data class ShoppingItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val category: String,
    val targetLocation: String = "Fridge",
    val quantity: Double = 1.0,
    val unit: String = "pcs",
    val estimatedPrice: Double = 2.99,
    val isChecked: Boolean = false,
    val notes: String = "",
    val addedFromRecipe: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
