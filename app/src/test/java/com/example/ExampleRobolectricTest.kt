package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ExpiryStatus
import com.example.data.model.FoodCategory
import com.example.data.model.SampleData
import com.example.data.model.StorageLocation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("PantryPal", appName)
  }

  @Test
  fun `recipe matching calculates correct match percentages`() {
    val recipes = SampleData.getRecipes()
    val spaghettiRecipe = recipes.first { it.title.contains("Spaghetti") }
    val defaultPantryNames = SampleData.getDefaultPantryItems().map { it.name }

    val match = spaghettiRecipe.calculateMatch(defaultPantryNames)
    assertEquals(100, match.matchPercentage)
    assertTrue("Should have 0 missing ingredients with default pantry", match.missingIngredients.isEmpty())
  }

  @Test
  fun `available currencies list contains major global currencies`() {
    val currencies = com.example.data.model.AVAILABLE_CURRENCIES
    assertTrue("Should contain EUR", currencies.any { it.code == "EUR" && it.symbol == "€" })
    assertTrue("Should contain GBP", currencies.any { it.code == "GBP" && it.symbol == "£" })
    assertTrue("Should contain JPY", currencies.any { it.code == "JPY" && it.symbol == "¥" })
    assertTrue("Should contain CAD", currencies.any { it.code == "CAD" && it.symbol == "C$" })
  }
}

