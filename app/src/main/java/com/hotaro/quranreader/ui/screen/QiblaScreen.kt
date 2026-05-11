package com.hotaro.quranreader.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.hotaro.quranreader.ui.viewmodel.QiblaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    onBackClick: () -> Unit,
    viewModel: QiblaViewModel = hiltViewModel()
) {
    val compassHeading by viewModel.compassHeading.collectAsState()
    val qiblaDirection by viewModel.qiblaDirection.collectAsState()
    val hasLocation by viewModel.hasLocation.collectAsState()

    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (hasPermission) {
            viewModel.fetchLocation()
        }
    }

    DisposableEffect(Unit) {
        viewModel.startSensors()
        if (hasPermission) {
            viewModel.fetchLocation()
        }
        onDispose {
            viewModel.stopSensors()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Qibla Finder") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (!hasPermission) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Location permission is required to find the Qibla.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }) {
                        Text("Grant Permission")
                    }
                }
            } else if (!hasLocation) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Acquiring location...")
                }
            } else {
                Compass(
                    heading = compassHeading,
                    qiblaDirection = qiblaDirection
                )
            }
        }
    }
}

@Composable
fun Compass(heading: Float, qiblaDirection: Float) {
    val qiblaAngle = qiblaDirection - heading

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            val primaryColor = MaterialTheme.colorScheme.primary
            val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
            val onSurface = MaterialTheme.colorScheme.onSurface

            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width / 2

                drawCircle(
                    color = surfaceVariant,
                    radius = radius,
                    center = center
                )

                // Draw North (relative to the compass housing)
                rotate(-heading) {
                    val path = Path().apply {
                        moveTo(center.x, center.y - radius + 20f)
                        lineTo(center.x - 20f, center.y)
                        lineTo(center.x + 20f, center.y)
                        close()
                    }
                    drawPath(path, color = Color.Red)
                    
                    val pathSouth = Path().apply {
                        moveTo(center.x, center.y + radius - 20f)
                        lineTo(center.x - 20f, center.y)
                        lineTo(center.x + 20f, center.y)
                        close()
                    }
                    drawPath(pathSouth, color = onSurface)
                }

                // Draw Qibla Pointer
                rotate(qiblaAngle) {
                    val pathQibla = Path().apply {
                        moveTo(center.x, center.y - radius + 10f)
                        lineTo(center.x - 10f, center.y - radius + 60f)
                        lineTo(center.x + 10f, center.y - radius + 60f)
                        close()
                    }
                    drawPath(pathQibla, color = primaryColor)
                    drawCircle(color = primaryColor, radius = 15f, center = center)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "${heading.toInt()}°",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Qibla: ${qiblaDirection.toInt()}°",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
