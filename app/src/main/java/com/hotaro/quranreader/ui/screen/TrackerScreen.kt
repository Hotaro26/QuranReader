package com.hotaro.quranreader.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hotaro.quranreader.data.model.RamadanDay
import com.hotaro.quranreader.data.model.Todo
import com.hotaro.quranreader.data.remote.WeatherDayDto
import com.hotaro.quranreader.ui.viewmodel.TrackerViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TrackerScreen(
    viewModel: TrackerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddTodoDialog by remember { mutableStateOf(false) }
    var newTodoTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tracker", style = MaterialTheme.typography.headlineLarge) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTodoDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Todo")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 840.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        WeatherCard(
                            weather = uiState.weather,
                            modifier = Modifier.weight(1f)
                        )
                        DateCard(modifier = Modifier.weight(1f))
                    }
                }

                item {
                    RamadanCountdownCard(
                        daysLeft = uiState.daysToRamadan,
                        isRamadanModeActive = uiState.ramadanModeActive,
                        onModeToggle = { viewModel.setRamadanModeActive(it) },
                        selectedRegion = uiState.selectedRegion,
                        onRegionChange = { viewModel.setRamadanRegion(it) }
                    )
                }

                if (uiState.ramadanModeActive) {
                    item {
                        Text(
                            text = "Ramadan Daily Tracker",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(uiState.ramadanDays) { day ->
                                RamadanDayCard(
                                    day = day,
                                    onFastedChange = { viewModel.updateRamadanFasted(day.day, it) },
                                    onTaraweehChange = { viewModel.updateRamadanTaraweeh(day.day, it) },
                                    onQuranChange = { viewModel.updateRamadanQuranRead(day.day, it) }
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Monthly Productivity",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    TodoHeatmap(heatmapData = uiState.heatmapData)
                }

                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Today's Todos",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            if (uiState.todos.any { it.isCompleted }) {
                                TextButton(onClick = { viewModel.clearCompletedTodos() }) {
                                    Text("Clear Completed")
                                }
                            }
                        }
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                if (uiState.todos.isEmpty()) {
                                    Text(
                                        text = "No todos yet. Add one to stay productive!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                } else {
                                    uiState.todos.forEach { todo ->
                                        TodoItem(
                                            todo = todo,
                                            onToggle = { viewModel.toggleTodo(todo) },
                                            onDelete = { viewModel.deleteTodo(todo) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (uiState.todoHistory.isNotEmpty()) {
                    item {
                        Text(
                            text = "History",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    uiState.todoHistory.forEach { (date, todos) ->
                        item {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(4.dp)) {
                                    todos.forEach { todo ->
                                        TodoItem(
                                            todo = todo,
                                            onToggle = { viewModel.toggleTodo(todo) },
                                            onDelete = { viewModel.deleteTodo(todo) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (showAddTodoDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAddTodoDialog = false 
                newTodoTitle = ""
            },
            title = { Text("Add Todo") },
            text = {
                OutlinedTextField(
                    value = newTodoTitle,
                    onValueChange = { newTodoTitle = it },
                    label = { Text("What needs to be done?") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addTodo(newTodoTitle)
                        showAddTodoDialog = false
                        newTodoTitle = ""
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddTodoDialog = false 
                    newTodoTitle = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun WeatherCard(
    weather: WeatherDayDto?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Cloud, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Weather", style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.weight(1f))
            if (weather != null) {
                Text(
                    text = "${((weather.max + weather.min) / 2).toInt()}°C",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text("Acquiring...", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun DateCard(
    modifier: Modifier = Modifier
) {
    val date = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).format(Date())
    Card(
        modifier = modifier.height(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.width(8.dp))
                Text("Date", style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = date,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TodoHeatmap(heatmapData: Map<Long, Int>) {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    
    val startOfMonth = calendar.clone() as Calendar
    startOfMonth.set(Calendar.DAY_OF_MONTH, 1)
    
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                maxItemsInEachRow = 7
            ) {
                for (i in 0 until daysInMonth) {
                    val dayCal = startOfMonth.clone() as Calendar
                    dayCal.add(Calendar.DAY_OF_MONTH, i)
                    val date = dayCal.timeInMillis
                    val count = heatmapData[date] ?: 0
                    HeatmapCell(count = count)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Less", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.width(4.dp))
                HeatmapCell(0)
                HeatmapCell(1)
                HeatmapCell(3)
                HeatmapCell(5)
                Spacer(Modifier.width(4.dp))
                Text("More", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun HeatmapCell(count: Int) {
    val color = when {
        count == 0 -> MaterialTheme.colorScheme.surfaceVariant
        count < 2 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        count < 4 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        else -> MaterialTheme.colorScheme.primary
    }
    
    Surface(
        modifier = Modifier.size(24.dp),
        shape = MaterialTheme.shapes.extraSmall,
        color = color
    ) {}
}

@Composable
fun RamadanCountdownCard(
    daysLeft: Long,
    isRamadanModeActive: Boolean,
    onModeToggle: (Boolean) -> Unit,
    selectedRegion: String,
    onRegionChange: (String) -> Unit
) {
    var showRegionMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Countdown to Ramadan 2027", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "$daysLeft Days Left",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    Icons.Default.Event,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Ramadan Mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(checked = isRamadanModeActive, onCheckedChange = onModeToggle)
                }

                Box {
                    AssistChip(
                        onClick = { showRegionMenu = true },
                        label = { Text(selectedRegion) },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                    )
                    DropdownMenu(expanded = showRegionMenu, onDismissRequest = { showRegionMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Middle East") },
                            onClick = { onRegionChange("Middle East"); showRegionMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("South Asia") },
                            onClick = { onRegionChange("South Asia"); showRegionMenu = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RamadanDayCard(
    day: RamadanDay,
    onFastedChange: (Boolean) -> Unit,
    onTaraweehChange: (Boolean) -> Unit,
    onQuranChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.width(160.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Day ${day.day}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            TrackerToggle(
                label = "Fasted",
                isChecked = day.fasted,
                onCheckedChange = onFastedChange,
                icon = Icons.Default.WbSunny
            )
            TrackerToggle(
                label = "Taraweeh",
                isChecked = day.prayedTaraweeh,
                onCheckedChange = onTaraweehChange,
                icon = Icons.Default.Nightlight
            )
            TrackerToggle(
                label = "Quran",
                isChecked = day.quranRead,
                onCheckedChange = onQuranChange,
                icon = Icons.AutoMirrored.Filled.MenuBook
            )
        }
    }
}

@Composable
fun TrackerToggle(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun TodoItem(
    todo: Todo,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggle() }
            )
            Text(
                text = todo.title,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (todo.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                color = if (todo.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Todo",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                )
            }
        }
    }
}
