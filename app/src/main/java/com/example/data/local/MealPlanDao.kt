package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPlanDao {
    @Query("SELECT * FROM meal_plans ORDER BY id ASC")
    fun getAllMealPlans(): Flow<List<MealPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlan(mealPlan: MealPlanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MealPlanEntity>)

    @Update
    suspend fun updateMealPlan(mealPlan: MealPlanEntity)

    @Delete
    suspend fun deleteMealPlan(mealPlan: MealPlanEntity)

    @Query("DELETE FROM meal_plans WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM meal_plans WHERE dayOfWeek = :dayOfWeek AND mealType = :mealType")
    suspend fun deleteBySlot(dayOfWeek: String, mealType: String)
}
