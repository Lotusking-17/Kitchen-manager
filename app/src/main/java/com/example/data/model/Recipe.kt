package com.example.data.model

data class RecipeIngredient(
    val name: String,
    val amount: String,
    val category: String = "Pantry & Grains"
)

data class Recipe(
    val id: String,
    val title: String,
    val description: String,
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val servings: Int,
    val calories: Int,
    val category: String, // Breakfast, Lunch, Dinner, Snack
    val dietaryTags: List<String>, // Vegetarian, Vegan, Gluten-Free, High-Protein, Quick
    val ingredients: List<RecipeIngredient>,
    val instructions: List<String>,
    val imageUrl: String = ""
) {
    val totalTimeMinutes: Int get() = prepTimeMinutes + cookTimeMinutes

    fun calculateMatch(pantryNames: List<String>): RecipeMatchResult {
        val total = ingredients.size
        if (total == 0) return RecipeMatchResult(100, emptyList(), emptyList())

        val matched = mutableListOf<RecipeIngredient>()
        val missing = mutableListOf<RecipeIngredient>()

        for (ing in ingredients) {
            val ingWords = ing.name.lowercase().split(" ")
            val found = pantryNames.any { pantryItemName ->
                val pLower = pantryItemName.lowercase()
                ingWords.any { word -> word.length > 2 && pLower.contains(word) } ||
                        pLower.split(" ").any { it.length > 2 && ing.name.lowercase().contains(it) }
            }
            if (found) {
                matched.add(ing)
            } else {
                missing.add(ing)
            }
        }

        val percentage = (matched.size * 100) / total
        return RecipeMatchResult(
            matchPercentage = percentage,
            matchedIngredients = matched,
            missingIngredients = missing
        )
    }
}

data class RecipeMatchResult(
    val matchPercentage: Int,
    val matchedIngredients: List<RecipeIngredient>,
    val missingIngredients: List<RecipeIngredient>
)
