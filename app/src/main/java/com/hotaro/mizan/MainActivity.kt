package com.hotaro.mizan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hotaro.mizan.reminder.PrayerReminderManager
import com.hotaro.mizan.ui.glass.AttendXFloatingNavBar
import com.hotaro.mizan.ui.glass.AttendXNavTab
import com.hotaro.mizan.ui.glass.GlassEffectConfig
import com.hotaro.mizan.ui.glass.GlassStyle
import com.hotaro.mizan.ui.glass.LocalAppBackdrop
import com.hotaro.mizan.ui.glass.LocalGlassEffectConfig
import com.hotaro.mizan.ui.glass.backdrop.backdrops.LayerBackdrop
import com.hotaro.mizan.ui.glass.backdrop.backdrops.layerBackdrop
import com.hotaro.mizan.ui.glass.backdrop.backdrops.rememberBackdropFreeze
import com.hotaro.mizan.ui.glass.backdrop.backdrops.rememberLayerBackdrop
import com.hotaro.mizan.ui.glass.floatingtabbar.rememberFloatingTabBarScrollConnection
import com.hotaro.mizan.ui.glass.isGlassAllowed
import com.hotaro.mizan.ui.navigation.sharedAxisXEnter
import com.hotaro.mizan.ui.navigation.sharedAxisXExit
import com.hotaro.mizan.ui.navigation.sharedAxisXPopEnter
import com.hotaro.mizan.ui.navigation.sharedAxisXPopExit
import com.hotaro.mizan.ui.navigation.slideFadeInFromRight
import com.hotaro.mizan.ui.navigation.slideFadeOutToRight
import com.hotaro.mizan.ui.navigation.slideFadeInFromLeft
import com.hotaro.mizan.ui.navigation.slideFadeOutToLeft
import com.hotaro.mizan.ui.screen.ChangelogScreen
import com.hotaro.mizan.ui.screen.ClockAndTimerScreen
import com.hotaro.mizan.ui.screen.ElementsAndFeelScreen
import com.hotaro.mizan.ui.screen.HomeScreen
import com.hotaro.mizan.ui.screen.OnboardingScreen
import com.hotaro.mizan.ui.screen.QiblaScreen
import com.hotaro.mizan.ui.screen.ReaderScreen
import com.hotaro.mizan.ui.screen.SettingsScreen
import com.hotaro.mizan.ui.screen.SurahListScreen
import com.hotaro.mizan.ui.theme.MizanTheme
import com.hotaro.mizan.ui.theme.QuranReaderTheme
import com.hotaro.mizan.ui.viewmodel.HomeViewModel
import com.hotaro.mizan.ui.viewmodel.OnboardingViewModel
import com.hotaro.mizan.ui.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

