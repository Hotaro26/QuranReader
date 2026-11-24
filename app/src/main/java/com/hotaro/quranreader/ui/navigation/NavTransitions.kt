package com.hotaro.quranreader.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.ui.unit.IntOffset

fun slideFadeInFromRight(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { (it * 0.15f).toInt() },
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        )
    ) + fadeIn(
        initialAlpha = 0.5f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )
}

fun slideFadeOutToRight(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { (it * 0.10f).toInt() },
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        )
    ) + fadeOut(
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    ) + scaleOut(
        targetScale = 0.95f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )
}

fun slideFadeInFromLeft(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { -(it * 0.15f).toInt() },
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        )
    ) + fadeIn(
        initialAlpha = 0.5f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )
}

fun slideFadeOutToLeft(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { -(it * 0.10f).toInt() },
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        )
    ) + fadeOut(
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )
}

fun sharedAxisXEnter(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { fullWidth -> (fullWidth * 0.12f).toInt() },
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        )
    ) + fadeIn(
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )
}

fun sharedAxisXExit(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { fullWidth -> -(fullWidth * 0.12f).toInt() },
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        )
    ) + fadeOut(
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )
}

fun sharedAxisXPopEnter(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { fullWidth -> -(fullWidth * 0.12f).toInt() },
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        )
    ) + fadeIn(
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )
}

fun sharedAxisXPopExit(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { fullWidth -> (fullWidth * 0.12f).toInt() },
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntOffset.VisibilityThreshold
        )
    ) + fadeOut(
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    ) + scaleOut(
        targetScale = 0.95f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )
}
