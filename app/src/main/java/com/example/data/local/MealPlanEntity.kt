package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_plans")
data class MealPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val dayOfWeek: String, // Monday, Tuesday, ...
    val mealType: String, // Breakfast, Lunch, Dinner, Snack
    val recipeId: String? = null,
    val title: String,
    val calories: Int = 0,
    val servings: Int = 2
)