val LocalFloatingNavBarPadding = compositionLocalOf { 110.dp }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        PrayerReminderManager.createNotificationChannel(this)
        PrayerReminderManager.scheduleReminders(this)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeConfig by themeViewModel.themeConfig.collectAsState()

            splashScreen.setKeepOnScreenCondition {
                themeConfig == null
            }

            if (themeConfig == null) {
                return@setContent
            }

            val currentConfig = themeConfig!!
            val glassConfig = remember(currentConfig.navBarBlurStyle) {
                when (currentConfig.navBarBlurStyle) {
                    "soft" -> GlassEffectConfig(
                        globalEnabled = true,
                        vibrancy = 1.0f,
                        blurRadius = 6f,
                        style = GlassStyle.BLUR,
                        surfaceOpacity = 0.50f,
                        navBarEnabled = true
                    )
                    "none" -> GlassEffectConfig(
                        globalEnabled = false,
                        style = GlassStyle.TRANSPARENT,
                        navBarEnabled = false
                    )
                    else -> GlassEffectConfig(
                        globalEnabled = true,
                        vibrancy = 1.25f,
                        blurRadius = 3f,
                        style = GlassStyle.LIQUID,
                        surfaceOpacity = 0.50f,
                        navBarEnabled = true
                    )
                }
            }
            val appBackdrop = rememberLayerBackdrop()

            QuranReaderTheme(
                themeMode = currentConfig.themeMode,
                paletteName = currentConfig.colorPalette,
                appFont = currentConfig.appFont
            ) {
                CompositionLocalProvider(
                    LocalGlassEffectConfig provides glassConfig,
                    LocalAppBackdrop provides appBackdrop
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        QuranApp(
                            homeViewModel = hiltViewModel(),
                            appBackdrop = appBackdrop,
                            initialConfig = currentConfig
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuranApp(
    homeViewModel: HomeViewModel,
    appBackdrop: LayerBackdrop,
    initialConfig: com.hotaro.mizan.ui.viewmodel.AppThemeConfig,
    themeViewModel: ThemeViewModel = hiltViewModel(),
    onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {
    val hasCompletedOnboarding by onboardingViewModel.hasCompleted.collectAsState(initial = initialConfig.hasCompletedOnboarding)
    val windowAnimationStyle by themeViewModel.windowAnimationStyle.collectAsState(initial = initialConfig.windowAnimationStyle)
    val useClassicNavBar by themeViewModel.useClassicNavBar.collectAsState(initial = initialConfig.useClassicNavBar)

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp > 600
    val view = LocalView.current
    val context = LocalContext.current

    val navTabs = remember {
        listOf(
            AttendXNavTab("Home", Icons.Filled.Home, Icons.Outlined.Home),
            AttendXNavTab("Surahs", Icons.Filled.Mosque, Icons.Outlined.Mosque),
            AttendXNavTab("Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
        )
    }

    val tabRoutes = remember { listOf("home", "surahs", "settings") }
    val showNavBar = currentDestination?.route in tabRoutes

    val selectedTabIndex = remember(currentDestination?.route) {
        val idx = tabRoutes.indexOf(currentDestination?.route)
        if (idx >= 0) idx else 0
    }

    val scrollConnection = rememberFloatingTabBarScrollConnection(
        initialIsInline = false,
    )
    val backdropFreeze = rememberBackdropFreeze()

    val density = LocalDensity.current
    var navBarOccupiedPx by remember { mutableFloatStateOf(0f) }
    val measuredNavBarPadding = with(density) { navBarOccupiedPx.toDp() }
    val navBarBottomPadding = if (measuredNavBarPadding > 0.dp) {
        if (useClassicNavBar) measuredNavBarPadding + 8.dp else measuredNavBarPadding + 20.dp
    } else {
        if (useClassicNavBar) 90.dp else 120.dp
    }

    CompositionLocalProvider(LocalFloatingNavBarPadding provides navBarBottomPadding) {
        Row(modifier = Modifier.fillMaxSize()) {
            if (isWideScreen && showNavBar) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                    navTabs.forEachIndexed { index, tab ->
                        val isSelected = selectedTabIndex == index
                        NavigationRailItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.label
                                )
                            },
                            label = { Text(tab.label) },
                            selected = isSelected,
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                if (tabRoutes[index] == "home") {
                                    homeViewModel.triggerHomeAnimation()
                                }
                                navController.navigate(tabRoutes[index]) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                    androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                // Screen content with backdrop sampling and scroll connections
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer()
                        .then(
                            if (isGlassAllowed() && !useClassicNavBar) {
                                Modifier.layerBackdrop(
                                    appBackdrop,
                                    frozen = backdropFreeze.frozen
                                )
                            } else Modifier
                        )
                        .graphicsLayer()
                        .nestedScroll(backdropFreeze.connection)
                        .nestedScroll(scrollConnection)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = if (hasCompletedOnboarding == false) "onboarding" else "home",
                        modifier = Modifier.fillMaxSize(),
                        enterTransition = {
                            when (windowAnimationStyle) {
                                "pop" -> scaleIn(
                                    initialScale = 0.82f,
                                    animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow)
                                ) + fadeIn(tween(250, easing = FastOutSlowInEasing))
                                "fade" -> fadeIn(tween(280, easing = FastOutSlowInEasing))
                                "slide_up" -> slideInVertically(
                                    initialOffsetY = { (it * 0.18f).toInt() },
                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                ) + fadeIn(tween(250, easing = FastOutSlowInEasing))
                                else -> {
                                    val initialRoute = initialState.destination.route
                                    val targetRoute = targetState.destination.route
                                    val initialIndex = tabRoutes.indexOf(initialRoute)
                                    val targetIndex = tabRoutes.indexOf(targetRoute)
                                    if (initialIndex != -1 && targetIndex != -1 && initialIndex != targetIndex) {
                                        if (targetIndex > initialIndex) sharedAxisXEnter() else sharedAxisXPopEnter()
                                    } else {
                                        slideFadeInFromRight()
                                    }
                                }
                            }
                        },
                        exitTransition = {
                            when (windowAnimationStyle) {
                                "pop" -> scaleOut(
                                    targetScale = 1.12f,
                                    animationSpec = tween(200, easing = FastOutSlowInEasing)
                                ) + fadeOut(tween(180, easing = FastOutSlowInEasing))
                                "fade" -> fadeOut(tween(200, easing = FastOutSlowInEasing))
                                "slide_up" -> slideOutVertically(
                                    targetOffsetY = { -(it * 0.10f).toInt() },
                                    animationSpec = tween(220, easing = FastOutSlowInEasing)
                                ) + fadeOut(tween(200, easing = FastOutSlowInEasing))
                                else -> {
                                    val initialRoute = initialState.destination.route
                                    val targetRoute = targetState.destination.route
                                    val initialIndex = tabRoutes.indexOf(initialRoute)
                                    val targetIndex = tabRoutes.indexOf(targetRoute)
                                    if (initialIndex != -1 && targetIndex != -1 && initialIndex != targetIndex) {
                                        if (targetIndex > initialIndex) sharedAxisXExit() else sharedAxisXPopExit()
                                    } else {
                                        slideFadeOutToLeft()
                                    }
                                }
                            }
                        },
                        popEnterTransition = {
                            when (windowAnimationStyle) {
                                "pop" -> scaleIn(
                                    initialScale = 1.12f,
                                    animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow)
                                ) + fadeIn(tween(250, easing = FastOutSlowInEasing))
                                "fade" -> fadeIn(tween(280, easing = FastOutSlowInEasing))
                                "slide_up" -> slideInVertically(
                                    initialOffsetY = { -(it * 0.10f).toInt() },
                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                ) + fadeIn(tween(250, easing = FastOutSlowInEasing))
                                else -> {
                                    val initialRoute = initialState.destination.route
                                    val targetRoute = targetState.destination.route
                                    val initialIndex = tabRoutes.indexOf(initialRoute)
                                    val targetIndex = tabRoutes.indexOf(targetRoute)
                                    if (initialIndex != -1 && targetIndex != -1 && initialIndex != targetIndex) {
                                        if (targetIndex > initialIndex) sharedAxisXEnter() else sharedAxisXPopEnter()
                                    } else {
                                        slideFadeInFromLeft()
                                    }
                                }
                            }
                        },
                        popExitTransition = {
                            when (windowAnimationStyle) {
                                "pop" -> scaleOut(
                                    targetScale = 0.82f,
                                    animationSpec = tween(200, easing = FastOutSlowInEasing)
                                ) + fadeOut(tween(180, easing = FastOutSlowInEasing))
                                "fade" -> fadeOut(tween(200, easing = FastOutSlowInEasing))
                                "slide_up" -> slideOutVertically(
                                    targetOffsetY = { (it * 0.18f).toInt() },
                                    animationSpec = tween(220, easing = FastOutSlowInEasing)
                                ) + fadeOut(tween(200, easing = FastOutSlowInEasing))
                                else -> {
                                    val initialRoute = initialState.destination.route
                                    val targetRoute = targetState.destination.route
                                    val initialIndex = tabRoutes.indexOf(initialRoute)
                                    val targetIndex = tabRoutes.indexOf(targetRoute)
                                    if (initialIndex != -1 && targetIndex != -1 && initialIndex != targetIndex) {
                                        if (targetIndex > initialIndex) sharedAxisXExit() else sharedAxisXPopExit()
                                    } else {
                                        slideFadeOutToRight()
                                    }
                                }
                            }
                        }
                    ) {
                        composable("home") {
                            HomeScreen(
                                onContinueClick = { surah, ayah ->
                                    navController.navigate("reader/${surah.number}/${surah.englishName}?ayah=$ayah")
                                },
                                onBookmarkClick = { bookmark ->
                                    navController.navigate("reader/${bookmark.surahNumber}/${bookmark.surahName}?ayah=${bookmark.ayahNumber}")
                                },
                                onQiblaClick = {
                                    navController.navigate("qibla")
                                }
                            )
                        }
                        composable("qibla") {
                            QiblaScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable("surahs") {
                            SurahListScreen(
                                onSurahClick = { surah ->
                                    navController.navigate("reader/${surah.number}/${surah.englishName}")
                                }
                            )
                        }
                        composable(
                            route = "reader/{surahNumber}/{surahName}?ayah={ayahNumber}",
                            arguments = listOf(
                                navArgument("surahNumber") { type = NavType.IntType },
                                navArgument("surahName") { type = NavType.StringType },
                                navArgument("ayahNumber") {
                                    type = NavType.IntType
                                    defaultValue = 1
                                }
                            )
                        ) { backStackEntry ->
                            val surahNumber = backStackEntry.arguments?.getInt("surahNumber") ?: 1
                            val surahName = backStackEntry.arguments?.getString("surahName") ?: ""
                            val ayahNumber = backStackEntry.arguments?.getInt("ayahNumber") ?: 1
                            ReaderScreen(
                                surahNumber = surahNumber,
                                surahName = surahName,
                                initialAyah = ayahNumber,
                                onBackClick = { navController.popBackStack() },
                                onSettingsClick = {
                                    navController.navigate("settings")
                                }
                            )
                        }
                        composable("onboarding") {
                            OnboardingScreen(
                                onComplete = {
                                    navController.navigate("home") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onElementsAndFeelClick = {
                                    navController.navigate("elements_and_feel")
                                },
                                onClockAndTimerClick = {
                                    navController.navigate("clock_and_timer")
                                },
                                onAboutClick = {
                                    navController.navigate("changelog")
                                },
                                onPermissionsClick = {
                                    navController.navigate("onboarding")
                                }
                            )
                        }
                        composable("clock_and_timer") {
                            ClockAndTimerScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable("elements_and_feel") {
                            ElementsAndFeelScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable("changelog") {
                            ChangelogScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }

                // Bottom Navigation Bar: Classic Docked or Floating Liquid Glass
                if (!isWideScreen && showNavBar) {
                    if (useClassicNavBar) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            tonalElevation = 4.dp,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .onSizeChanged { size ->
                                    if (size.height > navBarOccupiedPx) {
                                        navBarOccupiedPx = size.height.toFloat()
                                    }
                                }
                        ) {
                            navTabs.forEachIndexed { index, tab ->
                                val isSelected = selectedTabIndex == index
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        val targetRoute = tabRoutes[index]
                                        if (targetRoute == "home") {
                                            homeViewModel.triggerHomeAnimation()
                                        }
                                        navController.navigate(targetRoute) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.label
                                        )
                                    },
                                    label = {
                                        Text(
                                            tab.label,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                )
                            }
                        }
                    } else {
                        AttendXFloatingNavBar(
                            tabs = navTabs,
                            selectedIndex = selectedTabIndex,
                            onTabSelected = { index ->
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                val targetRoute = tabRoutes[index]
                                if (targetRoute == "home") {
                                    homeViewModel.triggerHomeAnimation()
                                }
                                navController.navigate(targetRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            scrollConnection = scrollConnection,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .onSizeChanged { size ->
                                    if (size.height > navBarOccupiedPx) {
                                        navBarOccupiedPx = size.height.toFloat()
                                    }
                                }
                                .navigationBarsPadding()
                                .padding(bottom = 12.dp, start = 20.dp, end = 20.dp)
                        )
                    }
                }
            }
        }
    }
}
