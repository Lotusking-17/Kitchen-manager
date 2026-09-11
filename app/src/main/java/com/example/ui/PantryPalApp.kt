package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryPalApp(
    viewModel: MainViewModel
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val shoppingItems by viewModel.shoppingItems.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    val snackMessage by viewModel.snackMessage.collectAsState()

    val isAddPantryOpen by viewModel.isAddPantryDialogOpen.collectAsState()
    val editingPantryItem by viewModel.editingPantryItem.collectAsState()
    val isAccountSheetOpen by viewModel.isAccountSheetOpen.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackMessage) {
        snackMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnack()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Kitchen,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "PantryPal",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                actions = {
                    // Live Cloud Sync pill indicator button
                    Surface(
                        onClick = { viewModel.openAccountSheet() },
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("top_bar_sync_pill")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (syncStatus.isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (syncStatus.isOnline) FreshGreen else Color.Gray)
                                )
                            }
                            Text(
                                text = if (syncStatus.isSyncing) "Syncing" else if (syncStatus.isOnline) "Cloud Online" else "Offline",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (syncStatus.isOnline) EmeraldPrimaryLight else Color.Gray
                            )
                        }
                    }

                    // Profile / Account Avatar
                    IconButton(
                        onClick = { viewModel.openAccountSheet() },
                        modifier = Modifier.testTag("btn_top_account")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.displayName.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                // 1. Pantry Tab
                NavigationBarItem(
                    selected = currentTab == AppNavTab.PANTRY,
                    onClick = { viewModel.setTab(AppNavTab.PANTRY) },
                    icon = {
                        Icon(
                            if (currentTab == AppNavTab.PANTRY) Icons.Default.Kitchen else Icons.Outlined.Kitchen,
                            contentDescription = "Pantry"
                        )
                    },
                    label = { Text("Pantry") },
                    modifier = Modifier.testTag("nav_tab_pantry")
                )

                // 2. Recipes Tab
                NavigationBarItem(
                    selected = currentTab == AppNavTab.RECIPES,
                    onClick = { viewModel.setTab(AppNavTab.RECIPES) },
                    icon = {
                        Icon(
                            if (currentTab == AppNavTab.RECIPES) Icons.Default.MenuBook else Icons.Outlined.MenuBook,
                            contentDescription = "Recipes"
                        )
                    },
                    label = { Text("Recipes") },
                    modifier = Modifier.testTag("nav_tab_recipes")
                )

                // 3. Shopping Tab
                val unboughtCount = shoppingItems.count { !it.isChecked }
                NavigationBarItem(
                    selected = currentTab == AppNavTab.SHOPPING,
                    onClick = { viewModel.setTab(AppNavTab.SHOPPING) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (unboughtCount > 0) {
                                    Badge { Text("$unboughtCount") }
                                }
                            }
                        ) {
                            Icon(
                                if (currentTab == AppNavTab.SHOPPING) Icons.Default.ShoppingCart else Icons.Outlined.ShoppingCart,
                                contentDescription = "Shopping"
                            )
                        }
                    },
                    label = { Text("Shopping") },
                    modifier = Modifier.testTag("nav_tab_shopping")
                )

                // 4. Meal Planner Tab
                NavigationBarItem(
                    selected = currentTab == AppNavTab.PLANNER,
                    onClick = { viewModel.setTab(AppNavTab.PLANNER) },
                    icon = {
                        Icon(
                            if (currentTab == AppNavTab.PLANNER) Icons.Default.CalendarMonth else Icons.Outlined.CalendarMonth,
                            contentDescription = "Planner"
                        )
                    },
                    label = { Text("Planner") },
                    modifier = Modifier.testTag("nav_tab_planner")
                )

                // 5. Eco & Waste Stats Tab
                NavigationBarItem(
                    selected = currentTab == AppNavTab.ANALYTICS,
                    onClick = { viewModel.setTab(AppNavTab.ANALYTICS) },
                    icon = {
                        Icon(
                            if (currentTab == AppNavTab.ANALYTICS) Icons.Default.Eco else Icons.Outlined.Eco,
                            contentDescription = "Eco & Stats"
                        )
                    },
                    label = { Text("Eco Stats") },
                    modifier = Modifier.testTag("nav_tab_analytics")
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppNavTab.PANTRY -> PantryScreen(viewModel = viewModel)
                AppNavTab.RECIPES -> RecipesScreen(viewModel = viewModel)
                AppNavTab.SHOPPING -> ShoppingScreen(viewModel = viewModel)
                AppNavTab.PLANNER -> MealPlannerScreen(viewModel = viewModel)
                AppNavTab.ANALYTICS -> WasteTrackerScreen(viewModel = viewModel)
            }
        }
    }

    // Add / Edit Pantry Dialog
    if (isAddPantryOpen) {
        AddEditPantryItemDialog(
            itemToEdit = editingPantryItem,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = { viewModel.closeAddPantryDialog() },
            onSave = { id, name, cat, loc, qty, unit, exp, notes, price ->
                viewModel.savePantryItem(id, name, cat, loc, qty, unit, exp, notes, price)
            }
        )
    }

    // Account & Cloud Sync Sheet
    if (isAccountSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.closeAccountSheet() },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            AccountScreen(viewModel = viewModel)
        }
    }
}
