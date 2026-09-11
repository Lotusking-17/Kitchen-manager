package com.example.data.model

data class CurrencyOption(
    val code: String,
    val symbol: String,
    val name: String,
    val flag: String
)

val AVAILABLE_CURRENCIES = listOf(
    CurrencyOption("USD", "$", "US Dollar", "🇺🇸"),
    CurrencyOption("EUR", "€", "Euro", "🇪🇺"),
    CurrencyOption("GBP", "£", "British Pound", "🇬🇧"),
    CurrencyOption("CAD", "C$", "Canadian Dollar", "🇨🇦"),
    CurrencyOption("AUD", "A$", "Australian Dollar", "🇦🇺"),
    CurrencyOption("JPY", "¥", "Japanese Yen", "🇯🇵"),
    CurrencyOption("CNY", "¥", "Chinese Yuan", "🇨🇳"),
    CurrencyOption("INR", "₹", "Indian Rupee", "🇮🇳"),
    CurrencyOption("SGD", "S$", "Singapore Dollar", "🇸🇬"),
    CurrencyOption("HKD", "HK$", "Hong Kong Dollar", "🇭🇰"),
    CurrencyOption("TWD", "NT$", "New Taiwan Dollar", "🇹🇼"),
    CurrencyOption("KRW", "₩", "South Korean Won", "🇰🇷"),
    CurrencyOption("CHF", "CHF", "Swiss Franc", "🇨🇭"),
    CurrencyOption("MXN", "Mex$", "Mexican Peso", "🇲🇽"),
    CurrencyOption("BRL", "R$", "Brazilian Real", "🇧🇷")
)

data class UserProfile(
    val email: String = "lotuswang17@gmail.com",
    val displayName: String = "Lotus Wang",
    val isLoggedIn: Boolean = true,
    val householdCode: String = "KP-7892",
    val householdName: String = "Wang Family Pantry",
    val isCloudSyncEnabled: Boolean = true,
    val lastSyncTime: Long = System.currentTimeMillis(),
    val dietaryPreferences: Set<String> = setOf("High-Protein", "Quick & Easy"),
    val allergies: Set<String> = emptySet(),
    val monthlyBudget: Double = 350.0,
    val currencyCode: String = "USD",
    val currencySymbol: String = "$"
)

data class SyncStatus(
    val isOnline: Boolean = true,
    val isSyncing: Boolean = false,
    val lastSyncMessage: String = "All changes saved to cloud"
)
