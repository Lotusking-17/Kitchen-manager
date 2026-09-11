package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.PantryItemEntity
import com.example.data.model.FoodCategory
import com.example.data.model.StorageLocation

data class QuickStaple(
    val name: String,
    val category: FoodCategory,
    val location: StorageLocation,
    val defaultDays: Int,
    val defaultUnit: String,
    val defaultQty: Double,
    val estimatedPrice: Double
)

val POPULAR_STAPLES = listOf(
    QuickStaple("Whole Milk", FoodCategory.DAIRY, StorageLocation.FRIDGE, 7, "gallon", 1.0, 4.29),
    QuickStaple("Eggs", FoodCategory.DAIRY, StorageLocation.FRIDGE, 14, "pcs", 12.0, 3.99),
    QuickStaple("Baby Spinach", FoodCategory.PRODUCE, StorageLocation.FRIDGE, 5, "g", 250.0, 2.99),
    QuickStaple("Tomatoes", FoodCategory.PRODUCE, StorageLocation.FRIDGE, 7, "pcs", 4.0, 3.19),
    QuickStaple("Chicken Breast", FoodCategory.MEAT, StorageLocation.FRIDGE, 3, "g", 500.0, 7.99),
    QuickStaple("Cheddar Cheese", FoodCategory.DAIRY, StorageLocation.FRIDGE, 21, "g", 200.0, 3.89),
    QuickStaple("Greek Yogurt", FoodCategory.DAIRY, StorageLocation.FRIDGE, 10, "g", 500.0, 4.49),
    QuickStaple("Spaghetti", FoodCategory.PANTRY_STAPLES, StorageLocation.PANTRY, 180, "g", 500.0, 1.89),
    QuickStaple("Olive Oil", FoodCategory.PANTRY_STAPLES, StorageLocation.PANTRY, 300, "ml", 750.0, 10.99),
    QuickStaple("Frozen Berries", FoodCategory.FROZEN, StorageLocation.FREEZER, 90, "g", 400.0, 4.19),
    QuickStaple("Crushed Tomatoes", FoodCategory.CANNED, StorageLocation.PANTRY, 365, "cans", 2.0, 2.49)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPantryItemDialog(
    itemToEdit: PantryItemEntity?,
    currencySymbol: String = "$",
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        name: String,
        category: String,
        location: String,
        quantity: Double,
        unit: String,
        expiryTimestamp: Long,
        notes: String,
        price: Double
    ) -> Unit
) {
    val dayMs = 24 * 60 * 60 * 1000L
    val now = System.currentTimeMillis()

    var name by remember { mutableStateOf(itemToEdit?.name ?: "") }
    var selectedCategory by remember {
        mutableStateOf(itemToEdit?.let { FoodCategory.fromString(it.category) } ?: FoodCategory.PRODUCE)
    }
    var selectedLocation by remember {
        mutableStateOf(itemToEdit?.let { StorageLocation.fromString(it.location) } ?: StorageLocation.FRIDGE)
    }
    var quantityText by remember { mutableStateOf((itemToEdit?.quantity ?: 1.0).toString()) }
    var unitText by remember { mutableStateOf(itemToEdit?.unit ?: "pcs") }
    var priceText by remember { mutableStateOf((itemToEdit?.estimatedPrice ?: 3.50).toString()) }
    var notesText by remember { mutableStateOf(itemToEdit?.notes ?: "") }

    var expiryDaysOffset by remember {
        val initialDays = if (itemToEdit != null) {
            val diff = itemToEdit.expiryTimestamp - now
            (diff / dayMs).toInt().coerceAtLeast(1)
        } else {
            7
        }
        mutableIntStateOf(initialDays)
    }

    var showBarcodeSuccess by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("add_edit_pantry_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (itemToEdit == null) "Add to Kitchen" else "Edit Item",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Track freshness and expiry automatically",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Staples row (only for new item)
                if (itemToEdit == null) {
                    Text(
                        text = "QUICK STAPLES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(POPULAR_STAPLES) { staple ->
                            SuggestionChip(
                                onClick = {
                                    name = staple.name
                                    selectedCategory = staple.category
                                    selectedLocation = staple.location
                                    expiryDaysOffset = staple.defaultDays
                                    unitText = staple.defaultUnit
                                    quantityText = staple.defaultQty.toString()
                                    priceText = staple.estimatedPrice.toString()
                                },
                                label = { Text("${staple.category.emoji} ${staple.name}") },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Item Name Field + Barcode Mock
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Food or Item Name") },
                    placeholder = { Text("e.g. Organic Strawberries, Cheddar") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_name_input"),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                // Simulate Barcode Scan
                                val randomStaple = POPULAR_STAPLES.random()
                                name = randomStaple.name
                                selectedCategory = randomStaple.category
                                selectedLocation = randomStaple.location
                                expiryDaysOffset = randomStaple.defaultDays
                                unitText = randomStaple.defaultUnit
                                quantityText = randomStaple.defaultQty.toString()
                                priceText = randomStaple.estimatedPrice.toString()
                                showBarcodeSuccess = true
                            },
                            modifier = Modifier.testTag("barcode_scan_button")
                        ) {
                            Icon(
                                Icons.Default.QrCodeScanner,
                                contentDescription = "Scan barcode",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )

                if (showBarcodeSuccess) {
                    Text(
                        text = "✓ Barcode identified staple automatically!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Storage Location Selector
                Text(
                    text = "STORAGE LOCATION",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StorageLocation.entries.forEach { loc ->
                        val isSelected = selectedLocation == loc
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedLocation = loc },
                            label = { Text("${loc.emoji} ${loc.title}") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quantity, Unit & Est. Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Qty") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quantity_input")
                    )
                    OutlinedTextField(
                        value = unitText,
                        onValueChange = { unitText = it },
                        label = { Text("Unit") },
                        placeholder = { Text("pcs, g") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("unit_input")
                    )
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Est. ($currencySymbol)") },
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("price_input")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Category selector
                Text(
                    text = "FOOD CATEGORY",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(FoodCategory.entries.toList()) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text("${cat.emoji} ${cat.title}") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Expiry Date Presets
                Text(
                    text = "EXPIRY / FRESHNESS DATE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "2 Days" to 2,
                        "5 Days" to 5,
                        "1 Wk" to 7,
                        "2 Wks" to 14,
                        "1 Mo" to 30,
                        "6 Mos" to 180
                    ).forEach { (label, days) ->
                        val isSelected = expiryDaysOffset == days
                        FilterChip(
                            selected = isSelected,
                            onClick = { expiryDaysOffset = days },
                            label = { Text(label) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Notes
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Notes (optional)") },
                    placeholder = { Text("e.g. Opened, brand, recipe idea") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                val qty = quantityText.toDoubleOrNull() ?: 1.0
                                val price = priceText.toDoubleOrNull() ?: 3.50
                                val expiryTime = now + (expiryDaysOffset * dayMs)
                                onSave(
                                    itemToEdit?.id ?: 0L,
                                    name.trim(),
                                    selectedCategory.title,
                                    selectedLocation.title,
                                    qty,
                                    unitText.trim().ifBlank { "pcs" },
                                    expiryTime,
                                    notesText.trim(),
                                    price
                                )
                            }
                        },
                        enabled = name.isNotBlank(),
                        modifier = Modifier.testTag("save_pantry_item_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (itemToEdit == null) "Add to Pantry" else "Save Changes")
                    }
                }
            }
        }
    }
}

@Composable
fun AddShoppingItemDialog(
    currencySymbol: String = "$",
    onDismiss: () -> Unit,
    onSave: (name: String, category: String, targetLocation: String, quantity: Double, unit: String, price: Double, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FoodCategory.PRODUCE) }
    var targetLocation by remember { mutableStateOf(StorageLocation.FRIDGE) }
    var quantityText by remember { mutableStateOf("1") }
    var unitText by remember { mutableStateOf("pcs") }
    var priceText by remember { mutableStateOf("3.00") }
    var notesText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("add_shopping_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add to Grocery List",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    placeholder = { Text("e.g. Sourdough Bread, Almond Milk") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("shopping_item_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("STORE AISLE / CATEGORY", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(FoodCategory.entries.toList()) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text("${cat.emoji} ${cat.title}") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("RESTOCK TO WHERE?", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StorageLocation.entries.forEach { loc ->
                        FilterChip(
                            selected = targetLocation == loc,
                            onClick = { targetLocation = loc },
                            label = { Text("${loc.emoji} ${loc.title}") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Qty") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unitText,
                        onValueChange = { unitText = it },
                        label = { Text("Unit") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Est. ($currencySymbol)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(
                                    name.trim(),
                                    selectedCategory.title,
                                    targetLocation.title,
                                    quantityText.toDoubleOrNull() ?: 1.0,
                                    unitText.trim().ifBlank { "pcs" },
                                    priceText.toDoubleOrNull() ?: 3.00,
                                    notesText.trim()
                                )
                            }
                        },
                        enabled = name.isNotBlank(),
                        modifier = Modifier.testTag("save_shopping_item_button")
                    ) {
                        Text("Add to List")
                    }
                }
            }
        }
    }
}
