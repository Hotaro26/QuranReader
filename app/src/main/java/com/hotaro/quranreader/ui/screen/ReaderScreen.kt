package com.hotaro.quranreader.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hotaro.quranreader.ui.viewmodel.AyahUiModel
import com.hotaro.quranreader.ui.viewmodel.ReaderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    surahNumber: Int,
    surahName: String,
    initialAyah: Int = 1,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: ReaderViewModel = hiltViewModel()
) {
    val ayahs by viewModel.ayahs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(surahNumber) {
        viewModel.loadSurah(surahNumber)
    }

    LaunchedEffect(ayahs) {
        if (ayahs.isNotEmpty() && initialAyah > 1) {
            val index = ayahs.indexOfFirst { it.numberInSurah == initialAyah }
            if (index != -1) {
                listState.scrollToItem(index)
            }
        }
    }

    // Track scroll position to update "Last Read"
    val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    LaunchedEffect(firstVisibleItemIndex, ayahs) {
        if (ayahs.isNotEmpty()) {
            ayahs.getOrNull(firstVisibleItemIndex)?.let { ayah ->
                viewModel.updateLastRead(ayah.numberInSurah)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(surahName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Translation Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .widthIn(max = 840.dp)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(ayahs) { ayah ->
                    val isBookmarked = bookmarks.any { it.surahNumber == surahNumber && it.ayahNumber == ayah.numberInSurah }
                    AyahItem(
                        ayah = ayah,
                        isBookmarked = isBookmarked,
                        onBookmarkClick = { viewModel.toggleBookmark(ayah) }
                    )
                }
            }
            }
        }
    }
}

@Composable
fun AyahItem(
    ayah: AyahUiModel,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${ayah.numberInSurah}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            IconButton(onClick = onBookmarkClick) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Bookmark",
                    tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            }
        }

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Text(
                text = ayah.arabicText,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    lineHeight = 44.sp
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = ayah.translationText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        HorizontalDivider(
            modifier = Modifier.padding(top = 16.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}
