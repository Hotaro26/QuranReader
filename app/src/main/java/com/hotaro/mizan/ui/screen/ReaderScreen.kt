package com.hotaro.mizan.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hotaro.mizan.ui.components.RoundedPullRefreshPage
import com.hotaro.mizan.ui.viewmodel.AyahUiModel
import com.hotaro.mizan.ui.viewmodel.ReaderViewModel

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

    val navBarPadding = com.hotaro.mizan.LocalFloatingNavBarPadding.current
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

    RoundedPullRefreshPage(
        isRefreshing = isLoading,
        onRefresh = { viewModel.refresh() },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        surahName,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black
                    ) 
                },
                navigationIcon = {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(40.dp)
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(40.dp)
                    ) {
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Translation Settings",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .widthIn(max = 840.dp)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = navBarPadding + 24.dp),
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
    // M3E: Wrapping verses in distinct, highly rounded floating cards rather than flat lists
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pill shape for verse number
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Text(
                        text = "${ayah.numberInSurah}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Interactive Bookmark Button with spring scale
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val buttonScale by animateFloatAsState(
                    targetValue = if (isPressed) 0.8f else 1f,
                    animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow),
                    label = "bookmark_scale"
                )
                val iconTint by animateColorAsState(
                    targetValue = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    label = "bookmark_color"
                )

                IconButton(
                    onClick = onBookmarkClick,
                    interactionSource = interactionSource,
                    modifier = Modifier.scale(buttonScale)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Bookmark",
                        tint = iconTint,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = ayah.arabicText,
                    style = MaterialTheme.typography.displaySmall.copy(
                        lineHeight = 56.sp
                    ),
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Replaced the flat divider with soft visual separation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = ayah.translationText,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 28.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
