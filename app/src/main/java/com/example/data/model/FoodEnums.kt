package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class StorageLocation(val title: String, val emoji: String) {
    FRIDGE("Fridge", "❄️"),
    PANTRY("Pantry", "🥫"),
    FREEZER("Freezer", "🧊"),
    SPICES("Spices & Seasoning", "🌿");

    companion object {
        fun fromString(value: String): StorageLocation {
            return entries.find { it.title.equals(value, ignoreCase = true) || it.name.equals(value, ignoreCase = true) } ?: PANTRY
        }
    }
}

enum class FoodCategory(val title: String, val emoji: String) {
    PRODUCE("Produce & Veggies", "🥬"),
    DAIRY("Dairy & Eggs", "🧀"),
    MEAT("Meat & Seafood", "🥩"),
    BAKERY("Bakery & Bread", "🍞"),
    PANTRY_STAPLES("Pantry & Grains", "🌾"),
    CANNED("Canned & Jars", "🥫"),
    FROZEN("Frozen Foods", "🧊"),
    SNACKS("Snacks & Treats", "🥨"),
    BEVERAGES("Beverages", "🧃"),
    SPICES("Spices & Condiments", "🧂"),
    HOUSEHOLD("Household & Other", "🧼");

    companion object {
        fun fromString(value: String): FoodCategory {
            return entries.find { it.title.equals(value, ignoreCase = true) || it.name.equals(value, ignoreCase = true) } ?: PANTRY_STAPLES
        }
    }
}

enum class ExpiryStatus {
    EXPIRED,
    EXPIRING_SOON, // 1 - 3 days
    FRESH;

    companion object {
        fun calculate(expiryTimestamp: Long): Pair<ExpiryStatus, Int> {
            val now = System.currentTimeMillis()
            val diffMs = expiryTimestamp - now
            val daysLeft = Math.ceil(diffMs / (1000.0 * 60 * 60 * 24)).toInt()

            val status = when {
                daysLeft < 0 -> EXPIRED
                daysLeft <= 3 -> EXPIRING_SOON
                else -> FRESH
            }
            return Pair(status, daysLeft)
        }
    }
}
