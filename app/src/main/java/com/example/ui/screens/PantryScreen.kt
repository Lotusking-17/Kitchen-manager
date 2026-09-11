package com.example.ui.screens

import androidx.compose.animation.*
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
import com.example.data.local.PantryItemEntity
import com.example.data.model.ExpiryStatus
import com.example.data.model.FoodCategory
import com.example.data.model.StorageLocation
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val pantryItems by viewModel.filteredPantryItems.collectAsState()
    val allActiveItems by viewModel.activePantryItems.collectAsState()
    val urgentAlerts by viewModel.urgentPantryAlerts.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val selectedFreshness by viewModel.selectedFreshnessFilter.collectAsState()
    val searchQuery by viewModel.pantrySearchQuery.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openAddPantryDialog() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Item") },
                text = { Text("Add Item", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("fab_add_pantry_item")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            // Urgent Expiry Banner (KitchenPal signature warning)
            if (urgentAlerts.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("urgent_expiry_banner"),
                        colors = CardDefaults.cardColors(
                            containerColor = AmberContainerLight.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ExpiringWarning.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Timer,
                                    contentDescription = "Urgent",
                                    tint = AmberSecondaryLight
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${urgentAlerts.size} items expiring soon!",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = OnAmberContainerLight
                                )
                                Text(
                                    text = urgentAlerts.take(3).joinToString(", ") { it.name },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnAmberContainerLight.copy(alpha = 0.85f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            TextButton(
                                onClick = { viewModel.setFreshnessFilter("Expiring Soon") }
                            ) {
                                Text("View", fontWeight = FontWeight.Bold, color = AmberSecondaryLight)
                            }
                        }
                    }
                }
            }

            // Location Tabs (Fridge, Pantry, Freezer, Spices)
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Text(
                        text = "STORAGE LOCATIONS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            val isAllSelected = selectedLocation == "All"
                            FilterChip(
                                selected = isAllSelected,
                                onClick = { viewModel.setLocationFilter("All") },
                                label = { Text("All (${allActiveItems.size})") },
                                leadingIcon = {
                                    Icon(
                                        if (isAllSelected) Icons.Default.Kitchen else Icons.Outlined.Kitchen,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                modifier = Modifier.testTag("location_filter_all")
                            )
                        }
                        items(StorageLocation.entries.toList()) { loc ->
                            val isSelected = selectedLocation.equals(loc.title, ignoreCase = true)
                            val count = allActiveItems.count { it.location.equals(loc.title, ignoreCase = true) }
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setLocationFilter(loc.title) },
                                label = { Text("${loc.emoji} ${loc.title} ($count)") },
                                modifier = Modifier.testTag("location_filter_${loc.name.lowercase()}")
                            )
                        }
                    }
                }
            }

            // Search Bar & Freshness Filter Chips
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setPantrySearchQuery(it) },
                        placeholder = { Text("Search your ingredients & staples...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setPantrySearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pantry_search_bar")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Freshness Status chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "All" to "All",
                            "Expiring Soon" to "⚠️ Expiring",
                            "Fresh" to "✅ Fresh",
                            "Expired" to "❌ Expired"
                        ).forEach { (filterKey, label) ->
                            val isSelected = selectedFreshness == filterKey
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setFreshnessFilter(filterKey) },
                                label = { Text(label, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }

            // Section Header with count
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedLocation == "All") "All Inventory" else selectedLocation,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${pantryItems.size} items",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Items List or Empty State
            if (pantryItems.isEmpty()) {
                item {
                    EmptyPantryView(
                        location = selectedLocation,
                        onAddClick = { viewModel.openAddPantryDialog() }
                    )
                }
            } else {
                items(pantryItems, key = { it.id }) { item ->
                    PantryItemCard(
                        item = item,
                        onConsumed = { viewModel.markConsumed(item.id, item.name) },
                        onWasted = { viewModel.markWasted(item.id, item.name) },
                        onRestock = { viewModel.restockItemToShopping(item) },
                        onEdit = { viewModel.openEditPantryDialog(item) },
                        onDelete = { viewModel.deletePantryItem(item.id, item.name) },
                        onQuantityAdjust = { delta -> viewModel.adjustPantryQuantity(item, delta) }
                    )
                }
            }
        }
    }
}

@Composable
fun PantryItemCard(
    item: PantryItemEntity,
    onConsumed: () -> Unit,
    onWasted: () -> Unit,
    onRestock: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onQuantityAdjust: (Double) -> Unit
) {
    val (status, daysLeft) = item.expiryStatusInfo
    var showMenu by remember { mutableStateOf(false) }

    val statusBgColor = when (status) {
        ExpiryStatus.EXPIRED -> ExpiredRed.copy(alpha = 0.12f)
        ExpiryStatus.EXPIRING_SOON -> ExpiringWarning.copy(alpha = 0.14f)
        ExpiryStatus.FRESH -> FreshGreen.copy(alpha = 0.12f)
    }

    val statusTextColor = when (status) {
        ExpiryStatus.EXPIRED -> ExpiredRed
        ExpiryStatus.EXPIRING_SOON -> AmberSecondaryLight
        ExpiryStatus.FRESH -> EmeraldPrimaryLight
    }

    val statusLabel = when (status) {
        ExpiryStatus.EXPIRED -> "Expired ${-daysLeft}d ago"
        ExpiryStatus.EXPIRING_SOON -> if (daysLeft == 0) "Expires Today!" else if (daysLeft == 1) "Expires Tomorrow" else "Expires in $daysLeft days"
        ExpiryStatus.FRESH -> "Fresh • $daysLeft days left"
    }

    val category = FoodCategory.fromString(item.category)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .testTag("pantry_card_${item.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Food emoji badge
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(statusBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = category.emoji, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name, location, and status
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Location Pill
                        Text(
                            text = item.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        // Freshness Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(statusBgColor)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = statusLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = statusTextColor
                            )
                        }
                    }
                }

                // More Menu Button
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.testTag("item_more_menu_${item.id}")
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Restock to Grocery List") },
                            leadingIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onRestock()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Edit Item") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mark as Wasted") },
                            leadingIcon = { Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = ExpiredRed) },
                            onClick = {
                                showMenu = false
                                onWasted()
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom row: Quantity stepper + One-tap "Ate / Cooked it" celebration button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity Stepper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = { onQuantityAdjust(-1.0) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                    }
                    Text(
                        text = "${if (item.quantity % 1.0 == 0.0) item.quantity.toInt() else item.quantity} ${item.unit}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    IconButton(
                        onClick = { onQuantityAdjust(1.0) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                    }
                }

                // Quick "Cooked / Consumed" button (Food waste saver!)
                OutlinedButton(
                    onClick = onConsumed,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = EmeraldPrimaryLight
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_consumed_${item.id}")
                ) {
                    Icon(
                        Icons.Default.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cooked / Eaten", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun EmptyPantryView(
    location: String,
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Kitchen,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (location == "All") "Your pantry is empty!" else "No items in $location",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Track your food freshness, prevent expiration waste, and find delicious recipes you can cook right away.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(18.dp))
        Button(
            onClick = onAddClick,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Add Your First Item")
        }
    }
}
