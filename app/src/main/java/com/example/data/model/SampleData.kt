package com.example.data.model

import com.example.data.local.MealPlanEntity
import com.example.data.local.PantryItemEntity
import com.example.data.local.ShoppingItemEntity

object SampleData {
    private val DAY_MS = 24 * 60 * 60 * 1000L

    fun getDefaultPantryItems(): List<PantryItemEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            // Expiring soon (Urgent! 1-2 days)
            PantryItemEntity(
                name = "Whole Milk",
                category = FoodCategory.DAIRY.title,
                location = StorageLocation.FRIDGE.title,
                quantity = 1.0,
                unit = "gallon",
                expiryTimestamp = now + (1 * DAY_MS) + (8 * 3600 * 1000),
                estimatedPrice = 4.29,
                notes = "Opened 3 days ago, keep cold",
                barcode = "011110417009"
            ),
            PantryItemEntity(
                name = "Fresh Baby Spinach",
                category = FoodCategory.PRODUCE.title,
                location = StorageLocation.FRIDGE.title,
                quantity = 250.0,
                unit = "g",
                expiryTimestamp = now + (2 * DAY_MS),
                estimatedPrice = 2.99,
                notes = "Great for salads or pasta",
                barcode = "033383120150"
            ),
            PantryItemEntity(
                name = "Chicken Breast Fillets",
                category = FoodCategory.MEAT.title,
                location = StorageLocation.FRIDGE.title,
                quantity = 600.0,
                unit = "g",
                expiryTimestamp = now + (3 * DAY_MS),
                estimatedPrice = 8.50,
                notes = "Organic boneless & skinless",
                barcode = "024500918234"
            ),
            // Fresh fridge staples
            PantryItemEntity(
                name = "Large Brown Eggs",
                category = FoodCategory.DAIRY.title,
                location = StorageLocation.FRIDGE.title,
                quantity = 10.0,
                unit = "pcs",
                expiryTimestamp = now + (12 * DAY_MS),
                estimatedPrice = 3.99,
                notes = "Farm fresh grade A",
                barcode = "011110852008"
            ),
            PantryItemEntity(
                name = "Greek Yogurt (Plain)",
                category = FoodCategory.DAIRY.title,
                location = StorageLocation.FRIDGE.title,
                quantity = 500.0,
                unit = "g",
                expiryTimestamp = now + (9 * DAY_MS),
                estimatedPrice = 4.49,
                notes = "High protein breakfast",
                barcode = "894700010045"
            ),
            PantryItemEntity(
                name = "Sharp Cheddar Cheese",
                category = FoodCategory.DAIRY.title,
                location = StorageLocation.FRIDGE.title,
                quantity = 200.0,
                unit = "g",
                expiryTimestamp = now + (18 * DAY_MS),
                estimatedPrice = 3.79,
                notes = "Block cheese",
                barcode = "041220194851"
            ),
            PantryItemEntity(
                name = "Vine Tomatoes",
                category = FoodCategory.PRODUCE.title,
                location = StorageLocation.FRIDGE.title,
                quantity = 4.0,
                unit = "pcs",
                expiryTimestamp = now + (5 * DAY_MS),
                estimatedPrice = 3.19,
                notes = "Ripe red tomatoes",
                barcode = "033383610022"
            ),
            // Pantry staples
            PantryItemEntity(
                name = "Spaghetti Pasta",
                category = FoodCategory.PANTRY_STAPLES.title,
                location = StorageLocation.PANTRY.title,
                quantity = 500.0,
                unit = "g",
                expiryTimestamp = now + (180 * DAY_MS),
                estimatedPrice = 1.89,
                notes = "Durum wheat semolina",
                barcode = "076808000030"
            ),
            PantryItemEntity(
                name = "Extra Virgin Olive Oil",
                category = FoodCategory.PANTRY_STAPLES.title,
                location = StorageLocation.PANTRY.title,
                quantity = 750.0,
                unit = "ml",
                expiryTimestamp = now + (240 * DAY_MS),
                estimatedPrice = 11.99,
                notes = "Cold pressed Italian",
                barcode = "071725718210"
            ),
            PantryItemEntity(
                name = "Garlic Bulbs",
                category = FoodCategory.PRODUCE.title,
                location = StorageLocation.PANTRY.title,
                quantity = 3.0,
                unit = "pcs",
                expiryTimestamp = now + (25 * DAY_MS),
                estimatedPrice = 1.50,
                notes = "Store in dry ventilated spot",
                barcode = "033383401012"
            ),
            PantryItemEntity(
                name = "Canned Crushed Tomatoes",
                category = FoodCategory.CANNED.title,
                location = StorageLocation.PANTRY.title,
                quantity = 2.0,
                unit = "cans",
                expiryTimestamp = now + (365 * DAY_MS),
                estimatedPrice = 2.49,
                notes = "San Marzano style",
                barcode = "041220678120"
            ),
            PantryItemEntity(
                name = "Rolled Oats",
                category = FoodCategory.PANTRY_STAPLES.title,
                location = StorageLocation.PANTRY.title,
                quantity = 1.0,
                unit = "kg",
                expiryTimestamp = now + (120 * DAY_MS),
                estimatedPrice = 4.29,
                notes = "Whole grain",
                barcode = "030000010305"
            ),
            // Freezer
            PantryItemEntity(
                name = "Frozen Mixed Berries",
                category = FoodCategory.FROZEN.title,
                location = StorageLocation.FREEZER.title,
                quantity = 400.0,
                unit = "g",
                expiryTimestamp = now + (90 * DAY_MS),
                estimatedPrice = 3.99,
                notes = "Strawberries, blueberries, raspberries",
                barcode = "011110822117"
            ),
            PantryItemEntity(
                name = "Salmon Fillets (Frozen)",
                category = FoodCategory.MEAT.title,
                location = StorageLocation.FREEZER.title,
                quantity = 2.0,
                unit = "pcs",
                expiryTimestamp = now + (75 * DAY_MS),
                estimatedPrice = 9.99,
                notes = "Wild caught Alaskan",
                barcode = "024500119283"
            ),
            // Spices
            PantryItemEntity(
                name = "Black Peppercorns",
                category = FoodCategory.SPICES.title,
                location = StorageLocation.SPICES.title,
                quantity = 1.0,
                unit = "grinder",
                expiryTimestamp = now + (300 * DAY_MS),
                estimatedPrice = 3.49,
                notes = "Freshly ground pepper",
                barcode = "052100010190"
            ),
            PantryItemEntity(
                name = "Sea Salt Flakes",
                category = FoodCategory.SPICES.title,
                location = StorageLocation.SPICES.title,
                quantity = 250.0,
                unit = "g",
                expiryTimestamp = now + (500 * DAY_MS),
                estimatedPrice = 2.89,
                notes = "Maldon style flakes",
                barcode = "070624000010"
            )
        )
    }

    fun getDefaultShoppingList(): List<ShoppingItemEntity> {
        return listOf(
            ShoppingItemEntity(
                name = "Fresh Avocados",
                category = FoodCategory.PRODUCE.title,
                targetLocation = StorageLocation.FRIDGE.title,
                quantity = 3.0,
                unit = "pcs",
                estimatedPrice = 3.99,
                isChecked = false,
                notes = "Check ripeness"
            ),
            ShoppingItemEntity(
                name = "Sourdough Bread",
                category = FoodCategory.BAKERY.title,
                targetLocation = StorageLocation.PANTRY.title,
                quantity = 1.0,
                unit = "loaf",
                estimatedPrice = 4.50,
                isChecked = false,
                notes = "Artisan bakery loaf"
            ),
            ShoppingItemEntity(
                name = "Almond Butter",
                category = FoodCategory.PANTRY_STAPLES.title,
                targetLocation = StorageLocation.PANTRY.title,
                quantity = 1.0,
                unit = "jar",
                estimatedPrice = 6.99,
                isChecked = true,
                notes = "Creamy no sugar added"
            ),
            ShoppingItemEntity(
                name = "Dijon Mustard",
                category = FoodCategory.SPICES.title,
                targetLocation = StorageLocation.FRIDGE.title,
                quantity = 1.0,
                unit = "bottle",
                estimatedPrice = 2.89,
                isChecked = false,
                notes = "For dressings & marinades"
            )
        )
    }

    fun getRecipes(): List<Recipe> {
        return listOf(
            Recipe(
                id = "recipe_garlic_pasta",
                title = "Creamy Garlic Parmesan Spaghetti",
                description = "Silky, indulgent pasta with toasted garlic, butter, and freshly grated cheese that comes together in 15 minutes.",
                prepTimeMinutes = 5,
                cookTimeMinutes = 10,
                servings = 3,
                calories = 480,
                category = "Dinner",
                dietaryTags = listOf("Vegetarian", "Quick & Easy"),
                ingredients = listOf(
                    RecipeIngredient("Spaghetti Pasta", "300 g", FoodCategory.PANTRY_STAPLES.title),
                    RecipeIngredient("Garlic Bulbs", "4 cloves", FoodCategory.PRODUCE.title),
                    RecipeIngredient("Extra Virgin Olive Oil", "2 tbsp", FoodCategory.PANTRY_STAPLES.title),
                    RecipeIngredient("Whole Milk", "1/2 cup", FoodCategory.DAIRY.title),
                    RecipeIngredient("Sharp Cheddar Cheese", "50 g", FoodCategory.DAIRY.title),
                    RecipeIngredient("Black Peppercorns", "pinch", FoodCategory.SPICES.title),
                    RecipeIngredient("Sea Salt Flakes", "to taste", FoodCategory.SPICES.title)
                ),
                instructions = listOf(
                    "Boil spaghetti in salted water until al dente (about 8-9 minutes). Reserve 1/2 cup of pasta cooking water.",
                    "While pasta cooks, gently warm olive oil in a large skillet and saute thinly sliced garlic over medium-low heat until fragrant and pale golden.",
                    "Whisk in milk and grated cheese until a creamy, glossy emulsion forms.",
                    "Toss the drained spaghetti directly into the sauce, adding splashes of pasta water as needed.",
                    "Finish with freshly cracked black pepper and extra cheese. Serve hot!"
                )
            ),
            Recipe(
                id = "recipe_spinach_scramble",
                title = "Fluffy Spinach & Cheddar Scramble",
                description = "Protein-packed breakfast scramble using fresh baby spinach and melting sharp cheddar cheese.",
                prepTimeMinutes = 5,
                cookTimeMinutes = 7,
                servings = 2,
                calories = 320,
                category = "Breakfast",
                dietaryTags = listOf("Vegetarian", "High-Protein", "Quick & Easy", "Gluten-Free"),
                ingredients = listOf(
                    RecipeIngredient("Large Brown Eggs", "4 pcs", FoodCategory.DAIRY.title),
                    RecipeIngredient("Fresh Baby Spinach", "100 g", FoodCategory.PRODUCE.title),
                    RecipeIngredient("Sharp Cheddar Cheese", "40 g", FoodCategory.DAIRY.title),
                    RecipeIngredient("Whole Milk", "2 tbsp", FoodCategory.DAIRY.title),
                    RecipeIngredient("Extra Virgin Olive Oil", "1 tbsp", FoodCategory.PANTRY_STAPLES.title),
                    RecipeIngredient("Black Peppercorns", "pinch", FoodCategory.SPICES.title)
                ),
                instructions = listOf(
                    "Beat eggs with milk, a pinch of sea salt, and black pepper until well combined.",
                    "Heat olive oil in a non-stick skillet over medium heat and wilt baby spinach for 1-2 minutes.",
                    "Pour in the beaten eggs and gently push from edges toward center with a silicone spatula to form soft curds.",
                    "Fold in shredded sharp cheddar cheese just before eggs finish cooking so it melts smoothly.",
                    "Garnish with sliced tomatoes or toast and enjoy immediately!"
                )
            ),
            Recipe(
                id = "recipe_crispy_chicken",
                title = "Garlic Herb Seared Chicken Breast",
                description = "Juicy golden chicken fillets basted with olive oil, garlic, and cracked pepper.",
                prepTimeMinutes = 8,
                cookTimeMinutes = 14,
                servings = 2,
                calories = 420,
                category = "Dinner",
                dietaryTags = listOf("High-Protein", "Gluten-Free", "Low-Carb"),
                ingredients = listOf(
                    RecipeIngredient("Chicken Breast Fillets", "400 g", FoodCategory.MEAT.title),
                    RecipeIngredient("Garlic Bulbs", "3 cloves", FoodCategory.PRODUCE.title),
                    RecipeIngredient("Extra Virgin Olive Oil", "2 tbsp", FoodCategory.PANTRY_STAPLES.title),
                    RecipeIngredient("Sea Salt Flakes", "1 tsp", FoodCategory.SPICES.title),
                    RecipeIngredient("Black Peppercorns", "1 tsp", FoodCategory.SPICES.title),
                    RecipeIngredient("Lemon Juice", "1 tbsp", FoodCategory.PRODUCE.title)
                ),
                instructions = listOf(
                    "Pat chicken breasts dry with paper towels and season generously on both sides with salt and pepper.",
                    "Heat olive oil in a heavy skillet over medium-high heat until shimmering.",
                    "Sear chicken for 6 minutes without moving until a golden crust develops.",
                    "Flip over, add smashed garlic cloves to the pan, and baste chicken with the infused oil for another 5-6 minutes until cooked through (165°F / 74°C).",
                    "Rest for 5 minutes before slicing so juices redistribute."
                )
            ),
            Recipe(
                id = "recipe_tomato_pasta",
                title = "Rustic Tomato Basil Pomodoro",
                description = "Classic comforting Italian pasta made with slow-simmered crushed tomatoes, garlic, and olive oil.",
                prepTimeMinutes = 5,
                cookTimeMinutes = 15,
                servings = 3,
                calories = 390,
                category = "Dinner",
                dietaryTags = listOf("Vegetarian", "Vegan", "Quick & Easy"),
                ingredients = listOf(
                    RecipeIngredient("Spaghetti Pasta", "300 g", FoodCategory.PANTRY_STAPLES.title),
                    RecipeIngredient("Canned Crushed Tomatoes", "1 can", FoodCategory.CANNED.title),
                    RecipeIngredient("Garlic Bulbs", "3 cloves", FoodCategory.PRODUCE.title),
                    RecipeIngredient("Extra Virgin Olive Oil", "3 tbsp", FoodCategory.PANTRY_STAPLES.title),
                    RecipeIngredient("Sea Salt Flakes", "1 tsp", FoodCategory.SPICES.title),
                    RecipeIngredient("Fresh Basil Leaves", "handful", FoodCategory.PRODUCE.title)
                ),
                instructions = listOf(
                    "Heat olive oil in a saucepan and gently sizzle sliced garlic until aromatic.",
                    "Add crushed tomatoes, salt, and pepper. Simmer on low heat for 12 minutes until thickened.",
                    "Cook pasta in boiling water until al dente.",
                    "Combine pasta with warm tomato pomodoro sauce and fresh basil. Serve with a drizzle of virgin olive oil."
                )
            ),
            Recipe(
                id = "recipe_berry_yogurt_bowl",
                title = "High-Protein Berry Greek Yogurt Bowl",
                description = "Nutritious creamy breakfast bowl loaded with antioxidants, fiber, and protein.",
                prepTimeMinutes = 4,
                cookTimeMinutes = 0,
                servings = 1,
                calories = 260,
                category = "Breakfast",
                dietaryTags = listOf("Vegetarian", "High-Protein", "Quick & Easy", "Gluten-Free"),
                ingredients = listOf(
                    RecipeIngredient("Greek Yogurt (Plain)", "200 g", FoodCategory.DAIRY.title),
                    RecipeIngredient("Frozen Mixed Berries", "100 g", FoodCategory.FROZEN.title),
                    RecipeIngredient("Rolled Oats", "30 g", FoodCategory.PANTRY_STAPLES.title),
                    RecipeIngredient("Honey or Maple Syrup", "1 tbsp", FoodCategory.PANTRY_STAPLES.title)
                ),
                instructions = listOf(
                    "Spoon Greek yogurt into a chilled breakfast bowl.",
                    "Thaw mixed berries slightly in microwave (15s) to release their natural sweet syrups.",
                    "Top yogurt with berries, crunchy rolled oats, and a drizzle of honey.",
                    "Enjoy as a fresh, energizing start to your day!"
                )
            ),
            Recipe(
                id = "recipe_pan_seared_salmon",
                title = "Crispy Skinned Salmon & Braised Spinach",
                description = "Restaurant-style pan-seared salmon served on a warm bed of garlic-wilted baby spinach.",
                prepTimeMinutes = 5,
                cookTimeMinutes = 10,
                servings = 2,
                calories = 490,
                category = "Dinner",
                dietaryTags = listOf("High-Protein", "Gluten-Free", "Low-Carb"),
                ingredients = listOf(
                    RecipeIngredient("Salmon Fillets (Frozen)", "2 pcs", FoodCategory.MEAT.title),
                    RecipeIngredient("Fresh Baby Spinach", "150 g", FoodCategory.PRODUCE.title),
                    RecipeIngredient("Garlic Bulbs", "2 cloves", FoodCategory.PRODUCE.title),
                    RecipeIngredient("Extra Virgin Olive Oil", "2 tbsp", FoodCategory.PANTRY_STAPLES.title),
                    RecipeIngredient("Sea Salt Flakes", "pinch", FoodCategory.SPICES.title)
                ),
                instructions = listOf(
                    "Thaw salmon fillets and pat very dry. Season with sea salt and black pepper.",
                    "Heat 1 tbsp olive oil in a skillet over high heat until very hot.",
                    "Place salmon skin-side down, press gently for 10 seconds, then cook for 4-5 minutes until skin is super crisp.",
                    "Flip and cook flesh side for 2-3 minutes. Transfer to plate.",
                    "In the same pan, quickly wilt spinach with minced garlic and remaining oil for 1 minute.",
                    "Serve crispy salmon on top of hot greens."
                )
            )
        )
    }

    fun getDefaultMealPlans(): List<MealPlanEntity> {
        return listOf(
            MealPlanEntity(
                dayOfWeek = "Monday",
                mealType = "Breakfast",
                recipeId = "recipe_spinach_scramble",
                title = "Fluffy Spinach & Cheddar Scramble",
                calories = 320
            ),
            MealPlanEntity(
                dayOfWeek = "Monday",
                mealType = "Dinner",
                recipeId = "recipe_garlic_pasta",
                title = "Creamy Garlic Parmesan Spaghetti",
                calories = 480
            ),
            MealPlanEntity(
                dayOfWeek = "Wednesday",
                mealType = "Breakfast",
                recipeId = "recipe_berry_yogurt_bowl",
                title = "High-Protein Berry Greek Yogurt Bowl",
                calories = 260
            ),
            MealPlanEntity(
                dayOfWeek = "Wednesday",
                mealType = "Dinner",
                recipeId = "recipe_crispy_chicken",
                title = "Garlic Herb Seared Chicken Breast",
                calories = 420
            ),
            MealPlanEntity(
                dayOfWeek = "Friday",
                mealType = "Dinner",
                recipeId = "recipe_pan_seared_salmon",
                title = "Crispy Skinned Salmon & Braised Spinach",
                calories = 490
            )
        )
    }
}
