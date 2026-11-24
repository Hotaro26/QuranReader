@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
package com.hotaro.mizan.ui.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.hotaro.mizan.LocalFloatingNavBarPadding
import com.hotaro.mizan.R
import com.hotaro.mizan.data.model.Edition
import com.hotaro.mizan.ui.components.RoundedPullRefreshPage
import com.hotaro.mizan.ui.viewmodel.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// AttendX-inspired card corner grouping shapes
object SettingsGroupShape {
    val SINGLE = RoundedCornerShape(24.dp)
    val FIRST = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 8.dp, bottomEnd = 8.dp)
    val MIDDLE = RoundedCornerShape(8.dp)
    val LAST = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 24.dp, bottomEnd = 24.dp)

    fun getShape(index: Int, total: Int): RoundedCornerShape {
        return when {
            total <= 1 -> SINGLE
            index == 0 -> FIRST
            index == total - 1 -> LAST
            else -> MIDDLE
        }
    }
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onElementsAndFeelClick: () -> Unit = {},
    onAboutClick: () -> Unit = {}
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val colorPalette by viewModel.colorPalette.collectAsState()
    val use24HourFormat by viewModel.use24HourFormat.collectAsState()
    val selectedTranslation by viewModel.selectedTranslation.collectAsState()
    val appFont by viewModel.appFont.collectAsState()
    val prayerCalculationMethod by viewModel.prayerCalculationMethod.collectAsState()
    val manualFajr by viewModel.manualFajr.collectAsState()
    val manualDhuhr by viewModel.manualDhuhr.collectAsState()
    val manualAsr by viewModel.manualAsr.collectAsState()
    val manualMaghrib by viewModel.manualMaghrib.collectAsState()
    val manualIsha by viewModel.manualIsha.collectAsState()
    val selectedAppIcon by viewModel.selectedAppIcon.collectAsState()
    val editions by viewModel.filteredEditions.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val navBarPadding = LocalFloatingNavBarPadding.current

    var showTranslationSheet by remember { mutableStateOf(false) }
    var showCalculationSheet by remember { mutableStateOf(false) }
    var showManualTimesSheet by remember { mutableStateOf(false) }
    var showAppIconSheet by remember { mutableStateOf(false) }
    var showSupportSheet by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val myUpiId = "sakibreza035@okaxis"

    val calculationMethods = remember {
        listOf(
            "Manual" to -1,
            "Shia Ithna-Ashari" to 0,
            "University of Islamic Sciences, Karachi" to 1,
            "Islamic Society of North America" to 2,
            "Muslim World League" to 3,
            "Umm Al-Qura University, Makkah" to 4,
            "Egyptian General Authority of Survey" to 5
        )
    }

    val currentMethodLabel = calculationMethods.find { it.second == prayerCalculationMethod }?.first
        ?: "Islamic Society of North America"

    RoundedPullRefreshPage(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Settings", 
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = navBarPadding + 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Support Hero Card
                item {
                    ExpressiveSupportCard(
                        onSupportClick = { showSupportSheet = true }
                    )
                }

                // Section: Appearance
                item {
                    SettingsSectionHeader("Appearance")
                }
                item {
                    val themes = listOf("System" to 0, "Light" to 1, "Dark" to 2)
                    ExpressiveDropdownSettingItem(
                        icon = Icons.Default.Visibility,
                        title = "Theme Mode",
                        subtitle = "Select app appearance",
                        currentValue = themes.find { it.second == themeMode }?.first ?: "System",
                        options = themes,
                        shape = SettingsGroupShape.getShape(0, 6),
                        onSelected = { viewModel.setThemeMode(it) }
                    )
                }
                item {
                    val palettes = listOf(
                        "Material You" to "dynamic",
                        "Classic Green" to "classic",
                        "Lavender" to "lavender",
                        "Pink" to "pink",
                        "Mocha" to "mocha",
                        "Catppuccin" to "catppuccin",
                        "Monochrome" to "monochrome"
                    )
                    ExpressiveDropdownSettingItem(
                        icon = Icons.Default.ColorLens,
                        title = "Color Palette",
                        subtitle = "Personalize colors across the UI",
                        currentValue = palettes.find { it.second == colorPalette }?.first ?: "Material You",
                        options = palettes,
                        shape = SettingsGroupShape.getShape(1, 6),
                        onSelected = { viewModel.setColorPalette(it) }
                    )
                }
                item {
                    val appIcons = listOf(
                        "emerald" to "Emerald Green",
                        "midnight" to "Midnight Blue",
                        "purple" to "Royal Purple"
                    )
                    val currentIconName = appIcons.find { it.first == selectedAppIcon }?.second ?: "Emerald Green"
                    ExpressiveActionSettingItem(
                        icon = Icons.Default.AppShortcut,
                        title = "App Icon",
                        subtitle = currentIconName,
                        shape = SettingsGroupShape.getShape(2, 6),
                        onClick = { showAppIconSheet = true }
                    )
                }
                item {
                    val fonts = listOf(
                        "Default" to "default",
                        "Hey Comic" to "hey_comic",
                        "KG Happy" to "kghappy",
                        "Matcha Cih" to "matcha_cih",
                        "Nirakolu" to "nirakolu",
                        "Romantic Sunrise" to "romantic_sunrise",
                        "Takeover" to "takeover",
                        "Takeover 3D" to "takeover_3d",
                        "Scratch Boys" to "scratch_boys"
                    )
                    ExpressiveDropdownSettingItem(
                        icon = Icons.Default.TextFields,
                        title = "App Typography",
                        subtitle = "Choose reading & UI font",
                        currentValue = fonts.find { it.second == appFont }?.first ?: "Default",
                        options = fonts,
                        shape = SettingsGroupShape.getShape(3, 6),
                        onSelected = { viewModel.setAppFont(it) }
                    )
                }
                item {
                    ExpressiveDropdownSettingItem(
                        icon = Icons.Default.Schedule,
                        title = "Clock Format",
                        subtitle = "Display style for prayer times",
                        currentValue = if (use24HourFormat) "24-hour" else "12-hour",
                        options = listOf("12-hour" to false, "24-hour" to true),
                        shape = SettingsGroupShape.getShape(4, 6),
                        onSelected = { viewModel.setUse24HourFormat(it) }
                    )
                }
                item {
                    ExpressiveActionSettingItem(
                        icon = Icons.Default.Tune,
                        title = "Elements & Feel",
                        subtitle = "Customize homepage pills, layout & behavior",
                        shape = SettingsGroupShape.getShape(5, 6),
                        onClick = onElementsAndFeelClick
                    )
                }

                // Section: Reading & Prayers (Conjoined)
                item {
                    Spacer(Modifier.height(8.dp))
                    SettingsSectionHeader("Reading & Prayer Times")
                }
                item {
                    ExpressiveActionSettingItem(
                        icon = Icons.Default.Translate,
                        title = "Translation Edition",
                        subtitle = selectedTranslation,
                        shape = SettingsGroupShape.getShape(0, 2),
                        onClick = { showTranslationSheet = true }
                    )
                }
                item {
                    ExpressiveActionSettingItem(
                        icon = Icons.Default.Mosque,
                        title = "Calculation Method",
                        subtitle = if (prayerCalculationMethod == -1) "Manual • Tap to adjust times" else currentMethodLabel,
                        shape = SettingsGroupShape.getShape(1, 2),
                        onClick = { showCalculationSheet = true }
                    )
                }

                // Section: About
                item {
                    Spacer(Modifier.height(8.dp))
                    SettingsSectionHeader("About & Developer")
                }
                item {
                    ExpressiveAboutCard(onClick = onAboutClick)
                }
            }
        }
    }

    // Modal Bottom Sheet for Translation Edition Selection
    if (showTranslationSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showTranslationSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Select Translation",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search translations or authors...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(editions) { edition ->
                        val isSelected = edition.identifier == selectedTranslation
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectTranslation(edition)
                                    showTranslationSheet = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = edition.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                    Text(
                                        text = "${edition.language} • ${edition.author}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet for Prayer Calculation Method Selection
    if (showCalculationSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showCalculationSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Calculation Method",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(calculationMethods) { (label, value) ->
                        val isSelected = value == prayerCalculationMethod
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setPrayerCalculationMethod(value)
                                    showCalculationSheet = false
                                    if (value == -1) {
                                        showManualTimesSheet = true
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                    if (value == -1) {
                                        Text(
                                            text = "Manually set Fajr, Dhuhr, Asr, Maghrib, and Isha times",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                } else if (value == -1) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet for Manual Prayer Times Configuration
    if (showManualTimesSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var editingPrayer by remember { mutableStateOf<Pair<String, String>?>(null) }

        ModalBottomSheet(
            onDismissRequest = { showManualTimesSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Manual Prayer Times",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Tap on any prayer to adjust its exact timing",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val prayers = listOf(
                    Triple("Fajr", manualFajr, Icons.Default.WbTwilight),
                    Triple("Dhuhr", manualDhuhr, Icons.Default.WbSunny),
                    Triple("Asr", manualAsr, Icons.Default.LightMode),
                    Triple("Maghrib", manualMaghrib, Icons.Default.NightsStay),
                    Triple("Isha", manualIsha, Icons.Default.DarkMode)
                )

                prayers.forEach { (name, time, icon) ->
                    val displayTime = formatDisplayPrayerTime(time, use24HourFormat)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                editingPrayer = name to time
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(Modifier.width(14.dp))
                            Text(
                                text = name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = displayTime,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit $name",
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { showManualTimesSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Save & Apply", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(20.dp))
            }
        }

        // Time Picker Dialog for setting prayer time
        if (editingPrayer != null) {
            val (currentPrayerName, currentTimeString) = editingPrayer!!
            val parts = currentTimeString.split(":")
            val initialH = parts.getOrNull(0)?.toIntOrNull() ?: 5
            val initialM = parts.getOrNull(1)?.toIntOrNull() ?: 0

            val timePickerState = rememberTimePickerState(
                initialHour = initialH,
                initialMinute = initialM,
                is24Hour = use24HourFormat
            )

            TimePickerDialog(
                title = "Set $currentPrayerName Time",
                onDismissRequest = { editingPrayer = null },
                confirmButton = {
                    TextButton(onClick = {
                        val formattedTime = String.format(Locale.US, "%02d:%02d", timePickerState.hour, timePickerState.minute)
                        viewModel.saveManualPrayerTime(currentPrayerName, formattedTime)
                        editingPrayer = null
                    }) {
                        Text("Confirm", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingPrayer = null }) {
                        Text("Cancel")
                    }
                }
            ) {
                TimePicker(state = timePickerState)
            }
        }
    }

    if (showSupportSheet) {
        SupportBottomSheet(
            onDismissRequest = { showSupportSheet = false },
            upiId = myUpiId
        )
    }

    if (showSupportDialog) {
        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            confirmButton = {
                TextButton(onClick = { showSupportDialog = false }) {
                    Text("Got it")
                }
            },
            icon = { Icon(Icons.Default.Favorite, null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Support Development", textAlign = TextAlign.Center) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Thank you for considering support! If your UPI app didn't open automatically, you can send to this VPA:",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(16.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = myUpiId,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        )
    }

    if (showAppIconSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showAppIconSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "App Icon",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Select your home screen launcher style",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val iconOptions = listOf(
                    Triple("emerald", "Emerald Green", Pair("Classic Islamic Mizan", R.drawable.preview_icon_emerald)),
                    Triple("midnight", "Midnight Blue", Pair("Deep Celestial Night", R.drawable.preview_icon_midnight)),
                    Triple("purple", "Royal Purple", Pair("Twilight Amethyst", R.drawable.preview_icon_purple))
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(iconOptions) { (key, title, details) ->
                        val (desc, drawableRes) = details
                        val isSelected = key == selectedAppIcon
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setAppIcon(context, key)
                                    showAppIconSheet = false
                                },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer
                            ),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(drawableRes),
                                    contentDescription = title,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = desc,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
    )
}

@Composable
fun <T> ExpressiveDropdownSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    currentValue: String,
    options: List<Pair<String, T>>,
    shape: RoundedCornerShape,
    onSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { expanded = true },
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentValue,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { (optionLabel, value) ->
                        DropdownMenuItem(
                            text = { Text(optionLabel, fontWeight = if (optionLabel == currentValue) FontWeight.Bold else FontWeight.Normal) },
                            onClick = {
                                onSelected(value)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpressiveActionSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    shape: RoundedCornerShape,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ExpressiveSupportCard(
    onSupportClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Favorite, null, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    "Support Mizan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "Keep Mizan ad-free, open, and evolving with continuous features and polish.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onSupportClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Default.Favorite, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Support", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ExpressiveAboutCard(onClick: () -> Unit = {}) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = SettingsGroupShape.SINGLE,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Mizan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ) {
                            Text(
                                "v1.0.0",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Crafted with ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = " by Hotaro • Tap for changelog & updates",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Hotaro26"))
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_github),
                        contentDescription = "GitHub",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("GitHub")
                }
                FilledTonalButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.pinterest.com/hotaro344/"))
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_pinterest),
                        contentDescription = "Pinterest",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Pinterest")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportBottomSheet(
    onDismissRequest: () -> Unit,
    upiId: String
) {
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            Text(
                "Support Mizan",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                "Help sustain development, keep the app 100% ad-free, and bring more features to life.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            // Card 1: UPI Support
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Payments, null, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("UPI Payment", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(upiId, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val uri = Uri.parse("upi://pay").buildUpon()
                                    .appendQueryParameter("pa", upiId)
                                    .appendQueryParameter("pn", "Hotaro")
                                    .appendQueryParameter("tn", "Support Mizan Development")
                                    .appendQueryParameter("cu", "INR")
                                    .build()
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("UPI ID", upiId))
                                    copied = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Pay via UPI App")
                        }
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("UPI ID", upiId))
                                copied = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(if (copied) "Copied!" else "Copy UPI ID")
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Card 2: Star on GitHub
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Hotaro26/QuranReader"))
                        context.startActivity(intent)
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(R.drawable.ic_github),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Star the Repository", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Show some love on GitHub", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300))
                }
            }

            Spacer(Modifier.height(12.dp))

            // Card 3: Buy Me a Coffee
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://buymeacoffee.com/hotaro26"))
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(R.drawable.ic_coffee),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Buy Me a Coffee", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Fuel ongoing design & development", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

private fun formatDisplayPrayerTime(time: String, use24Hour: Boolean): String {
    return try {
        val inFormat = SimpleDateFormat("HH:mm", Locale.US)
        val outFormat = if (use24Hour) SimpleDateFormat("HH:mm", Locale.US) else SimpleDateFormat("hh:mm a", Locale.US)
        val date = inFormat.parse(time) ?: Date()
        outFormat.format(date)
    } catch (_: Exception) {
        time
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                )
                content()
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    dismissButton?.invoke()
                    Spacer(Modifier.width(8.dp))
                    confirmButton()
                }
            }
        }
    }
}
