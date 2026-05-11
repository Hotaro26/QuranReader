package com.hotaro.quranreader.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hotaro.quranreader.data.model.Bookmark
import com.hotaro.quranreader.data.model.PrayerTime
import com.hotaro.quranreader.data.model.Surah
import com.hotaro.quranreader.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onContinueClick: (Surah, Int) -> Unit,
    onBookmarkClick: (Bookmark) -> Unit,
    onQiblaClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quran Reader", style = MaterialTheme.typography.headlineLarge) }
            )
        },
        floatingActionButton = {
            uiState.lastReadSurah?.let { surah ->
                ExtendedFloatingActionButton(
                    onClick = { onContinueClick(surah, uiState.lastReadAyah) },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                    text = { Text("Continue") },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    TimeAndPrayerCard(
                        currentTime = uiState.currentTime,
                        prayerTimes = uiState.prayerTimes
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        QiblaCard(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = onQiblaClick
                        )
                        LastReadCard(
                            surah = uiState.lastReadSurah,
                            ayahNumber = uiState.lastReadAyah,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = { uiState.lastReadSurah?.let { onContinueClick(it, uiState.lastReadAyah) } }
                        )
                    }
                }

                item {
                    Text(
                    text = "Recent Bookmarks",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.bookmarks.isEmpty()) {
                item {
                    Text(
                        text = "No bookmarks yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                item {
                    val scrollState = rememberScrollState()
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .verticalScroll(scrollState)
                        ) {
                            uiState.bookmarks.forEach { bookmark ->
                                BookmarkItem(
                                    bookmark = bookmark,
                                    onClick = { onBookmarkClick(bookmark) }
                                )
                            }
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
        }
    }
}

@Composable
fun TimeAndPrayerCard(
    currentTime: String,
    prayerTimes: List<PrayerTime>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Current Time", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = currentTime,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(text = "Prayer Times", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(prayerTimes) { prayer ->
                    PrayerTimeItem(prayer)
                }
            }
        }
    }
}

@Composable
fun PrayerTimeItem(prayer: PrayerTime) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = if (prayer.isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (prayer.isNext) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = prayer.name, style = MaterialTheme.typography.labelSmall)
            Text(text = prayer.time, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LastReadCard(
    surah: Surah?,
    ayahNumber: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Book, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Last Read", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (surah != null) {
                Text(
                    text = surah.englishName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ayah $ayahNumber",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Text(text = "Start reading to see your progress here.")
            }
        }
    }
}

@Composable
fun BookmarkItem(
    bookmark: Bookmark,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Bookmark,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "${bookmark.surahName} - Ayah ${bookmark.ayahNumber}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            if (bookmark.text.isNotBlank()) {
                Text(
                    text = bookmark.text,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun QiblaCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Explore, 
                contentDescription = null, 
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Qibla Finder", 
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = "Find the direction of the Kaaba",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}
