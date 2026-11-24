package com.hotaro.mizan.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RoundedPullRefreshPage(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    containerColor: Color = Color.Unspecified,
    pageColor: Color = Color.Unspecified,
    pageShape: Shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
    content: @Composable (PaddingValues) -> Unit
) {
    val state = rememberPullToRefreshState()
    val view = LocalView.current
    val density = LocalDensity.current
    val isDark = isSystemInDarkTheme()

    val actualContainerColor = if (containerColor != Color.Unspecified) {
        containerColor
    } else if (isDark) {
        Color(0xFF090A0C)
    } else {
        MaterialTheme.colorScheme.surfaceContainerLowest
    }

    val actualPageColor = if (pageColor != Color.Unspecified) {
        pageColor
    } else if (isDark) {
        Color(0xFF141518)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val thresholdDp = 80.dp
    val thresholdPx = with(density) { thresholdDp.toPx() }
    val maxPullPx = with(density) { 140.dp.toPx() }

    // Haptic feedback tick when passing threshold
    var hasVibratedThreshold by remember { mutableStateOf(false) }
    LaunchedEffect(state.distanceFraction) {
        if (state.distanceFraction >= 1.0f && !hasVibratedThreshold && !isRefreshing) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            hasVibratedThreshold = true
        } else if (state.distanceFraction < 0.2f) {
            hasVibratedThreshold = false
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .pullToRefresh(
                state = state,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh
            ),
        containerColor = actualContainerColor,
        topBar = topBar,
        floatingActionButton = floatingActionButton
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            // Material 3 Expressive LoadingIndicator revealed behind the descending page
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .graphicsLayer {
                        val progress = state.distanceFraction.coerceIn(0f, 1f)
                        alpha = if (isRefreshing) 1f else progress
                        val scale = if (isRefreshing) 1f else 0.4f + (progress * 0.6f)
                        scaleX = scale
                        scaleY = scale
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isRefreshing) {
                    LoadingIndicator(
                        modifier = Modifier.size(42.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (state.distanceFraction > 0.05f) {
                    LoadingIndicator(
                        progress = { state.distanceFraction.coerceIn(0f, 1f) },
                        modifier = Modifier.size(42.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Rounded Page Surface that detaches and slides downwards when pulled
            val pullDistance = (state.distanceFraction * thresholdPx).coerceIn(0f, maxPullPx)
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = pullDistance
                        clip = true
                        shape = pageShape
                    },
                shape = pageShape,
                color = actualPageColor
            ) {
                val subPadding = PaddingValues(
                    start = innerPadding.calculateStartPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
                    top = 0.dp,
                    end = innerPadding.calculateEndPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
                    bottom = innerPadding.calculateBottomPadding()
                )
                content(subPadding)
            }
        }
    }
}
