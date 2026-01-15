package com.example.timetableapp.generatetable

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timetableapp.TimetableEntry

@Composable
fun GenerateTimetableInterface(
    onBackClick: () -> Unit,
    existingSchedule: List<TimetableEntry>,
    onAddEntries: (List<TimetableEntry>) -> Unit
) {
    val inputBgColor = Color(0xFFF5E6D3)
    val buttonGold = Color(0xFFB58B43)
    val context = LocalContext.current

    // --- STATES ---
    var currentStep by remember { mutableIntStateOf(0) }
    var selectedSubject by remember { mutableStateOf("Select Subject") }
    var selectedIconRes by remember { mutableStateOf<Int?>(null) }
    var teacherName by remember { mutableStateOf("") }

    val isSpecialEvent = selectedSubject.uppercase() == "REHAT" || selectedSubject.uppercase() == "PERHIMPUNAN"

    val daysList = listOf("SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY")
    var selectedDay by remember { mutableStateOf("") }
    val selectedTimes = remember { mutableStateListOf<String>() }

    // The Queue: Holds entries temporarily so you can add Sunday and Tuesday in one shot
    val queuedEntries = remember { mutableStateListOf<TimetableEntry>() }

    val timeSlots = listOf(
        "7.30-8.00", "8.00-8.30", "8.30-9.00", "9.00-9.30", "9.30-10.00", "10.00-10.30",
        "10.30-11.00", "11.00-11.30", "11.30-12.00", "12.00-12.30", "12.30-1.00"
    )

    fun isSlotAvailable(day: String, time: String): Boolean {
        if (day.isEmpty()) return true
        val inDatabase = existingSchedule.any { it.dayAndTime == "$day $time" }
        val inQueue = queuedEntries.any { it.dayAndTime == "$day $time" }
        return !inDatabase && !inQueue
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentStep) {
            // STEP 1: Select Subject (From your external SubjectView)
            1 -> SubjectListScreen(
                onSubjectSelected = { subject ->
                    selectedSubject = subject
                    if (subject.uppercase() == "REHAT" || subject.uppercase() == "PERHIMPUNAN") {
                        teacherName = "-"
                    } else if (teacherName == "-") {
                        teacherName = ""
                    }
                    currentStep = 2
                },
                onBack = { currentStep = 0 }
            )

            // STEP 2: Icon Picker (From your external Icon picker)
            2 -> IconPickerScreen(
                subjectName = selectedSubject,
                onIconSelected = { selectedIconRes = it; currentStep = 0 },
                onBack = { currentStep = 1 }
            )

            // STEP 10: SCHEDULING SCREEN
            10 -> {
                Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                    Spacer(Modifier.height(40.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { currentStep = 0 }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                        }
                        Text("Set Time: $selectedSubject", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }

                    Text("1. Select Day", color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(top = 10.dp))
                    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        daysList.forEach { day ->
                            DayChip(day.substring(0,3), selectedDay == day, Modifier.weight(1f)) {
                                selectedDay = day
                                selectedTimes.clear()
                            }
                        }
                    }

                    if (selectedDay.isNotEmpty()) {
                        Text("2. Pick Times for $selectedDay", color = Color.White, fontSize = 14.sp)
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.height(180.dp).padding(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(timeSlots) { time ->
                                val available = isSlotAvailable(selectedDay, time)
                                TimeChip(time, selectedTimes.contains(time), available) {
                                    if (available) {
                                        if (selectedTimes.contains(time)) selectedTimes.remove(time) else selectedTimes.add(time)
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (selectedTimes.isNotEmpty()) {
                                    selectedTimes.forEach { time ->
                                        queuedEntries.add(TimetableEntry(
                                            subject = selectedSubject,
                                            lecturer = teacherName,
                                            duration = time,
                                            dayAndTime = "$selectedDay $time",
                                            iconRes = selectedIconRes
                                        ))
                                    }
                                    selectedTimes.clear()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.Add, null)
                            Spacer(Modifier.width(8.dp))
                            Text("ADD TO QUEUE")
                        }
                    }

                    Spacer(Modifier.height(15.dp))
                    Text("Items in Queue:", color = Color.White, fontSize = 14.sp)

                    Box(modifier = Modifier.weight(1f).fillMaxWidth().background(Color.Black.copy(0.3f), RoundedCornerShape(8.dp)).padding(8.dp)) {
                        if (queuedEntries.isEmpty()) {
                            Text("No slots added", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
                        } else {
                            LazyColumn {
                                items(queuedEntries) { entry ->
                                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text("${entry.dayAndTime}", color = Color.White, modifier = Modifier.weight(1f), fontSize = 14.sp)
                                        IconButton(onClick = { queuedEntries.remove(entry) }) {
                                            Icon(Icons.Default.Delete, null, tint = Color(0xFFFF5252))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            onAddEntries(queuedEntries.toList())
                            Toast.makeText(context, "Timetable Updated!", Toast.LENGTH_SHORT).show()
                            currentStep = 0
                            queuedEntries.clear()
                            selectedSubject = "Select Subject"; teacherName = ""
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(55.dp),
                        enabled = queuedEntries.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonGold)
                    ) {
                        Icon(Icons.Default.Check, null); Spacer(Modifier.width(10.dp)); Text("SAVE EVERYTHING")
                    }
                }
            }

            // STEP 0: INITIAL INFO
            else -> {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 25.dp)) {
                    Spacer(Modifier.height(50.dp))
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White) }
                    Text("New Entry", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)

                    Spacer(Modifier.height(20.dp))
                    InputField("Subject", selectedSubject, true, inputBgColor) { currentStep = 1 }

                    Spacer(Modifier.height(10.dp))
                    Text("Teacher Name", color = Color.White)
                    TextField(
                        value = teacherName,
                        onValueChange = { if (!isSpecialEvent) teacherName = it },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        enabled = !isSpecialEvent,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = if (isSpecialEvent) Color.Gray.copy(0.3f) else inputBgColor,
                            unfocusedContainerColor = if (isSpecialEvent) Color.Gray.copy(0.3f) else inputBgColor,
                            disabledContainerColor = Color.Gray.copy(0.2f),
                            disabledTextColor = Color.White.copy(0.6f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(Modifier.height(40.dp))
                    Button(
                        onClick = {
                            if (selectedSubject != "Select Subject" && (isSpecialEvent || teacherName.isNotBlank())) {
                                currentStep = 10
                            } else {
                                Toast.makeText(context, "Please complete the form", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonGold)
                    ) {
                        Text("NEXT: SELECT TIMES", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- REUSABLE COMPONENTS ---

@Composable
fun DayChip(day: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Color(0xFFB58B43) else Color.White,
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(day, color = if (isSelected) Color.White else Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun TimeChip(time: String, isSelected: Boolean, isAvailable: Boolean, onClick: () -> Unit) {
    val color = when {
        !isAvailable -> Color.Gray.copy(0.4f)
        isSelected -> Color(0xFFB58B43)
        else -> Color(0xFFF5E6D3)
    }
    Surface(
        onClick = { if (isAvailable) onClick() },
        modifier = Modifier.fillMaxWidth().height(40.dp),
        shape = RoundedCornerShape(8.dp),
        color = color
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(if (isAvailable) time else "TAKEN", color = if (isAvailable) Color.Black else Color.White, fontSize = 11.sp)
        }
    }
}

@Composable
fun InputField(label: String, value: String, isDropdown: Boolean, bgColor: Color, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, color = Color.White, fontSize = 14.sp)
        Surface(modifier = Modifier.fillMaxWidth().height(50.dp).clickable { onClick() }, shape = RoundedCornerShape(8.dp), color = bgColor) {
            Box(Modifier.padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart) {
                Text(value, color = Color.Black)
                if (isDropdown) Icon(Icons.Default.KeyboardArrowDown, null, Modifier.align(Alignment.CenterEnd))
            }
        }
    }
}