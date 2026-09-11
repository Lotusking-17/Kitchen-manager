package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.data.model.AVAILABLE_CURRENCIES
import com.example.ui.MainViewModel
import com.example.ui.theme.AmberSecondaryLight
import com.example.ui.theme.EmeraldContainerLight
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.FreshGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()

    var showLoginForm by remember { mutableStateOf(!userProfile.isLoggedIn) }
    var emailInput by remember { mutableStateOf(userProfile.email) }
    var passwordInput by remember { mutableStateOf("••••••••") }
    var nameInput by remember { mutableStateOf(userProfile.displayName) }

    var householdNameInput by remember { mutableStateOf(userProfile.householdName) }
    var householdCodeInput by remember { mutableStateOf(userProfile.householdCode) }

    val formattedSyncTime = remember(userProfile.lastSyncTime) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        sdf.format(Date(userProfile.lastSyncTime))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp)
    ) {
        // User Profile & Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("account_profile_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (userProfile.displayName.isNotEmpty()) userProfile.displayName.take(1).uppercase() else "P",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userProfile.displayName.ifBlank { "Guest User" },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = userProfile.email.ifBlank { "Not signed in" },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Online / Offline Status Badge
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (syncStatus.isOnline) FreshGreen else Color.Gray)
                                )
                                Text(
                                    text = if (syncStatus.isOnline) "Cloud Online Sync Active" else "Offline Mode",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (syncStatus.isOnline) EmeraldPrimaryLight else Color.Gray,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        IconButton(onClick = { showLoginForm = !showLoginForm }) {
                            Icon(
                                if (userProfile.isLoggedIn) Icons.Default.ManageAccounts else Icons.Default.Login,
                                contentDescription = "Manage Login",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Cloud Sync Controls Row
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Live Cloud Sync",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Last synced: $formattedSyncTime",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Switch(
                                checked = syncStatus.isOnline,
                                onCheckedChange = { viewModel.toggleOnline(it) },
                                modifier = Modifier.testTag("switch_online_sync")
                            )

                            IconButton(
                                onClick = { viewModel.triggerManualSync() },
                                enabled = syncStatus.isOnline && !syncStatus.isSyncing,
                                modifier = Modifier.testTag("btn_manual_sync")
                            ) {
                                if (syncStatus.isSyncing) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.Sync, contentDescription = "Sync now", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Login / Switch Account Expandable Card
        if (showLoginForm || !userProfile.isLoggedIn) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("account_login_form"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = if (userProfile.isLoggedIn) "Account Details" else "Sign In to Sync Kitchen Across Devices",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sync pantry inventory, shopping lists, and meal plans securely online.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Display Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Email Address") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (emailInput.isNotBlank()) {
                                        viewModel.login(emailInput.trim(), nameInput.trim())
                                        showLoginForm = false
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_confirm_login")
                            ) {
                                Text("Sign In & Sync")
                            }

                            if (userProfile.isLoggedIn) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.logout()
                                        showLoginForm = true
                                    },
                                    modifier = Modifier.weight(0.7f)
                                ) {
                                    Text("Sign Out")
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        // Shared Household Pantry Section (KitchenPal signature family sync)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AmberSecondaryLight.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.People, contentDescription = null, tint = AmberSecondaryLight)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Shared Household Pantry", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Share fridge & pantry lists with family or roommates", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = householdNameInput,
                        onValueChange = { householdNameInput = it },
                        label = { Text("Kitchen / Household Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = householdCodeInput,
                            onValueChange = { householdCodeInput = it },
                            label = { Text("Family Join Code") },
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                viewModel.updateHousehold(householdNameInput, householdCodeInput)
                            }
                        ) {
                            Text("Update")
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Dietary Preferences & Filters
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Dietary Preferences & Recipe Filters",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Smart recipes will automatically adapt to your eating habits.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val options = listOf(
                        "Vegetarian" to "🥬",
                        "Vegan" to "🌱",
                        "Gluten-Free" to "🌾",
                        "High-Protein" to "💪",
                        "Low-Carb" to "🥑",
                        "Dairy-Free" to "🥛",
                        "Quick & Easy" to "⚡"
                    )

                    options.chunked(2).forEach { rowOptions ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowOptions.forEach { (diet, emoji) ->
                                val isSelected = userProfile.dietaryPreferences.contains(diet)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.toggleDietaryPreference(diet) },
                                    label = { Text("$emoji $diet") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowOptions.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Currency & Pricing Format Section
        item {
            var customCode by remember { mutableStateOf("") }
            var customSymbol by remember { mutableStateOf("") }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("currency_settings_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Paid,
                                contentDescription = "Currency",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Currency & Pricing Format",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Active: ${userProfile.currencyCode} (${userProfile.currencySymbol})",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Choose your preferred currency for grocery costs, budget tracking, and food waste savings:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Currency Option Chips
                    AVAILABLE_CURRENCIES.chunked(3).forEach { rowCurrencies ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowCurrencies.forEach { curr ->
                                val isSelected = userProfile.currencyCode == curr.code && userProfile.currencySymbol == curr.symbol
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.updateCurrency(curr.code, curr.symbol) },
                                    label = {
                                        Text(
                                            text = "${curr.flag} ${curr.code} (${curr.symbol})",
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("currency_chip_${curr.code.lowercase()}")
                                )
                            }
                            if (rowCurrencies.size < 3) {
                                repeat(3 - rowCurrencies.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Custom Currency",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customCode,
                            onValueChange = { customCode = it.uppercase() },
                            label = { Text("Code (e.g. SEK)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = customSymbol,
                            onValueChange = { customSymbol = it },
                            label = { Text("Symbol (e.g. kr)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                if (customCode.isNotBlank() && customSymbol.isNotBlank()) {
                                    viewModel.updateCurrency(customCode.trim(), customSymbol.trim())
                                    customCode = ""
                                    customSymbol = ""
                                }
                            },
                            enabled = customCode.isNotBlank() && customSymbol.isNotBlank()
                        ) {
                            Text("Set")
                        }
                    }
                }
            }
        }
    }
}
