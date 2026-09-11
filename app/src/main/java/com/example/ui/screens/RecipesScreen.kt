package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Recipe
import com.example.data.model.RecipeIngredient
import com.example.data.model.RecipeMatchResult
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val matchedRecipes by viewModel.matchedRecipes.collectAsState()
    val activePantry by viewModel.activePantryItems.collectAsState()
    val selectedRecipeForDetail by viewModel.selectedRecipeForDetail.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }

    val filteredRecipes = remember(matchedRecipes, selectedFilter) {
        when (selectedFilter) {
            "Can Cook Now" -> matchedRecipes.filter { it.second.matchPercentage == 100 }
            "High Match" -> matchedRecipes.filter { it.second.matchPercentage >= 70 }
            "Quick (<20m)" -> matchedRecipes.filter { it.first.totalTimeMinutes <= 20 }
            "High-Protein" -> matchedRecipes.filter { it.first.dietaryTags.contains("High-Protein") }
            "Vegetarian" -> matchedRecipes.filter { it.first.dietaryTags.contains("Vegetarian") }
            else -> matchedRecipes
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            // Header Hero Banner: "Cook with What You Have"
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = EmeraldContainerLight.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimaryLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, tint = EmeraldOnPrimaryLight)
                            }
                            Column {
                                Text(
                                    text = "Smart Recipe Matcher",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OnEmeraldContainerLight
                                )
                                Text(
                                    text = "Pantry analyzed: ${activePantry.size} active ingredients",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnEmeraldContainerLight.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val readyCount = matchedRecipes.count { it.second.matchPercentage == 100 }
                        Text(
                            text = if (readyCount > 0) "🎉 You have all ingredients for $readyCount meals right now!" else "Add 1 or 2 items to unlock complete recipes!",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldPrimaryLight
                        )
                    }
                }
            }

            // Recipe Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    val filters = listOf("All", "Can Cook Now", "High Match", "Quick (<20m)", "High-Protein", "Vegetarian")
                    items(filters) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) }
                        )
                    }
                }
            }

            // Recipe Cards
            if (filteredRecipes.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No recipes match this filter",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try clearing filters or adding more staples to your pantry.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredRecipes, key = { it.first.id }) { (recipe, matchResult) ->
                    RecipeCard(
                        recipe = recipe,
                        matchResult = matchResult,
                        onClick = { viewModel.openRecipeDetail(recipe) },
                        onAddMissing = { viewModel.addMissingIngredientsToGrocery(recipe) }
                    )
                }
            }
        }
    }

    // Recipe Detail Modal Dialog
    selectedRecipeForDetail?.let { recipe ->
        val pantryNames = activePantry.map { it.name }
        val matchResult = remember(recipe, pantryNames) { recipe.calculateMatch(pantryNames) }
        RecipeDetailDialog(
            recipe = recipe,
            matchResult = matchResult,
            onDismiss = { viewModel.closeRecipeDetail() },
            onAddMissingToGrocery = {
                viewModel.addMissingIngredientsToGrocery(recipe)
            },
            onCookMeal = {
                viewModel.showSnack("Cooked ${recipe.title}! Ingredients recorded.")
                viewModel.closeRecipeDetail()
            },
            onPlanMeal = { day, mealType ->
                viewModel.planRecipe(day, mealType, recipe)
            }
        )
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    matchResult: RecipeMatchResult,
    onClick: () -> Unit,
    onAddMissing: () -> Unit
) {
    val isFullMatch = matchResult.matchPercentage == 100
    val matchColor = if (isFullMatch) FreshGreen else if (matchResult.matchPercentage >= 70) ExpiringWarning else MaterialTheme.colorScheme.outline

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
            .testTag("recipe_card_${recipe.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Match Pill & Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Match percentage badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(matchColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            if (isFullMatch) Icons.Default.CheckCircle else Icons.Default.PieChart,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = matchColor
                        )
                        Text(
                            text = if (isFullMatch) "100% MATCH • READY!" else "${matchResult.matchPercentage}% MATCH (${matchResult.missingIngredients.size} missing)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = matchColor
                        )
                    }
                }

                Text(
                    text = recipe.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = recipe.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Meta row: Time, Calories, Servings
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                    Text(text = "${recipe.totalTimeMinutes}m", style = MaterialTheme.typography.bodySmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, modifier = Modifier.size(14.dp), tint = AmberSecondaryLight)
                    Text(text = "${recipe.calories} kcal", style = MaterialTheme.typography.bodySmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
                    Text(text = "${recipe.servings} serv", style = MaterialTheme.typography.bodySmall)
                }
            }

            // If missing ingredients, show quick add button
            if (matchResult.missingIngredients.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Need: " + matchResult.missingIngredients.take(2).joinToString { it.name } + if (matchResult.missingIngredients.size > 2) " +${matchResult.missingIngredients.size - 2}" else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = AmberSecondaryLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = onAddMissing,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add to List", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailDialog(
    recipe: Recipe,
    matchResult: RecipeMatchResult,
    onDismiss: () -> Unit,
    onAddMissingToGrocery: () -> Unit,
    onCookMeal: () -> Unit,
    onPlanMeal: (day: String, mealType: String) -> Unit
) {
    var showPlanSelector by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf("Monday") }
    var selectedMealType by remember { mutableStateOf("Dinner") }

    val checkedInstructions = remember { mutableStateMapOf<Int, Boolean>() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { showPlanSelector = !showPlanSelector }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Plan meal", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                // Title & Category
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${recipe.category} • ${recipe.dietaryTags.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Prep", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${recipe.prepTimeMinutes}m", fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Cook", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${recipe.cookTimeMinutes}m", fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Calories", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${recipe.calories}", fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Servings", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${recipe.servings}", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Meal Planner Inline Expandable
                if (showPlanSelector) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Schedule in Meal Plan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday").forEach { day ->
                                    FilterChip(
                                        selected = selectedDay == day,
                                        onClick = { selectedDay = day },
                                        label = { Text(day.take(3)) }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("Breakfast", "Lunch", "Dinner", "Snack").forEach { type ->
                                    FilterChip(
                                        selected = selectedMealType == type,
                                        onClick = { selectedMealType = type },
                                        label = { Text(type) }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    onPlanMeal(selectedDay, selectedMealType)
                                    showPlanSelector = false
                                }
                            ) {
                                Text("Confirm Plan for $selectedDay")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                }

                // Ingredients Header + Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "INGREDIENTS (${recipe.ingredients.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (matchResult.missingIngredients.isNotEmpty()) {
                        Button(
                            onClick = onAddMissingToGrocery,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Missing (${matchResult.missingIngredients.size})", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Ingredients list with In-Pantry vs Missing indicator
                recipe.ingredients.forEach { ing ->
                    val isInPantry = matchResult.matchedIngredients.any { it.name.equals(ing.name, ignoreCase = true) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (isInPantry) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (isInPantry) FreshGreen else AmberSecondaryLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = ing.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = ing.amount,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isInPantry) "In Pantry" else "Missing",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isInPantry) FreshGreen else AmberSecondaryLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Cooking Steps
                Text(
                    text = "STEP-BY-STEP INSTRUCTIONS",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(10.dp))

                recipe.instructions.forEachIndexed { index, instruction ->
                    val isStepDone = checkedInstructions[index] == true
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { checkedInstructions[index] = !isStepDone },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isStepDone) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isStepDone) FreshGreen else MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${index + 1}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = instruction,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Cooked meal action
                Button(
                    onClick = onCookMeal,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Restaurant, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("I Made This Meal! (Record in History)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
