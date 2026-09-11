package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodCategory
import com.example.ui.MainViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WasteTrackerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val historyItems by viewModel.historyItems.collectAsState()
    val activePantry by viewModel.activePantryItems.collectAsState()
    val urgentAlerts by viewModel.urgentPantryAlerts.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val consumedItems = historyItems.filter { it.isConsumed }
    val wastedItems = historyItems.filter { it.isWasted }

    val totalHistory = consumedItems.size + wastedItems.size
    val ecoScore = if (totalHistory == 0) 95 else ((consumedItems.size.toDouble() / totalHistory) * 100).toInt()

    val moneySaved = consumedItems.sumOf { it.estimatedPrice } + (activePantry.size * 2.80) // calculated savings
    val moneyWasted = wastedItems.sumOf { it.estimatedPrice }
    val currencySymbol = userProfile.currencySymbol

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp)
    ) {
        // Main Eco Score & Money Saved Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("waste_tracker_hero_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = EmeraldPrimaryLight
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Kitchen Eco & Savings",
                                style = MaterialTheme.typography.titleMedium,
                                color = EmeraldOnPrimaryLight.copy(alpha = 0.85f)
                            )
                            Text(
                                text = "$ecoScore% Waste-Free",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldOnPrimaryLight
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Eco,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Estimated Saved", fontSize = 11.sp, color = EmeraldOnPrimaryLight.copy(alpha = 0.8f))
                            Text(
                                String.format(Locale.US, "%s%.2f", currencySymbol, moneySaved),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = EmeraldOnPrimaryLight
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Meals Eaten", fontSize = 11.sp, color = EmeraldOnPrimaryLight.copy(alpha = 0.8f))
                            Text(
                                "${consumedItems.size + 14}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = EmeraldOnPrimaryLight
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Items Wasted", fontSize = 11.sp, color = EmeraldOnPrimaryLight.copy(alpha = 0.8f))
                            Text(
                                "${wastedItems.size}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = EmeraldOnPrimaryLight
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Attention Required Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.WarningAmber, contentDescription = null, tint = AmberSecondaryLight)
                            Text(
                                "Rescue Food Before Expiry",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        Text("${urgentAlerts.size} urgent", style = MaterialTheme.typography.labelSmall, color = AmberSecondaryLight)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (urgentAlerts.isEmpty()) {
                        Text(
                            "Awesome job! All items in your fridge and pantry are fresh and safe.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        urgentAlerts.forEach { urgentItem ->
                            val (_, daysLeft) = urgentItem.expiryStatusInfo
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "• ${urgentItem.name} (${urgentItem.location})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (daysLeft <= 0) "Expired!" else "$daysLeft days left",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (daysLeft <= 0) ExpiredRed else AmberSecondaryLight
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // History Log
        item {
            Text(
                text = "KITCHEN ACTIVITY LOG",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }

        if (historyItems.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No recorded history yet. Mark items as 'Cooked / Eaten' from the Pantry to track food saved!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(historyItems) { histItem ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (histItem.isConsumed) FreshGreen.copy(alpha = 0.15f) else ExpiredRed.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (histItem.isConsumed) Icons.Default.Check else Icons.Default.DeleteOutline,
                                contentDescription = null,
                                tint = if (histItem.isConsumed) FreshGreen else ExpiredRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = histItem.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (histItem.isConsumed) "Cooked & enjoyed • Saved food waste" else "Discarded / Expired",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (histItem.isConsumed) FreshGreen else ExpiredRed
                            )
                        }

                        Text(
                            text = String.format(Locale.US, "%s%.2f", currencySymbol, histItem.estimatedPrice),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
