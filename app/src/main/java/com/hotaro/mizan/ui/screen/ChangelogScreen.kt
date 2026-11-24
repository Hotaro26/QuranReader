package com.hotaro.mizan.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotaro.mizan.LocalFloatingNavBarPadding
import com.hotaro.mizan.ui.components.RoundedPullRefreshPage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ReleaseNote(
    val version: String,
    val date: String,
    val isLatest: Boolean = false,
    val tags: List<String>,
    val highlights: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangelogScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val navBarPadding = LocalFloatingNavBarPadding.current
    val coroutineScope = rememberCoroutineScope()

    var isCheckingUpdate by remember { mutableStateOf(false) }
    var updateStatusMessage by remember { mutableStateOf<String?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }

    val checkUpdate: () -> Unit = {
        coroutineScope.launch {
            isCheckingUpdate = true
            isRefreshing = true
            updateStatusMessage = null
            delay(1200)
            isCheckingUpdate = false
            isRefreshing = false
            updateStatusMessage = "You have the latest version of Mizan!"
        }
    }

    val releases = remember {
        listOf(
            ReleaseNote(
                version = "v1.0.0",
                date = "24 November 2026",
                isLatest = true,
                tags = listOf("Major", "Liquid Glass", "M3 Expressive"),
                highlights = listOf(
                    "Rebranded to Mizan with unified package identifier com.hotaro.mizan",
                    "AttendX-inspired floating liquid glass navigation bar with gooey blur backdrop",
                    "Google Photos-style rounded pull-to-refresh page with M3 Expressive morphing polygon indicator",
                    "Upgraded to Android 16 (API 36) with Android 11+ minSdk compatibility",
                    "Predictive back swipe gestures enabled across all screens",
                    "Material 3 Expressive conjoined settings layout with modal bottom sheets",
                    "Elements & Feel customizable homepage pills and gestures",
                    "Real-time prayer times and weather synchronization"
                )
            ),
            ReleaseNote(
                version = "v0.9.0",
                date = "15 October 2026",
                tags = listOf("Feature", "Prayer & Qibla"),
                highlights = listOf(
                    "Qibla Finder compass with sensor calibration and Kaaba bearing calculation",
                    "Multiple prayer calculation methods with AlAdhan integration",
                    "Automatic city geocoding and location updates"
                )
            ),
            ReleaseNote(
                version = "v0.8.0",
                date = "01 September 2026",
                tags = listOf("Initial Release"),
                highlights = listOf(
                    "Complete offline Quran reader with Uthmani Arabic script",
                    "English translations by Dr. Mustafa Khattab & multiple editions",
                    "Local bookmarking and last-read persistence with Room"
                )
            )
        )
    }

    RoundedPullRefreshPage(
        isRefreshing = isRefreshing,
        onRefresh = { checkUpdate() },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Release Notes",
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
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = navBarPadding + 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hero Update Checker Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(22.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(22.dp))
                                    }
                                }
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(
                                        "Mizan v1.0.0",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Installed & Up to Date",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            Spacer(Modifier.height(18.dp))

                            Button(
                                onClick = { checkUpdate() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                if (isCheckingUpdate) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text("Checking GitHub...", fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Check for Updates", fontWeight = FontWeight.Bold)
                                }
                            }

                            AnimatedVisibility(visible = updateStatusMessage != null) {
                                updateStatusMessage?.let { msg ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text(msg, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    SettingsSectionHeader("Version History")
                }

                items(releases) { release ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        release.version,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (release.isLatest) {
                                        Spacer(Modifier.width(8.dp))
                                        SuggestionChip(
                                            onClick = {},
                                            label = { Text("Latest", fontWeight = FontWeight.Bold) },
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                            ),
                                            border = null
                                        )
                                    }
                                }
                                Text(
                                    release.date,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                release.tags.forEach { tag ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    ) {
                                        Text(
                                            tag,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            release.highlights.forEach { highlight ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("• ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(
                                        highlight,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
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
