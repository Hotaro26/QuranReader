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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
                ThemeSelection(
                    currentMode = themeMode,
                    onModeSelected = { viewModel.setThemeMode(it) }
                )
            }

            item {
                PaletteSelection(
                    currentPalette = colorPalette,
                    onPaletteSelected = { viewModel.setColorPalette(it) }
                )
            }

            item {
                ClockFormatSelection(
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
fun ThemeSelection(
    currentMode: Int,
    onModeSelected: (Int) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(Icons.Default.Visibility, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Theme Mode", style = MaterialTheme.typography.titleSmall)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("System" to 0, "Light" to 1, "Dark" to 2).forEach { (label, mode) ->
                    FilterChip(
                        selected = currentMode == mode,
                        onClick = { onModeSelected(mode) },
                        label = { Text(label) }
                    )
                }
            }
        }
    }
}

@Composable
fun PaletteSelection(
    currentPalette: String,
    onPaletteSelected: (String) -> Unit
) {
    val palettes = mutableListOf(
        "Material You" to "dynamic",
        "Classic Green" to "classic",
        "Lavender" to "lavender",
        "Pink" to "pink",
        "Mocha" to "mocha",
        "Catppuccin" to "catppuccin",
        "Monochrome" to "monochrome"
    )

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(Icons.Default.ColorLens, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Color Palette", style = MaterialTheme.typography.titleSmall)
            }
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                palettes.forEach { (label, palette) ->
                    FilterChip(
                        selected = currentPalette == palette,
                        onClick = { onPaletteSelected(palette) },
                        label = { Text(label) }
                    )
                }
            }
        }
    }
}

@Composable
fun ClockFormatSelection(
    use24HourFormat: Boolean,
    onFormatSelected: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Clock Format", style = MaterialTheme.typography.titleSmall)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !use24HourFormat,
                    onClick = { onFormatSelected(false) },
                    label = { Text("12-hour") }
                )
                FilterChip(
                    selected = use24HourFormat,
                    onClick = { onFormatSelected(true) },
                    label = { Text("24-hour") }
                )
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
