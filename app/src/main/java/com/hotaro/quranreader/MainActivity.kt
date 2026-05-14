package com.hotaro.quranreader

import android.Manifest
import android.content.pm.PackageManager
import android.view.HapticFeedbackConstants
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
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
import com.hotaro.quranreader.ui.screen.HomeScreen
import com.hotaro.quranreader.ui.screen.OnboardingScreen
import com.hotaro.quranreader.ui.screen.QiblaScreen
import com.hotaro.quranreader.ui.screen.ReaderScreen
import com.hotaro.quranreader.ui.screen.SettingsScreen
import com.hotaro.quranreader.ui.screen.SurahListScreen
import com.hotaro.quranreader.ui.screen.TrackerScreen
import com.hotaro.quranreader.ui.theme.QuranReaderTheme
import com.hotaro.quranreader.ui.viewmodel.OnboardingViewModel
import com.hotaro.quranreader.ui.viewmodel.HomeViewModel
import com.hotaro.quranreader.ui.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val homeViewModel: HomeViewModel = hiltViewModel()
            val themeMode by themeViewModel.themeMode.collectAsState(initial = 0)
            val colorPalette by themeViewModel.colorPalette.collectAsState(initial = "dynamic")
            val appFont by themeViewModel.appFont.collectAsState(initial = "default")

            QuranReaderTheme(
                themeMode = themeMode,
                paletteName = colorPalette,
                appFont = appFont
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuranApp(homeViewModel)
                }
            }
        }
    }
}

data class NavigationItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun QuranApp(homeViewModel: HomeViewModel) {
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

    val items = listOf(
        NavigationItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
        NavigationItem("surahs", "Surahs", Icons.Filled.Mosque, Icons.Outlined.Mosque),
        NavigationItem("tracker", "Tracker", Icons.Filled.TaskAlt, Icons.Outlined.TaskAlt),
        NavigationItem("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    val showNavBar = currentDestination?.route in listOf("home", "surahs", "tracker", "settings")
    var settingsRotationTarget by remember { mutableStateOf(0f) }

    Row(modifier = Modifier.fillMaxSize()) {
        if (isWideScreen && showNavBar) {
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Spacer(Modifier.weight(1f))
                items.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.2f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "scale"
                    )

                    val rotation by animateFloatAsState(
                        targetValue = if (item.route == "settings") settingsRotationTarget else 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "rotation"
                    )

                    NavigationRailItem(
                        icon = { 
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                modifier = Modifier
                                    .scale(scale)
                                    .rotate(rotation)
                            )
                        },
                        label = { Text(item.label) },
                        selected = isSelected,
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            if (item.route == "settings") {
                                settingsRotationTarget += 360f
                            }
                            if (item.route == "home") {
                                homeViewModel.triggerHomeAnimation()
                            }
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                Spacer(Modifier.weight(1f))
            }
        }

        Scaffold(
            modifier = Modifier.weight(1f),
            bottomBar = {
                if (!isWideScreen && showNavBar) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.background,
                        tonalElevation = 0.dp
                    ) {
                        items.forEach { item ->
                            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                            val scale by animateFloatAsState(
                                targetValue = if (isSelected) 1.2f else 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "scale"
                            )

                            val rotation by animateFloatAsState(
                                targetValue = if (item.route == "settings") settingsRotationTarget else 0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "rotation"
                            )

                            NavigationBarItem(
                                icon = { 
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label,
                                        modifier = Modifier
                                            .scale(scale)
                                            .rotate(rotation)
                                    )
                                },
                                label = { Text(item.label) },
                                selected = isSelected,
                                onClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                    if (item.route == "settings") {
                                        settingsRotationTarget += 360f
                                    }
                                    if (item.route == "home") {
                                        homeViewModel.triggerHomeAnimation()
                                    }
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding),
                enterTransition = {
                    val tabs = listOf("home", "surahs", "tracker", "settings")
                    val initialIndex = tabs.indexOf(initialState.destination.route)
                    val targetIndex = tabs.indexOf(targetState.destination.route)

                    if (initialIndex != -1 && targetIndex != -1 && initialIndex != targetIndex) {
                        slideInHorizontally(
                            initialOffsetX = { if (targetIndex > initialIndex) it else -it },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                    }
                },
                exitTransition = {
                    val tabs = listOf("home", "surahs", "tracker", "settings")
                    val initialIndex = tabs.indexOf(initialState.destination.route)
                    val targetIndex = tabs.indexOf(targetState.destination.route)

                    if (initialIndex != -1 && targetIndex != -1 && initialIndex != targetIndex) {
                        slideOutHorizontally(
                            targetOffsetX = { if (targetIndex > initialIndex) -it else it },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                    } else {
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                    }
                },
                popEnterTransition = {
                    val tabs = listOf("home", "surahs", "tracker", "settings")
                    val initialIndex = tabs.indexOf(initialState.destination.route)
                    val targetIndex = tabs.indexOf(targetState.destination.route)

                    if (initialIndex != -1 && targetIndex != -1 && initialIndex != targetIndex) {
                        slideInHorizontally(
                            initialOffsetX = { if (targetIndex > initialIndex) it else -it },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                    }
                },
                popExitTransition = {
                    val tabs = listOf("home", "surahs", "tracker", "settings")
                    val initialIndex = tabs.indexOf(initialState.destination.route)
                    val targetIndex = tabs.indexOf(targetState.destination.route)

                    if (initialIndex != -1 && targetIndex != -1 && initialIndex != targetIndex) {
                        slideOutHorizontally(
                            targetOffsetX = { if (targetIndex > initialIndex) -it else it },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                    } else {
                        slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                    }
                }
            ) {                composable("home") {
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
                composable("tracker") {
                    TrackerScreen()
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
    }
}
