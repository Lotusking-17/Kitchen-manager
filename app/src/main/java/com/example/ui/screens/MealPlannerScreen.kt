package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MealPlanEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.AmberSecondaryLight
import com.example.ui.theme.CoralTertiaryLight
import com.example.ui.theme.EmeraldContainerLight
import com.example.ui.theme.EmeraldPrimaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlannerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val mealPlans by viewModel.mealPlans.collectAsState()
    val recipes = viewModel.allRecipes
    val activePantry by viewModel.activePantryItems.collectAsState()

    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    var selectedDay by remember { mutableStateOf("Monday") }

    val mealsForSelectedDay = mealPlans.filter { it.dayOfWeek.equals(selectedDay, ignoreCase = true) }
    val dayCalories = mealsForSelectedDay.sumOf { it.calories }

    var showAssignRecipeDialog by remember { mutableStateOf<String?>(null) } // holds mealType if open

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            // Header Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Weekly Meal Planner",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$selectedDay • $dayCalories kcal planned",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldContainerLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = EmeraldPrimaryLight)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Smart feature: Generate grocery list for the whole week!
                        Button(
                            onClick = {
                                val pantryNames = activePantry.map { it.name }
                                var totalAdded = 0
                                mealPlans.forEach { plan ->
                                    val recipe = recipes.find { it.id == plan.recipeId }
                                    if (recipe != null) {
                                        viewModel.addMissingIngredientsToGrocery(recipe)
                                        totalAdded++
                                    }
                                }
                                viewModel.showSnack("Synced weekly meal ingredients with Grocery List!")
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.SyncAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Auto-Generate Groceries for Week")
                        }
                    }
                }
            }

            // Days of the week row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    items(daysOfWeek) { day ->
                        val isSelected = selectedDay == day
                        val dayPlansCount = mealPlans.count { it.dayOfWeek.equals(day, ignoreCase = true) }
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDay = day },
                            label = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(day.take(3), fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                    if (dayPlansCount > 0) {
                                        Text("$dayPlansCount meals", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            },
                            modifier = Modifier.testTag("day_tab_$day")
                        )
                    }
                }
            }

            // Meal Slots for the day (Breakfast, Lunch, Dinner, Snack)
            val slots = listOf(
                "Breakfast" to "☀️",
                "Lunch" to "🥗",
                "Dinner" to "🍲",
                "Snack" to "🍎"
            )

            items(slots) { (slotName, emoji) ->
                val plannedMeal = mealsForSelectedDay.find { it.mealType.equals(slotName, ignoreCase = true) }
                MealSlotCard(
                    slotName = slotName,
                    emoji = emoji,
                    plannedMeal = plannedMeal,
                    onAssignClick = { showAssignRecipeDialog = slotName },
                    onRemoveClick = {
                        plannedMeal?.let { viewModel.removePlannedMeal(it.id) }
                    }
                )
            }
        }
    }

    // Assign Recipe Dialog
    showAssignRecipeDialog?.let { slotName ->
        AlertDialog(
            onDismissRequest = { showAssignRecipeDialog = null },
            title = { Text("Plan $slotName for $selectedDay") },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(recipes) { recipe ->
                        ListItem(
                            headlineContent = { Text(recipe.title, fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("${recipe.calories} kcal • ${recipe.totalTimeMinutes}m • ${recipe.category}") },
                            leadingContent = {
                                Icon(Icons.Default.RestaurantMenu, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            modifier = Modifier.clickable {
                                viewModel.planRecipe(selectedDay, slotName, recipe)
                                showAssignRecipeDialog = null
                            }
                        )
                        HorizontalDivider()
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAssignRecipeDialog = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun MealSlotCard(
    slotName: String,
    emoji: String,
    plannedMeal: MealPlanEntity?,
    onAssignClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(emoji, fontSize = 20.sp)
                    Text(
                        text = slotName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (plannedMeal != null) {
                    IconButton(onClick = onRemoveClick) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", tint = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (plannedMeal != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = plannedMeal.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${plannedMeal.calories} calories • ${plannedMeal.servings} servings",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(
                        onClick = onAssignClick,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Change", fontSize = 12.sp)
                    }
                }
            } else {
                OutlinedButton(
                    onClick = onAssignClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Plan a meal or recipe for $slotName")
                }
            }
        }
    }
}
