package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.MealPlanEntity
import com.example.data.local.PantryItemEntity
import com.example.data.local.ShoppingItemEntity
import com.example.data.model.ExpiryStatus
import com.example.data.model.Recipe
import com.example.data.model.RecipeMatchResult
import com.example.data.model.StorageLocation
import com.example.data.model.SyncStatus
import com.example.data.model.UserProfile
import com.example.data.repository.KitchenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab(val title: String) {
    PANTRY("Pantry"),
    RECIPES("Recipes"),
    SHOPPING("Shopping"),
    PLANNER("Planner"),
    ANALYTICS("Eco & Stats")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KitchenRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = KitchenRepository(db)
    }

    val activePantryItems: StateFlow<List<PantryItemEntity>> = repository.activePantryItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historyItems: StateFlow<List<PantryItemEntity>> = repository.historyPantryItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shoppingItems: StateFlow<List<ShoppingItemEntity>> = repository.shoppingItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mealPlans: StateFlow<List<MealPlanEntity>> = repository.mealPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val syncStatus: StateFlow<SyncStatus> = repository.syncStatus
    val allRecipes: List<Recipe> = repository.recipes

    // Tab Navigation
    private val _currentTab = MutableStateFlow(AppNavTab.PANTRY)
    val currentTab = _currentTab.asStateFlow()

    // Filters for Pantry
    private val _selectedLocation = MutableStateFlow("All")
    val selectedLocation = _selectedLocation.asStateFlow()

    private val _selectedFreshnessFilter = MutableStateFlow("All")
    val selectedFreshnessFilter = _selectedFreshnessFilter.asStateFlow()

    private val _pantrySearchQuery = MutableStateFlow("")
    val pantrySearchQuery = _pantrySearchQuery.asStateFlow()

    // Filtered Pantry Items
    val filteredPantryItems: StateFlow<List<PantryItemEntity>> = combine(
        activePantryItems,
        _selectedLocation,
        _selectedFreshnessFilter,
        _pantrySearchQuery
    ) { items, location, freshness, query ->
        items.filter { item ->
            val matchesLocation = if (location == "All") true else item.location.equals(location, ignoreCase = true)
            val (status, _) = item.expiryStatusInfo
            val matchesFreshness = when (freshness) {
                "Expiring Soon" -> status == ExpiryStatus.EXPIRING_SOON
                "Expired" -> status == ExpiryStatus.EXPIRED
                "Fresh" -> status == ExpiryStatus.FRESH
                else -> true
            }
            val matchesQuery = if (query.isBlank()) true else {
                item.name.contains(query, ignoreCase = true) || item.category.contains(query, ignoreCase = true)
            }
            matchesLocation && matchesFreshness && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Urgent alerts (expiring in 3 days or expired)
    val urgentPantryAlerts: StateFlow<List<PantryItemEntity>> = activePantryItems.combine(_selectedLocation) { items, _ ->
        items.filter {
            val (status, _) = it.expiryStatusInfo
            status == ExpiryStatus.EXPIRING_SOON || status == ExpiryStatus.EXPIRED
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Recipes with live match scores based on pantry items
    val matchedRecipes: StateFlow<List<Pair<Recipe, RecipeMatchResult>>> = activePantryItems.combine(userProfile) { items, profile ->
        val pantryNames = items.map { it.name }
        allRecipes
            .filter { recipe ->
                // Filter dietary preferences
                if (profile.dietaryPreferences.contains("Vegetarian") && !recipe.dietaryTags.contains("Vegetarian")) false
                else if (profile.dietaryPreferences.contains("Gluten-Free") && !recipe.dietaryTags.contains("Gluten-Free")) false
                else if (profile.dietaryPreferences.contains("High-Protein") && !recipe.dietaryTags.contains("High-Protein")) false
                else true
            }
            .map { recipe ->
                Pair(recipe, recipe.calculateMatch(pantryNames))
            }
            .sortedByDescending { it.second.matchPercentage }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Feedback Banner message
    private val _snackMessage = MutableStateFlow<String?>(null)
    val snackMessage = _snackMessage.asStateFlow()

    // Dialog state holders
    private val _isAddPantryDialogOpen = MutableStateFlow(false)
    val isAddPantryDialogOpen = _isAddPantryDialogOpen.asStateFlow()

    private val _editingPantryItem = MutableStateFlow<PantryItemEntity?>(null)
    val editingPantryItem = _editingPantryItem.asStateFlow()

    private val _isAddShoppingDialogOpen = MutableStateFlow(false)
    val isAddShoppingDialogOpen = _isAddShoppingDialogOpen.asStateFlow()

    private val _selectedRecipeForDetail = MutableStateFlow<Recipe?>(null)
    val selectedRecipeForDetail = _selectedRecipeForDetail.asStateFlow()

    private val _isAccountSheetOpen = MutableStateFlow(false)
    val isAccountSheetOpen = _isAccountSheetOpen.asStateFlow()

    // User actions
    fun setTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun setLocationFilter(loc: String) {
        _selectedLocation.value = loc
    }

    fun setFreshnessFilter(filter: String) {
        _selectedFreshnessFilter.value = filter
    }

    fun setPantrySearchQuery(query: String) {
        _pantrySearchQuery.value = query
    }

    fun showSnack(msg: String) {
        _snackMessage.value = msg
    }

    fun clearSnack() {
        _snackMessage.value = null
    }

    fun openAddPantryDialog() {
        _editingPantryItem.value = null
        _isAddPantryDialogOpen.value = true
    }

    fun openEditPantryDialog(item: PantryItemEntity) {
        _editingPantryItem.value = item
        _isAddPantryDialogOpen.value = true
    }

    fun closeAddPantryDialog() {
        _isAddPantryDialogOpen.value = false
        _editingPantryItem.value = null
    }

    fun openAddShoppingDialog() {
        _isAddShoppingDialogOpen.value = true
    }

    fun closeAddShoppingDialog() {
        _isAddShoppingDialogOpen.value = false
    }

    fun openRecipeDetail(recipe: Recipe) {
        _selectedRecipeForDetail.value = recipe
    }

    fun closeRecipeDetail() {
        _selectedRecipeForDetail.value = null
    }

    fun openAccountSheet() {
        _isAccountSheetOpen.value = true
    }

    fun closeAccountSheet() {
        _isAccountSheetOpen.value = false
    }

    // Pantry Operations
    fun savePantryItem(
        id: Long,
        name: String,
        category: String,
        location: String,
        quantity: Double,
        unit: String,
        expiryTimestamp: Long,
        notes: String,
        estimatedPrice: Double = 3.50
    ) {
        viewModelScope.launch {
            if (id == 0L) {
                repository.addPantryItem(
                    PantryItemEntity(
                        name = name,
                        category = category,
                        location = location,
                        quantity = quantity,
                        unit = unit,
                        expiryTimestamp = expiryTimestamp,
                        notes = notes,
                        estimatedPrice = estimatedPrice
                    )
                )
                showSnack("Added $name to $location")
            } else {
                repository.updatePantryItem(
                    PantryItemEntity(
                        id = id,
                        name = name,
                        category = category,
                        location = location,
                        quantity = quantity,
                        unit = unit,
                        expiryTimestamp = expiryTimestamp,
                        notes = notes,
                        estimatedPrice = estimatedPrice
                    )
                )
                showSnack("Updated $name")
            }
            closeAddPantryDialog()
        }
    }

    fun deletePantryItem(id: Long, name: String) {
        viewModelScope.launch {
            repository.deletePantryItem(id)
            showSnack("Removed $name")
        }
    }

    fun markConsumed(id: Long, name: String) {
        viewModelScope.launch {
            repository.markItemConsumed(id)
            showSnack("Yay! Enjoyed $name. Saved food waste!")
        }
    }

    fun markWasted(id: Long, name: String) {
        viewModelScope.launch {
            repository.markItemWasted(id)
            showSnack("Logged $name to waste tracker")
        }
    }

    fun adjustPantryQuantity(item: PantryItemEntity, delta: Double) {
        viewModelScope.launch {
            val newQty = (item.quantity + delta).coerceAtLeast(0.0)
            repository.adjustQuantity(item.id, newQty)
        }
    }

    fun restockItemToShopping(item: PantryItemEntity) {
        viewModelScope.launch {
            repository.restockPantryItemToShopping(item)
            showSnack("Added ${item.name} to Shopping List")
        }
    }

    // Shopping Operations
    fun addShoppingItem(
        name: String,
        category: String,
        targetLocation: String,
        quantity: Double,
        unit: String,
        price: Double,
        notes: String
    ) {
        viewModelScope.launch {
            repository.addShoppingItem(
                ShoppingItemEntity(
                    name = name,
                    category = category,
                    targetLocation = targetLocation,
                    quantity = quantity,
                    unit = unit,
                    estimatedPrice = price,
                    notes = notes
                )
            )
            showSnack("Added $name to shopping list")
            closeAddShoppingDialog()
        }
    }

    fun toggleShoppingChecked(id: Long, isChecked: Boolean) {
        viewModelScope.launch {
            repository.toggleShoppingItemChecked(id, isChecked)
        }
    }

    fun deleteShoppingItem(id: Long) {
        viewModelScope.launch {
            repository.deleteShoppingItem(id)
        }
    }

    fun clearCheckedShopping() {
        viewModelScope.launch {
            repository.clearCheckedShoppingItems()
            showSnack("Cleared completed grocery items")
        }
    }

    fun transferPurchasedToPantry() {
        viewModelScope.launch {
            val count = repository.transferCheckedToPantry()
            if (count > 0) {
                showSnack("Transferred $count purchased groceries to your Pantry!")
            } else {
                showSnack("Check off items in cart before transferring to pantry")
            }
        }
    }

    // Recipe Operations
    fun addMissingIngredientsToGrocery(recipe: Recipe) {
        viewModelScope.launch {
            val pantryNames = activePantryItems.value.map { it.name }
            val count = repository.addMissingIngredientsFromMealPlan(recipe, pantryNames)
            showSnack("Added $count missing ingredients for ${recipe.title} to Grocery List!")
        }
    }

    // Meal Plan Operations
    fun planRecipe(day: String, mealType: String, recipe: Recipe) {
        viewModelScope.launch {
            repository.setMealPlan(day, mealType, recipe)
            showSnack("Planned ${recipe.title} for $day $mealType")
        }
    }

    fun removePlannedMeal(id: Long) {
        viewModelScope.launch {
            repository.removeMealPlan(id)
            showSnack("Meal plan updated")
        }
    }

    // Account & Sync Operations
    fun toggleOnline(isOnline: Boolean) {
        repository.toggleOnlineMode(isOnline)
        showSnack(if (isOnline) "Connected to Cloud Sync" else "Switched to Offline Mode")
    }

    fun triggerManualSync() {
        viewModelScope.launch {
            repository.manualCloudSync()
            showSnack("Pantry successfully synced with Cloud")
        }
    }

    fun login(email: String, name: String) {
        repository.login(email, name)
        showSnack("Logged in as $email")
        closeAccountSheet()
    }

    fun logout() {
        repository.logout()
        showSnack("Logged out. Switched to local guest mode")
    }

    fun updateHousehold(name: String, code: String) {
        repository.updateHousehold(name, code)
        showSnack("Household updated: $name")
    }

    fun toggleDietaryPreference(pref: String) {
        repository.toggleDietaryPreference(pref)
    }

    fun updateCurrency(code: String, symbol: String) {
        repository.updateCurrency(code, symbol)
        showSnack("Currency changed to $code ($symbol)")
    }
}
