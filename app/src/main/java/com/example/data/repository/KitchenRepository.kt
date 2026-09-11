package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.MealPlanEntity
import com.example.data.local.PantryItemEntity
import com.example.data.local.ShoppingItemEntity
import com.example.data.model.Recipe
import com.example.data.model.SampleData
import com.example.data.model.SyncStatus
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KitchenRepository(private val database: AppDatabase) {

    private val pantryDao = database.pantryDao()
    private val shoppingDao = database.shoppingDao()
    private val mealPlanDao = database.mealPlanDao()

    val activePantryItems: Flow<List<PantryItemEntity>> = pantryDao.getActivePantryItems()
    val historyPantryItems: Flow<List<PantryItemEntity>> = pantryDao.getHistoryItems()
    val shoppingItems: Flow<List<ShoppingItemEntity>> = shoppingDao.getAllShoppingItems()
    val mealPlans: Flow<List<MealPlanEntity>> = mealPlanDao.getAllMealPlans()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile = _userProfile.asStateFlow()

    private val _syncStatus = MutableStateFlow(SyncStatus(isOnline = true, isSyncing = false, lastSyncMessage = "Cloud synced just now"))
    val syncStatus = _syncStatus.asStateFlow()

    val recipes: List<Recipe> = SampleData.getRecipes()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        val currentPantry = activePantryItems.first()
        if (currentPantry.isEmpty()) {
            pantryDao.insertAll(SampleData.getDefaultPantryItems())
        }
        val currentShopping = shoppingItems.first()
        if (currentShopping.isEmpty()) {
            shoppingDao.insertAll(SampleData.getDefaultShoppingList())
        }
        val currentMealPlans = mealPlans.first()
        if (currentMealPlans.isEmpty()) {
            mealPlanDao.insertAll(SampleData.getDefaultMealPlans())
        }
    }

    // --- Pantry Operations ---
    suspend fun addPantryItem(item: PantryItemEntity): Long = withContext(Dispatchers.IO) {
        val id = pantryDao.insertItem(item)
        triggerCloudSync("Added ${item.name} to Cloud")
        id
    }

    suspend fun updatePantryItem(item: PantryItemEntity) = withContext(Dispatchers.IO) {
        pantryDao.updateItem(item)
        triggerCloudSync("Updated ${item.name}")
    }

    suspend fun deletePantryItem(id: Long) = withContext(Dispatchers.IO) {
        pantryDao.deleteItemById(id)
        triggerCloudSync("Removed item from Cloud")
    }

    suspend fun markItemConsumed(id: Long) = withContext(Dispatchers.IO) {
        pantryDao.markConsumed(id)
        triggerCloudSync("Marked consumed - saved food waste!")
    }

    suspend fun markItemWasted(id: Long) = withContext(Dispatchers.IO) {
        pantryDao.markWasted(id)
        triggerCloudSync("Updated waste log in Cloud")
    }

    suspend fun adjustQuantity(id: Long, newQuantity: Double) = withContext(Dispatchers.IO) {
        if (newQuantity <= 0) {
            pantryDao.markConsumed(id)
        } else {
            pantryDao.updateQuantity(id, newQuantity)
        }
        triggerCloudSync("Synchronized quantity")
    }

    // --- Shopping List Operations ---
    suspend fun addShoppingItem(item: ShoppingItemEntity): Long = withContext(Dispatchers.IO) {
        val id = shoppingDao.insertItem(item)
        triggerCloudSync("Added ${item.name} to shared shopping list")
        id
    }

    suspend fun toggleShoppingItemChecked(id: Long, isChecked: Boolean) = withContext(Dispatchers.IO) {
        shoppingDao.setChecked(id, isChecked)
        triggerCloudSync("Cart status synced")
    }

    suspend fun deleteShoppingItem(id: Long) = withContext(Dispatchers.IO) {
        shoppingDao.deleteById(id)
    }

    suspend fun clearCheckedShoppingItems() = withContext(Dispatchers.IO) {
        shoppingDao.clearCheckedItems()
        triggerCloudSync("Cleaned completed items")
    }

    // Key KitchenPal Feature: Move checked shopping items into Pantry/Fridge!
    suspend fun transferCheckedToPantry(): Int = withContext(Dispatchers.IO) {
        val checkedItems = shoppingDao.getCheckedItems()
        if (checkedItems.isEmpty()) return@withContext 0

        val now = System.currentTimeMillis()
        val defaultExpiryDays = 10L * 24 * 60 * 60 * 1000L

        val newPantryEntities = checkedItems.map { shopItem ->
            PantryItemEntity(
                name = shopItem.name,
                category = shopItem.category,
                location = shopItem.targetLocation,
                quantity = shopItem.quantity,
                unit = shopItem.unit,
                expiryTimestamp = now + defaultExpiryDays,
                estimatedPrice = shopItem.estimatedPrice,
                notes = if (shopItem.notes.isNotEmpty()) shopItem.notes else "Restocked from grocery trip"
            )
        }

        pantryDao.insertAll(newPantryEntities)
        shoppingDao.clearCheckedItems()
        triggerCloudSync("Restocked ${checkedItems.size} items to pantry")
        checkedItems.size
    }

    suspend fun restockPantryItemToShopping(pantryItem: PantryItemEntity) = withContext(Dispatchers.IO) {
        val shoppingItem = ShoppingItemEntity(
            name = pantryItem.name,
            category = pantryItem.category,
            targetLocation = pantryItem.location,
            quantity = if (pantryItem.quantity > 0) pantryItem.quantity else 1.0,
            unit = pantryItem.unit,
            estimatedPrice = pantryItem.estimatedPrice,
            notes = "Restocked from pantry (${pantryItem.location})"
        )
        shoppingDao.insertItem(shoppingItem)
        triggerCloudSync("Restocked ${pantryItem.name} to shopping list")
    }

    // --- Meal Planning Operations ---
    suspend fun setMealPlan(day: String, mealType: String, recipe: Recipe) = withContext(Dispatchers.IO) {
        mealPlanDao.deleteBySlot(day, mealType)
        mealPlanDao.insertMealPlan(
            MealPlanEntity(
                dayOfWeek = day,
                mealType = mealType,
                recipeId = recipe.id,
                title = recipe.title,
                calories = recipe.calories,
                servings = recipe.servings
            )
        )
        triggerCloudSync("Updated meal plan for $day $mealType")
    }

    suspend fun removeMealPlan(id: Long) = withContext(Dispatchers.IO) {
        mealPlanDao.deleteById(id)
        triggerCloudSync("Removed planned meal")
    }

    // Generate grocery list from weekly meal plan missing ingredients
    suspend fun addMissingIngredientsFromMealPlan(recipe: Recipe, currentPantryNames: List<String>): Int = withContext(Dispatchers.IO) {
        val matchResult = recipe.calculateMatch(currentPantryNames)
        var addedCount = 0
        for (missing in matchResult.missingIngredients) {
            shoppingDao.insertItem(
                ShoppingItemEntity(
                    name = missing.name,
                    category = missing.category,
                    quantity = 1.0,
                    unit = "item",
                    estimatedPrice = 3.00,
                    notes = "Required for ${recipe.title}",
                    addedFromRecipe = recipe.title
                )
            )
            addedCount++
        }
        if (addedCount > 0) {
            triggerCloudSync("Added $addedCount recipe items to shopping list")
        }
        addedCount
    }

    // --- Online, Cloud Sync & User Profile Operations ---
    fun toggleOnlineMode(isOnline: Boolean) {
        _syncStatus.value = _syncStatus.value.copy(
            isOnline = isOnline,
            lastSyncMessage = if (isOnline) "Cloud sync active" else "Working offline (cached)"
        )
    }

    suspend fun manualCloudSync() = withContext(Dispatchers.IO) {
        if (!_syncStatus.value.isOnline) return@withContext
        _syncStatus.value = _syncStatus.value.copy(isSyncing = true, lastSyncMessage = "Synchronizing with cloud...")
        delay(750) // Realistic cloud sync latency
        val now = System.currentTimeMillis()
        _userProfile.value = _userProfile.value.copy(lastSyncTime = now)
        _syncStatus.value = _syncStatus.value.copy(
            isSyncing = false,
            lastSyncMessage = "All devices synchronized"
        )
    }

    private fun triggerCloudSync(actionMessage: String) {
        if (!_syncStatus.value.isOnline) return
        CoroutineScope(Dispatchers.IO).launch {
            _syncStatus.value = _syncStatus.value.copy(isSyncing = true)
            delay(400)
            val now = System.currentTimeMillis()
            _userProfile.value = _userProfile.value.copy(lastSyncTime = now)
            _syncStatus.value = _syncStatus.value.copy(
                isSyncing = false,
                lastSyncMessage = actionMessage
            )
        }
    }

    fun login(email: String, name: String) {
        _userProfile.value = _userProfile.value.copy(
            email = email,
            displayName = name.ifBlank { email.substringBefore("@").replaceFirstChar { it.uppercase() } },
            isLoggedIn = true,
            isCloudSyncEnabled = true
        )
        triggerCloudSync("Logged in as $email")
    }

    fun logout() {
        _userProfile.value = _userProfile.value.copy(
            isLoggedIn = false,
            email = "",
            displayName = "Guest User"
        )
    }

    fun updateHousehold(name: String, code: String) {
        _userProfile.value = _userProfile.value.copy(
            householdName = name,
            householdCode = code
        )
        triggerCloudSync("Connected to household: $name")
    }

    fun toggleDietaryPreference(pref: String) {
        val current = _userProfile.value.dietaryPreferences.toMutableSet()
        if (current.contains(pref)) {
            current.remove(pref)
        } else {
            current.add(pref)
        }
        _userProfile.value = _userProfile.value.copy(dietaryPreferences = current)
    }

    fun updateCurrency(code: String, symbol: String) {
        _userProfile.value = _userProfile.value.copy(
            currencyCode = code,
            currencySymbol = symbol
        )
        triggerCloudSync("Currency updated to $code ($symbol)")
    }
}
