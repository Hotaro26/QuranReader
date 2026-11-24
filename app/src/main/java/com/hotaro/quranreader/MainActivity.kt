package com.hotaro.quranreader

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import com.hotaro.quranreader.ui.glass.AttendXFloatingNavBar
import com.hotaro.quranreader.ui.glass.AttendXNavTab
import com.hotaro.quranreader.ui.glass.GlassEffectConfig
import com.hotaro.quranreader.ui.glass.LocalAppBackdrop
import com.hotaro.quranreader.ui.glass.LocalGlassEffectConfig
import com.hotaro.quranreader.ui.glass.backdrop.backdrops.LayerBackdrop
import com.hotaro.quranreader.ui.glass.backdrop.backdrops.layerBackdrop
import com.hotaro.quranreader.ui.glass.backdrop.backdrops.rememberBackdropFreeze
import com.hotaro.quranreader.ui.glass.backdrop.backdrops.rememberLayerBackdrop
import com.hotaro.quranreader.ui.glass.floatingtabbar.rememberFloatingTabBarScrollConnection
import com.hotaro.quranreader.ui.glass.isGlassAllowed
import com.hotaro.quranreader.ui.navigation.sharedAxisXEnter
import com.hotaro.quranreader.ui.navigation.sharedAxisXExit
import com.hotaro.quranreader.ui.navigation.sharedAxisXPopEnter
import com.hotaro.quranreader.ui.navigation.sharedAxisXPopExit
import com.hotaro.quranreader.ui.navigation.slideFadeInFromRight
import com.hotaro.quranreader.ui.navigation.slideFadeOutToRight
import com.hotaro.quranreader.ui.navigation.slideFadeInFromLeft
import com.hotaro.quranreader.ui.navigation.slideFadeOutToLeft
import com.hotaro.quranreader.ui.screen.HomeScreen
import com.hotaro.quranreader.ui.screen.QiblaScreen
import com.hotaro.quranreader.ui.screen.ReaderScreen
import com.hotaro.quranreader.ui.screen.SettingsScreen
import com.hotaro.quranreader.ui.screen.SurahListScreen
import com.hotaro.quranreader.ui.theme.QuranReaderTheme
import com.hotaro.quranreader.ui.viewmodel.HomeViewModel
import com.hotaro.quranreader.ui.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

val LocalFloatingNavBarPadding = compositionLocalOf { 110.dp }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val homeViewModel: HomeViewModel = hiltViewModel()
            val themeMode by themeViewModel.themeMode.collectAsState(initial = 0)
            val colorPalette by themeViewModel.colorPalette.collectAsState(initial = "dynamic")
            val appFont by themeViewModel.appFont.collectAsState(initial = "default")

            val glassConfig = remember {
                GlassEffectConfig(
                    globalEnabled = true,
                    vibrancy = 1.25f,
                    blurRadius = 3f,
                    surfaceOpacity = 0.50f,
                    navBarEnabled = true
                )
            }
            val appBackdrop = rememberLayerBackdrop()

            QuranReaderTheme(
                themeMode = themeMode,
                paletteName = colorPalette,
                appFont = appFont
            ) {
                CompositionLocalProvider(
                    LocalGlassEffectConfig provides glassConfig,
                    LocalAppBackdrop provides appBackdrop
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        QuranApp(homeViewModel, appBackdrop)
                    }
                }
            }
        }
    }
}

@Composable
fun QuranApp(homeViewModel: HomeViewModel, appBackdrop: LayerBackdrop) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp > 600
    val view = LocalView.current
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

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
        measuredNavBarPadding + 20.dp
    } else {
        120.dp
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
                            if (isGlassAllowed()) {
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
                        startDestination = "home",
                        modifier = Modifier.fillMaxSize(),
                        enterTransition = {
                            val initialRoute = initialState.destination.route
                            val targetRoute = targetState.destination.route
                            val initialIndex = tabRoutes.indexOf(initialRoute)
                            val targetIndex = tabRoutes.indexOf(targetRoute)

                            if (initialIndex != -1 && targetIndex != -1 && initialIndex != targetIndex) {
                                if (targetIndex > initialIndex) sharedAxisXEnter() else sharedAxisXPopEnter()
                            } else {
                                slideFadeInFromRight()
                            }
                        },
                        exitTransition = {
                            val initialRoute = initialState.destination.route
                            val targetRoute = targetState.destination.route
                            val initialIndex = tabRoutes.indexOf(initialRoute)
                            val targetIndex = tabRoutes.indexOf(targetRoute)

                            if (initialIndex != -1 && targetIndex != -1 && initialIndex != targetIndex) {
                                if (targetIndex > initialIndex) sharedAxisXExit() else sharedAxisXPopExit()
                            } else {
                                slideFadeOutToLeft()
                            }
                        },
                        popEnterTransition = {
                            val initialRoute = initialState.destination.route
                            val targetRoute = targetState.destination.route
                            val initialIndex = tabRoutes.indexOf(initialRoute)
                            val targetIndex = tabRoutes.indexOf(targetRoute)

                            if (initialIndex != -1 && targetIndex != -1 && initialIndex != targetIndex) {
                                if (targetIndex > initialIndex) sharedAxisXEnter() else sharedAxisXPopEnter()
                            } else {
                                slideFadeInFromLeft()
                            }
                        },
                        popExitTransition = {
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
                        composable("settings") {
                            SettingsScreen()
                        }
                    }
                }

                // AttendX Liquid Glass Bottom Navigation Bar elevated above system navigation gesture pill
                if (!isWideScreen && showNavBar) {
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
