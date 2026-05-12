package com.hotaro.quranreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
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
import com.hotaro.quranreader.ui.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeMode by themeViewModel.themeMode.collectAsState(initial = 0)
            val colorPalette by themeViewModel.colorPalette.collectAsState(initial = "classic")
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
                    QuranApp()
                }
            }
        }
    }
}

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

@Composable
fun QuranApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp > 600

    val items = listOf(
        BottomNavItem("home", "Home", Icons.Default.Home),
        BottomNavItem("surahs", "Surahs", Icons.Default.Mosque),
        BottomNavItem("tracker", "Tracker", Icons.Default.TaskAlt),
        BottomNavItem("settings", "Settings", Icons.Default.Settings)
    )

    val showNavBar = currentDestination?.route in listOf("home", "surahs", "tracker", "settings")

    Row(modifier = Modifier.fillMaxSize()) {
        if (isWideScreen && showNavBar) {
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.background
            ) {
                Spacer(Modifier.weight(1f))
                items.forEach { item ->
                    NavigationRailItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
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
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
                                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                                onClick = {
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
                modifier = Modifier.padding(innerPadding)
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
