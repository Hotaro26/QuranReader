@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
package com.hotaro.quranreader.ui.screen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hotaro.quranreader.R
import com.hotaro.quranreader.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val colorPalette by viewModel.colorPalette.collectAsState()
    val use24HourFormat by viewModel.use24HourFormat.collectAsState()
    val selectedTranslation by viewModel.selectedTranslation.collectAsState()
    val appFont by viewModel.appFont.collectAsState()
    val editions by viewModel.filteredEditions.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showTranslationDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val myUpiId = "sakibreza035@okaxis"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.headlineLarge) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                SupportDevCard(
                    onSupportClick = {
                        val payeeName = "Hotaro"
                        val transactionNote = "Support Quran Reader Development"
                        
                        val uri = Uri.parse("upi://pay").buildUpon()
                            .appendQueryParameter("pa", myUpiId)
                            .appendQueryParameter("pn", payeeName)
                            .appendQueryParameter("tn", transactionNote)
                            .appendQueryParameter("am", "0")
                            .appendQueryParameter("cu", "INR")
                            .build()
                        
                        val upiIntent = Intent(Intent.ACTION_VIEW, uri)
                        try {
                            context.startActivity(upiIntent)
                        } catch (e: Exception) {
                            showSupportDialog = true
                        }
                    }
                )
            }

            item {
                Text(text = "Appearance", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }

            item {
                CustomizeCard(
                    themeMode = themeMode,
                    onThemeModeSelected = { viewModel.setThemeMode(it) },
                    colorPalette = colorPalette,
                    onPaletteSelected = { viewModel.setColorPalette(it) },
                    appFont = appFont,
                    onFontSelected = { viewModel.setAppFont(it) },
                    use24HourFormat = use24HourFormat,
                    onFormatSelected = { viewModel.setUse24HourFormat(it) }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(text = "Reading", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }

            item {
                ListItem(
                    headlineContent = { Text("Translation") },
                    supportingContent = { Text(selectedTranslation) },
                    leadingContent = { Icon(Icons.Default.Translate, contentDescription = null) },
                    modifier = Modifier.clickable { showTranslationDialog = true }
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(text = "About", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }

            item {
                AboutAppCard()
            }
        }
        }
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
            title = { Text("Support hotaro", textAlign = TextAlign.Center) },
            text = {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text(
                        "Thank you for considering support! If your UPI app didn't open automatically, you can use this ID:",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(16.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = myUpiId,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Every bit of support helps keep Quran Reader growing. ❤️",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }

    if (showTranslationDialog) {
        AlertDialog(
            onDismissRequest = { showTranslationDialog = false },
            title = { Text("Select Translation") },
            text = {
                Column {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                        items(editions) { edition ->
                            ListItem(
                                headlineContent = { Text(edition.name) },
                                supportingContent = { Text("${edition.author} • ${edition.language.uppercase()}") },
                                modifier = Modifier.clickable {
                                    viewModel.selectTranslation(edition)
                                    showTranslationDialog = false
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTranslationDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun SupportDevCard(
    onSupportClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        )
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Text(
                    "Support Development",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Help keep Quran Reader alive and ad-free. Your support helps me maintain and improve the app.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(16.dp))
            
            Button(
                onClick = onSupportClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Default.Payments, null)
                Spacer(Modifier.width(8.dp))
                Text("Support via UPI")
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search editions...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = MaterialTheme.shapes.medium,
        singleLine = true
    )
}

@Composable
fun CustomizeCard(
    themeMode: Int,
    onThemeModeSelected: (Int) -> Unit,
    colorPalette: String,
    onPaletteSelected: (String) -> Unit,
    appFont: String,
    onFontSelected: (String) -> Unit,
    use24HourFormat: Boolean,
    onFormatSelected: (Boolean) -> Unit
) {
    val palettes = listOf(
        "Material You" to "dynamic",
        "Classic Green" to "classic",
        "Lavender" to "lavender",
        "Pink" to "pink",
        "Mocha" to "mocha",
        "Catppuccin" to "catppuccin",
        "Monochrome" to "monochrome"
    )

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

    val themes = listOf(
        "System" to 0,
        "Light" to 1,
        "Dark" to 2
    )

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingDropdown(
                label = "Theme Mode",
                icon = Icons.Default.Visibility,
                currentValue = themes.find { it.second == themeMode }?.first ?: "System",
                options = themes,
                onOptionSelected = { onThemeModeSelected(it) }
            )

            SettingDropdown(
                label = "Color Palette",
                icon = Icons.Default.ColorLens,
                currentValue = palettes.find { it.second == colorPalette }?.first ?: "Material You",
                options = palettes,
                onOptionSelected = { onPaletteSelected(it) }
            )

            SettingDropdown(
                label = "App Font",
                icon = Icons.Default.TextFields,
                currentValue = fonts.find { it.second == appFont }?.first ?: "Default",
                options = fonts,
                onOptionSelected = { onFontSelected(it) }
            )

            SettingDropdown(
                label = "Clock Format",
                icon = Icons.Default.Schedule,
                currentValue = if (use24HourFormat) "24-hour" else "12-hour",
                options = listOf("12-hour" to false, "24-hour" to true),
                onOptionSelected = { onFormatSelected(it) }
            )
        }
    }
}

@Composable
fun <T> SettingDropdown(
    label: String,
    icon: ImageVector,
    currentValue: String,
    options: List<Pair<String, T>>,
    onOptionSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = label, style = MaterialTheme.typography.titleSmall)
            }

            Box {
                Surface(
                    onClick = { expanded = true },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = currentValue, style = MaterialTheme.typography.labelLarge)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { (optionLabel, value) ->
                        DropdownMenuItem(
                            text = { Text(optionLabel) },
                            onClick = {
                                onOptionSelected(value)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable FlowRowScope.() -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}

@Composable
fun AboutAppCard() {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "About App / Dev", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Developed with ❤️ by Hotaro",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Hotaro26"))
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("GitHub")
                }
                OutlinedButton(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.pinterest.com/hotaro344/"))
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Pinterest")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tech Stack & APIs",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "• Jetpack Compose & Kotlin\n• Hilt for Dependency Injection\n• Retrofit & Gson for Networking\n• Room & DataStore for Local Data\n• Quran API by Fawaz Ahmed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
