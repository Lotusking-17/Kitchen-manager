package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PantryItemEntity::class,
        ShoppingItemEntity::class,
        MealPlanEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pantryDao(): PantryDao
    abstract fun shoppingDao(): ShoppingDao
    abstract fun mealPlanDao(): MealPlanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pantrypal_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
