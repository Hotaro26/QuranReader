package com.hotaro.mizan.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotaro.mizan.R
import com.hotaro.mizan.ui.viewmodel.TasbihViewModel
import kotlin.math.abs

@Composable
fun QiblaTasbihCard(
    modifier: Modifier = Modifier,
    onQiblaClick: () -> Unit,
    onTasbihClick: () -> Unit,
    tasbihViewModel: TasbihViewModel,
    iconRotation: Float = 0f
) {
    val activePage by tasbihViewModel.cardPage.collectAsState()
    val haptic = LocalHapticFeedback.current

    var showIndicator by remember { mutableStateOf(true) }
    var interactionTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(activePage, interactionTrigger) {
        showIndicator = true
        kotlinx.coroutines.delay(3000L)
        showIndicator = false
    }

    val indicatorAlpha by animateFloatAsState(
        targetValue = if (showIndicator) 1f else 0f,
        animationSpec = tween(400),
        label = "indicatorAlpha"
    )

    val currentDhikr by tasbihViewModel.currentDhikr.collectAsState()
    val currentCount by tasbihViewModel.currentCount.collectAsState()
    val targetCount by tasbihViewModel.targetCount.collectAsState()

    // Horizontal swipe gesture only (tap-only for counting, no scroll-to-count)
    var dragAccumulatorX by remember { mutableFloatStateOf(0f) }
    val dragThreshold = 35f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    interactionTrigger++
                }
            }
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    dragAccumulatorX += delta
                    if (abs(dragAccumulatorX) >= dragThreshold) {
                        if (dragAccumulatorX < 0 && activePage == 0) {
                            tasbihViewModel.setCardPage(1)
                        } else if (dragAccumulatorX > 0 && activePage == 1) {
                            tasbihViewModel.setCardPage(0)
                        }
                        dragAccumulatorX = 0f
                    }
                }
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Row: Mode title, Icon, and Expand Button (No indicator pills here)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        tasbihViewModel.setCardPage((activePage + 1) % 2)
                    }
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (activePage == 0) {
                                Icon(
                                    Icons.Default.Explore,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(17.dp)
                                        .rotate(iconRotation)
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_tasbih),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiary,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (activePage == 0) "Qibla" else "Tasbih",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = {
                        if (activePage == 0) onQiblaClick() else onTasbihClick()
                    },
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer { alpha = indicatorAlpha },
                    enabled = indicatorAlpha > 0.05f
                ) {
                    Icon(
                        Icons.Default.OpenInFull,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            // Animated Content Body (Tap only, clean styling without highlight boxes)
            AnimatedContent(
                targetState = activePage,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn(tween(200)))
                            .togetherWith(slideOutHorizontally { width -> -width } + fadeOut(tween(150)))
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn(tween(200)))
                            .togetherWith(slideOutHorizontally { width -> width } + fadeOut(tween(150)))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = "qiblaTasbihCardTransition"
            ) { page ->
                if (page == 0) {
                    // Qibla Mode
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onQiblaClick() }
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Compass",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Tap to open Finder",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.75f)
                            )
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.75f)
                            )
                        }
                    }
                } else {
                    // Tasbih Mode (Tap to count only)
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onTasbihClick() }
                        ) {
                            Text(
                                text = currentDhikr.transliteration,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = currentDhikr.arabic,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.75f),
                                maxLines = 1
                            )
                        }

                        // Circular Tap Puck (Tap only)
                        val progress = if (targetCount > 0) (currentCount % targetCount).toFloat() / targetCount else 0f
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary,
                            shadowElevation = 3.dp,
                            modifier = Modifier
                                .size(48.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    tasbihViewModel.increment()
                                    try {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    } catch (e: Exception) {
                                        // Ignore
                                    }
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxSize(),
                                    color = MaterialTheme.colorScheme.onTertiary,
                                    trackColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.25f),
                                    strokeWidth = 3.dp,
                                    strokeCap = StrokeCap.Round
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$currentCount",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        lineHeight = 15.sp
                                    )
                                    if (targetCount > 0) {
                                        Text(
                                            text = "/$targetCount",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 8.sp,
                                            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Row: Swipe indicator pills (Placed at the bottom of the card, auto-hides after 3s)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = indicatorAlpha }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        tasbihViewModel.setCardPage((activePage + 1) % 2)
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(2) { index ->
                    val isSelected = activePage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .width(if (isSelected) 14.dp else 5.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.tertiary
                                else MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.25f)
                            )
                    )
                }
            }
        }
    }
}
