package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ShoppingItemEntity
import com.example.data.model.FoodCategory
import com.example.data.model.StorageLocation
import com.example.ui.MainViewModel
import com.example.ui.theme.AmberSecondaryLight
import com.example.ui.theme.EmeraldContainerLight
import com.example.ui.theme.EmeraldPrimaryLight
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val shoppingItems by viewModel.shoppingItems.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val isAddDialogOpen by viewModel.isAddShoppingDialogOpen.collectAsState()

    val checkedCount = shoppingItems.count { it.isChecked }
    val totalCost = shoppingItems.filter { !it.isChecked }.sumOf { it.estimatedPrice * it.quantity }
    val totalEstimated = shoppingItems.sumOf { it.estimatedPrice * it.quantity }
    val currencySymbol = userProfile.currencySymbol

    // Group items by category (supermarket aisles)
    val groupedItems = shoppingItems.groupBy { it.category }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openAddShoppingDialog() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Default.AddShoppingCart, contentDescription = "Add Item") },
                text = { Text("Add Grocery", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("fab_add_shopping_item")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            // Summary Header Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("shopping_summary_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
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
                                    text = "Smart Grocery Trip",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${shoppingItems.size} items • $checkedCount purchased",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = String.format(Locale.US, "%s%.2f", currencySymbol, totalCost),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "estimated cart",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Transfer checked items into Pantry! (KitchenPal signature workflow)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.transferPurchasedToPantry() },
                                enabled = checkedCount > 0,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("btn_transfer_to_pantry")
                            ) {
                                Icon(Icons.Default.MoveToInbox, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Transfer to Pantry ($checkedCount)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (checkedCount > 0) {
                                OutlinedButton(
                                    onClick = { viewModel.clearCheckedShopping() },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(0.7f)
                                ) {
                                    Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Clear", fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Aisle / Category Sections
            if (shoppingItems.isEmpty()) {
                item {
                    EmptyShoppingView(onAddClick = { viewModel.openAddShoppingDialog() })
                }
            } else {
                groupedItems.forEach { (categoryName, itemsInCategory) ->
                    val category = FoodCategory.fromString(categoryName)
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${category.emoji} ${category.title}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "(${itemsInCategory.size})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    items(itemsInCategory, key = { it.id }) { item ->
                        ShoppingItemRow(
                            item = item,
                            currencySymbol = currencySymbol,
                            onToggleChecked = { isChecked -> viewModel.toggleShoppingChecked(item.id, isChecked) },
                            onDelete = { viewModel.deleteShoppingItem(item.id) }
                        )
                    }
                }
            }
        }
    }

    if (isAddDialogOpen) {
        AddShoppingItemDialog(
            currencySymbol = currencySymbol,
            onDismiss = { viewModel.closeAddShoppingDialog() },
            onSave = { name, cat, loc, qty, unit, price, notes ->
                viewModel.addShoppingItem(name, cat, loc, qty, unit, price, notes)
            }
        )
    }
}

@Composable
fun ShoppingItemRow(
    item: ShoppingItemEntity,
    currencySymbol: String = "$",
    onToggleChecked: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val targetLoc = StorageLocation.fromString(item.targetLocation)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onToggleChecked(!item.isChecked) }
            .testTag("shopping_row_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isChecked) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.isChecked) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = { onToggleChecked(it) },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("checkbox_${item.id}")
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (item.isChecked) FontWeight.Normal else FontWeight.SemiBold,
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (item.isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${if (item.quantity % 1.0 == 0.0) item.quantity.toInt() else item.quantity} ${item.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(text = "•", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    // Restocks to badge
                    Text(
                        text = "Goes to ${targetLoc.emoji} ${targetLoc.title}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (!item.addedFromRecipe.isNullOrBlank()) {
                        Text(text = "•", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        Text(
                            text = "For ${item.addedFromRecipe}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberSecondaryLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Text(
                text = String.format(Locale.US, "%s%.2f", currencySymbol, item.estimatedPrice * item.quantity),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (item.isChecked) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
            )

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun EmptyShoppingView(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Shopping list is empty!",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Add grocery items or restock directly from recipes and pantry alerts with 1 tap.",
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
            Text("Add Grocery Item")
        }
    }
}
