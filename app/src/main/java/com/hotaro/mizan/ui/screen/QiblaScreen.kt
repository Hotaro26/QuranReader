package com.hotaro.mizan.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.StayCurrentPortrait
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.hotaro.mizan.LocalFloatingNavBarPadding
import com.hotaro.mizan.R
import com.hotaro.mizan.ui.components.RoundedPullRefreshPage
import com.hotaro.mizan.ui.viewmodel.QiblaViewModel
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    onBackClick: () -> Unit,
    viewModel: QiblaViewModel = hiltViewModel()
) {
    val compassHeading by viewModel.compassHeading.collectAsState()
    val qiblaDirection by viewModel.qiblaDirection.collectAsState()
    val pitch by viewModel.pitch.collectAsState()
    val roll by viewModel.roll.collectAsState()
    val isLevel by viewModel.isLevel.collectAsState()
    val distanceToKaabaKm by viewModel.distanceToKaabaKm.collectAsState()
    val currentCity by viewModel.currentCity.collectAsState()
    val currentCoordinates by viewModel.currentCoordinates.collectAsState()
    val sensorAccuracy by viewModel.sensorAccuracy.collectAsState()
    val hasLocation by viewModel.hasLocation.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val context = LocalContext.current
    val navBarPadding = LocalFloatingNavBarPadding.current
    var showCalibrationSheet by remember { mutableStateOf(false) }

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

    // Angle delta & alignment calculation
    val diff = ((qiblaDirection - compassHeading + 540f) % 360f) - 180f
    val isAligned = abs(diff) <= 3.5f
    val turnDegrees = abs(diff).toInt()
    val isTurnRight = diff > 0f

    // Haptic feedback on locking onto Qibla
    val haptic = LocalHapticFeedback.current
    val vibrator = remember {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            null
        }
    }
    var wasAligned by remember { mutableStateOf(false) }
    LaunchedEffect(isAligned) {
        if (isAligned && !wasAligned) {
            try {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            } catch (e: Exception) {
                // Ignore view haptic failure
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(40)
                }
            } catch (e: Exception) {
                // Ignore vibrator permission failure
            }
        }
        wasAligned = isAligned
    }

    // Continuous shortest delta animation for smooth dial movement
    var targetRotation by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(compassHeading) {
        var delta = (compassHeading - targetRotation) % 360f
        if (delta > 180f) delta -= 360f
        if (delta < -180f) delta += 360f
        targetRotation += delta
    }
    val animatedHeading by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 320f),
        label = "animatedHeading"
    )

    RoundedPullRefreshPage(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Qibla Finder",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (currentCity.isNotEmpty()) currentCity else "Facing Holy Kaaba, Makkah",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(40.dp)
                    ) {
                        IconButton(onClick = { showCalibrationSheet = true }) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Calibration Info",
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
            if (!hasPermission) {
                // Permission Request Card
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(40.dp))
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "Precise Location Needed",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Mizan calculates the exact geometric bearing towards the Kaaba in Makkah based on your current latitude and longitude.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(28.dp))
                    Button(
                        onClick = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth(0.85f),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Enable Location", fontWeight = FontWeight.Bold)
                    }
                }
            } else if (!hasLocation) {
                // Acquiring Location State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        "Acquiring Kaaba Coordinates...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Calibrating GPS and internal compass sensor",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Full M3 Expressive Qibla Finder
                LazyColumn(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 8.dp,
                        bottom = navBarPadding + 32.dp
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Hero Alignment Feedback Card
                    item {
                        HeroAlignmentCard(
                            isAligned = isAligned,
                            turnDegrees = turnDegrees,
                            isTurnRight = isTurnRight,
                            isLevel = isLevel
                        )
                    }

                    // Main Expressive Compass Dial
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ExpressiveCompassDial(
                                heading = animatedHeading,
                                qiblaBearing = qiblaDirection,
                                isAligned = isAligned,
                                isLevel = isLevel,
                                pitch = pitch,
                                roll = roll,
                                modifier = Modifier.size(310.dp)
                            )
                        }
                    }

                    // Leveling Alert Pill (shows if tilted)
                    if (!isLevel) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f),
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.StayCurrentPortrait,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        "Hold device flat for best compass accuracy",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // Contextual Data Cards Grid
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Distance Card
                            InfoDataCard(
                                iconRes = R.drawable.ic_kaaba,
                                title = "Holy Kaaba",
                                value = if (distanceToKaabaKm > 0) "$distanceToKaabaKm km" else "Calculating...",
                                subtitle = "Distance to Makkah",
                                modifier = Modifier.weight(1f)
                            )
                            // Qibla Angle Card
                            InfoDataCard(
                                icon = Icons.Default.Explore,
                                title = "Qibla Bearing",
                                value = "${qiblaDirection.toInt()}° ${getCardinalDirection(qiblaDirection)}",
                                subtitle = "From True North",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Location & Accuracy Banner
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        if (currentCity.isNotEmpty()) currentCity else "GPS Coordinates",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        if (currentCoordinates.isNotEmpty()) currentCoordinates else "21.4225° N, 39.8262° E",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                // Calibration indicator
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (sensorAccuracy >= 2) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.errorContainer,
                                    contentColor = if (sensorAccuracy >= 2) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.clickable { showCalibrationSheet = true }
                                ) {
                                    Text(
                                        text = if (sensorAccuracy >= 2) "Accurate" else "Calibrate",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Compass Calibration Modal Bottom Sheet
    if (showCalibrationSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCalibrationSheet = false },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ScreenRotation, contentDescription = null, modifier = Modifier.size(28.dp))
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "Calibrate Compass Sensor",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Smartphones use internal magnetic sensors that can be affected by metal objects, electronics, or magnetic phone covers. To ensure pinpoint accuracy:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text("1. ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("Wave your phone in a figure-8 motion in the air 3 to 4 times.", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(verticalAlignment = Alignment.Top) {
                            Text("2. ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("Keep away from laptops, metallic tables, or strong magnetic fields.", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(verticalAlignment = Alignment.Top) {
                            Text("3. ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("Hold the phone flat in the palm of your hand.", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { showCalibrationSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Got it", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun HeroAlignmentCard(
    isAligned: Boolean,
    turnDegrees: Int,
    isTurnRight: Boolean,
    isLevel: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isAligned) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val cardColor by animateColorAsState(
        targetValue = if (isAligned) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        animationSpec = tween(350),
        label = "cardColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isAligned) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(350),
        label = "contentColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isAligned) 6.dp else 0.dp, RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor,
            contentColor = contentColor
        ),
        border = if (isAligned) CardDefaults.outlinedCardBorder().copy(width = 2.dp, brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary))) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isAligned) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(22.dp))
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Facing the Kaaba",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            "Aligned perfectly with Qibla in Makkah",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!isTurnRight) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                            }
                            Text(
                                "Turn $turnDegrees° ${if (isTurnRight) "Right" else "Left"}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            if (isTurnRight) {
                                Spacer(Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    Text(
                        "to align with Qibla",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpressiveCompassDial(
    heading: Float,
    qiblaBearing: Float,
    isAligned: Boolean,
    isLevel: Boolean,
    pitch: Float,
    roll: Float,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val surfaceContainer = MaterialTheme.colorScheme.surfaceContainer
    val surfaceContainerHigh = MaterialTheme.colorScheme.surfaceContainerHigh
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    val goldColor = Color(0xFFE5C158)
    val scarletRed = Color(0xFFE53935)

    val qiblaRelativeAngle = qiblaBearing - heading

    Box(
        modifier = modifier
            .shadow(12.dp, CircleShape)
            .clip(CircleShape)
            .background(surfaceContainer)
            .border(
                width = if (isAligned) 4.dp else 1.5.dp,
                color = if (isAligned) primaryColor else outlineVariant.copy(alpha = 0.6f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width / 2f

            // 1. Draw Compass Dial Rim with degree ticks and cardinal points
            rotate(-heading, pivot = center) {
                val tickPaint = Paint().apply {
                    color = onSurfaceVariant.copy(alpha = 0.7f).toArgb()
                    strokeWidth = 2.dp.toPx()
                    isAntiAlias = true
                }
                val majorTickPaint = Paint().apply {
                    color = onSurface.toArgb()
                    strokeWidth = 3.dp.toPx()
                    isAntiAlias = true
                }
                val textPaint = Paint().apply {
                    color = onSurfaceVariant.toArgb()
                    textSize = 11.sp.toPx()
                    textAlign = Paint.Align.CENTER
                    typeface = Typeface.DEFAULT_BOLD
                    isAntiAlias = true
                }
                val cardinalPaint = Paint().apply {
                    color = onSurface.toArgb()
                    textSize = 15.sp.toPx()
                    textAlign = Paint.Align.CENTER
                    typeface = Typeface.DEFAULT_BOLD
                    isAntiAlias = true
                }
                val northPaint = Paint().apply {
                    color = scarletRed.toArgb()
                    textSize = 17.sp.toPx()
                    textAlign = Paint.Align.CENTER
                    typeface = Typeface.DEFAULT_BOLD
                    isAntiAlias = true
                }

                for (deg in 0 until 360 step 5) {
                    val angleRad = Math.toRadians(deg.toDouble() - 90.0)
                    val cosA = cos(angleRad).toFloat()
                    val sinA = sin(angleRad).toFloat()

                    when {
                        deg == 0 -> {
                            // North Cardinal
                            val textX = center.x + (radius - 28.dp.toPx()) * cosA
                            val textY = center.y + (radius - 28.dp.toPx()) * sinA + 6.dp.toPx()
                            rotate(heading, pivot = Offset(textX, textY - 6.dp.toPx())) {
                                drawContext.canvas.nativeCanvas.drawText("N", textX, textY, northPaint)
                            }
                            // North Accent tick
                            val startX = center.x + (radius - 14.dp.toPx()) * cosA
                            val startY = center.y + (radius - 14.dp.toPx()) * sinA
                            val endX = center.x + radius * cosA
                            val endY = center.y + radius * sinA
                            drawLine(color = scarletRed, start = Offset(startX, startY), end = Offset(endX, endY), strokeWidth = 4.dp.toPx())
                        }
                        deg == 90 -> {
                            val textX = center.x + (radius - 26.dp.toPx()) * cosA
                            val textY = center.y + (radius - 26.dp.toPx()) * sinA + 5.dp.toPx()
                            rotate(heading, pivot = Offset(textX, textY - 5.dp.toPx())) {
                                drawContext.canvas.nativeCanvas.drawText("E", textX, textY, cardinalPaint)
                            }
                        }
                        deg == 180 -> {
                            val textX = center.x + (radius - 26.dp.toPx()) * cosA
                            val textY = center.y + (radius - 26.dp.toPx()) * sinA + 5.dp.toPx()
                            rotate(heading, pivot = Offset(textX, textY - 5.dp.toPx())) {
                                drawContext.canvas.nativeCanvas.drawText("S", textX, textY, cardinalPaint)
                            }
                        }
                        deg == 270 -> {
                            val textX = center.x + (radius - 26.dp.toPx()) * cosA
                            val textY = center.y + (radius - 26.dp.toPx()) * sinA + 5.dp.toPx()
                            rotate(heading, pivot = Offset(textX, textY - 5.dp.toPx())) {
                                drawContext.canvas.nativeCanvas.drawText("W", textX, textY, cardinalPaint)
                            }
                        }
                        deg % 30 == 0 -> {
                            // Major tick
                            val startX = center.x + (radius - 12.dp.toPx()) * cosA
                            val startY = center.y + (radius - 12.dp.toPx()) * sinA
                            val endX = center.x + radius * cosA
                            val endY = center.y + radius * sinA
                            drawContext.canvas.nativeCanvas.drawLine(startX, startY, endX, endY, majorTickPaint)

                            // Degree numeral
                            val textX = center.x + (radius - 24.dp.toPx()) * cosA
                            val textY = center.y + (radius - 24.dp.toPx()) * sinA + 4.dp.toPx()
                            rotate(heading, pivot = Offset(textX, textY - 4.dp.toPx())) {
                                drawContext.canvas.nativeCanvas.drawText("$deg°", textX, textY, textPaint)
                            }
                        }
                        deg % 15 == 0 -> {
                            // Medium tick
                            val startX = center.x + (radius - 8.dp.toPx()) * cosA
                            val startY = center.y + (radius - 8.dp.toPx()) * sinA
                            val endX = center.x + radius * cosA
                            val endY = center.y + radius * sinA
                            drawContext.canvas.nativeCanvas.drawLine(startX, startY, endX, endY, tickPaint)
                        }
                        else -> {
                            // Minor tick
                            val startX = center.x + (radius - 4.dp.toPx()) * cosA
                            val startY = center.y + (radius - 4.dp.toPx()) * sinA
                            val endX = center.x + radius * cosA
                            val endY = center.y + radius * sinA
                            drawContext.canvas.nativeCanvas.drawLine(startX, startY, endX, endY, tickPaint)
                        }
                    }
                }

                // Magnetic North needle (sleek red diamond pointing to North)
                val needlePath = Path().apply {
                    moveTo(center.x, center.y - radius + 42.dp.toPx())
                    lineTo(center.x - 7.dp.toPx(), center.y - radius + 75.dp.toPx())
                    lineTo(center.x, center.y - radius + 70.dp.toPx())
                    close()
                }
                drawPath(needlePath, color = scarletRed)

                val needleRight = Path().apply {
                    moveTo(center.x, center.y - radius + 42.dp.toPx())
                    lineTo(center.x + 7.dp.toPx(), center.y - radius + 75.dp.toPx())
                    lineTo(center.x, center.y - radius + 70.dp.toPx())
                    close()
                }
                drawPath(needleRight, color = scarletRed.copy(alpha = 0.75f))
            }

            // 2. Draw Golden Qibla Needle pointing directly to Kaaba
            rotate(qiblaRelativeAngle, pivot = center) {
                // Outer Qibla target glow arc
                drawArc(
                    brush = Brush.radialGradient(
                        colors = listOf(goldColor.copy(alpha = 0.9f), Color.Transparent),
                        center = Offset(center.x, center.y - radius + 12.dp.toPx()),
                        radius = 24.dp.toPx()
                    ),
                    startAngle = 250f,
                    sweepAngle = 40f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius + 14.dp.toPx(), center.y - radius + 14.dp.toPx()),
                    size = Size((radius - 14.dp.toPx()) * 2f, (radius - 14.dp.toPx()) * 2f),
                    style = Stroke(width = 5.dp.toPx())
                )

                // Golden Qibla Arrow Pointer
                val arrowHead = Path().apply {
                    moveTo(center.x, center.y - radius + 10.dp.toPx())
                    lineTo(center.x - 14.dp.toPx(), center.y - radius + 42.dp.toPx())
                    lineTo(center.x, center.y - radius + 34.dp.toPx())
                    close()
                }
                drawPath(arrowHead, color = goldColor)

                val arrowHeadRight = Path().apply {
                    moveTo(center.x, center.y - radius + 10.dp.toPx())
                    lineTo(center.x + 14.dp.toPx(), center.y - radius + 42.dp.toPx())
                    lineTo(center.x, center.y - radius + 34.dp.toPx())
                    close()
                }
                drawPath(arrowHeadRight, color = goldColor.copy(alpha = 0.75f))

                // Needle Shaft
                drawLine(
                    color = goldColor,
                    start = Offset(center.x, center.y - radius + 34.dp.toPx()),
                    end = Offset(center.x, center.y - 40.dp.toPx()),
                    strokeWidth = 3.dp.toPx()
                )
            }
        }

        // 3. Center Metallic Puck (Live Heading & Kaaba Icon or Leveling Bubble)
        Surface(
            shape = CircleShape,
            color = surfaceContainerHigh,
            shadowElevation = 8.dp,
            modifier = Modifier
                .size(92.dp)
                .border(
                    width = 2.dp,
                    color = if (isAligned) goldColor else outlineVariant.copy(alpha = 0.5f),
                    shape = CircleShape
                )
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (!isLevel) {
                    // Leveling Bubble Crosshair
                    val bubbleX = (roll.coerceIn(-25f, 25f) / 25f) * 22.dp.value
                    val bubbleY = (pitch.coerceIn(-25f, 25f) / 25f) * 22.dp.value
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .offset(x = bubbleX.dp, y = bubbleY.dp)
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.7f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .border(1.5.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), CircleShape)
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_kaaba),
                            contentDescription = "Kaaba",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(Modifier.height(2.dp))
                        val displayAngle = ((heading % 360f + 360f) % 360f).toInt()
                        Text(
                            text = "$displayAngle°",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoDataCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconRes: Int? = null,
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (iconRes != null) {
                            Icon(painter = painterResource(id = iconRes), contentDescription = null, tint = Color.Unspecified, modifier = Modifier.size(18.dp))
                        } else if (icon != null) {
                            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

private fun getCardinalDirection(deg: Float): String {
    val directions = listOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
    val index = (((deg % 360f + 360f) % 360f + 11.25f) / 22.5f).toInt() % 16
    return directions[index]
}
