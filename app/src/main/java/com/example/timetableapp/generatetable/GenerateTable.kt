package com.example.timetableapp.generatetable

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

    var currentStep by remember { mutableIntStateOf(0) }
    var selectedSubject by remember { mutableStateOf("Select Subject") }
    var selectedIconRes by remember { mutableStateOf<Int?>(null) }
    var teacherName by remember { mutableStateOf("") }

    val daysList = listOf("SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY")
    val selectedDays = remember { mutableStateListOf<String>() }

    val timeSlots = listOf(
        "7.30-8.00", "8.00-8.30", "8.30-9.00", "9.00-9.30", "9.30-10.00", "10.00-10.30",
        "10.30-11.00", "11.00-11.30", "11.30-12.00", "12.00-12.30", "12.30-1.00"
    )
    val selectedTimes = remember { mutableStateListOf<String>() }

    // Logic to check if subject is special (No teacher needed)
    val isSpecialSubject = selectedSubject.uppercase() == "REHAT" ||
            selectedSubject.uppercase() == "PERHIMPUNAN"

    fun isSlotAvailable(time: String): Boolean {
        if (selectedDays.isEmpty()) return true
        val occupiedDaysCount = selectedDays.count { day ->
            existingSchedule.any { entry ->
                val entryParts = entry.dayAndTime.split(" ")
                val entryDay = entryParts.firstOrNull()?.uppercase()?.trim() ?: ""
                val entryTime = entryParts.lastOrNull()?.trim() ?: ""
                val d = day.uppercase().trim()
                val isSameDay = (d == entryDay || d.startsWith(entryDay) || entryDay.startsWith(d))
                isSameDay && entryTime == time
            }
        }
        return occupiedDaysCount < selectedDays.size
    }

    fun resetForm() {
        selectedSubject = "Select Subject"
        selectedIconRes = null
        teacherName = ""
        selectedDays.clear()
        selectedTimes.clear()
        currentStep = 0
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentStep) {
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
            2 -> IconPickerScreen(
                subjectName = selectedSubject,
                onIconSelected = { selectedIconRes = it; currentStep = 0 },
                onBack = { currentStep = 1 }
            )
            10 -> {
                Column(modifier = Modifier.fillMaxSize().padding(25.dp)) {
                    Spacer(Modifier.height(50.dp))
                    Text("Schedule for $selectedSubject", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)

                    Spacer(Modifier.height(20.dp))
                    Text("1. Select Days:", color = Color.White, fontSize = 14.sp)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 10.dp)) {
                        daysList.chunked(3).forEach { rowDays ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowDays.forEach { day ->
                                    DayChip(day, selectedDays.contains(day), modifier = Modifier.weight(1f)) {
                                        if (selectedDays.contains(day)) selectedDays.remove(day)
                                        else selectedDays.add(day)
                                    }
                                }
                                if (rowDays.size < 3) Spacer(modifier = Modifier.weight((3 - rowDays.size).toFloat()))
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Text("2. Select Times:", color = Color.White, fontSize = 14.sp)

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f).padding(vertical = 10.dp)
                    ) {
                        items(timeSlots) { time ->
                            val available = isSlotAvailable(time)
                            TimeChip(time, selectedTimes.contains(time), available) {
                                if (available) {
                                    if (selectedTimes.contains(time)) selectedTimes.remove(time)
                                    else selectedTimes.add(time)
                                }
                            }
                        }
                    }

                    GenerateActionButton("SAVE AND GENERATE", Icons.Default.Check, buttonGold) {
                        if (selectedDays.isNotEmpty() && selectedTimes.isNotEmpty()) {
                            val newEntries = mutableListOf<TimetableEntry>()
                            selectedDays.forEach { day ->
                                selectedTimes.forEach { time ->
                                    val isFree = !existingSchedule.any { entry ->
                                        val parts = entry.dayAndTime.split(" ")
                                        val eDay = parts.firstOrNull()?.uppercase()?.trim() ?: ""
                                        val eTime = parts.lastOrNull()?.trim() ?: ""
                                        (day.uppercase().trim() == eDay) && eTime == time
                                    }
                                    if (isFree) {
                                        newEntries.add(
                                            TimetableEntry(
                                                subject = selectedSubject,
                                                lecturer = teacherName,
                                                duration = time,
                                                dayAndTime = "$day $time",
                                                iconRes = selectedIconRes
                                            )
                                        )
                                    }
                                }
                            }
                            if (newEntries.isNotEmpty()) {
                                onAddEntries(newEntries)
                                Toast.makeText(context, "Saved ${newEntries.size} slots!", Toast.LENGTH_SHORT).show()
                                resetForm()
                            } else {
                                Toast.makeText(context, "Slots taken!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Pick day and time", Toast.LENGTH_SHORT).show()
                        }
                    }

                    TextButton(onClick = { currentStep = 0 }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Back to Info", color = Color.White)
                    }
                }
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 25.dp)) {
                    Spacer(Modifier.height(50.dp))
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White) }
                    Text("Subject Details", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(20.dp))

                    InputField("Subject", selectedSubject, true, inputBgColor) { currentStep = 1 }

                    Text("Teacher Name", color = Color.White, modifier = Modifier.padding(top = 10.dp))
                    TextField(
                        value = teacherName,
                        onValueChange = { if (!isSpecialSubject) teacherName = it },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        enabled = !isSpecialSubject,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = inputBgColor,
                            unfocusedContainerColor = inputBgColor,
                            disabledContainerColor = Color.Gray.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    Spacer(Modifier.height(40.dp))
                    Button(
                        onClick = {
                            if (selectedSubject != "Select Subject" && teacherName.isNotBlank()) currentStep = 10
                            else Toast.makeText(context, "Fill details", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonGold)
                    ) {
                        Text("NEXT", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- HELPERS (Keep these the same) ---
@Composable
fun DayChip(day: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier.height(45.dp).background(if (isSelected) Color(0xFFB58B43) else Color.White, RoundedCornerShape(8.dp))
            .clickable { onClick() }.border(1.dp, if (isSelected) Color.White else Color.Black, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) { Text(text = day, color = if (isSelected) Color.White else Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
}

@Composable
fun TimeChip(time: String, isSelected: Boolean, isAvailable: Boolean, onClick: () -> Unit) {
    val bgColor = when { !isAvailable -> Color.Gray.copy(alpha = 0.5f); isSelected -> Color(0xFFB58B43); else -> Color(0xFFF5E6D3) }
    Box(
        modifier = Modifier.fillMaxWidth().height(45.dp).background(bgColor, RoundedCornerShape(8.dp))
            .clickable(enabled = isAvailable) { onClick() }.border(if (isSelected) 1.dp else 0.dp, Color.White, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) { Text(text = if (isAvailable) time else "TAKEN", fontSize = 12.sp, color = if (isSelected || !isAvailable) Color.White else Color.Black) }
}

@Composable
fun GenerateActionButton(text: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(55.dp), colors = ButtonDefaults.buttonColors(containerColor = color), shape = RoundedCornerShape(8.dp)) {
        Icon(icon, null); Spacer(Modifier.width(10.dp)); Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun InputField(label: String, value: String, isDropdown: Boolean, bgColor: Color, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(label, color = Color.White, fontSize = 14.sp)
        Surface(modifier = Modifier.fillMaxWidth().height(50.dp).clickable { onClick() }, shape = RoundedCornerShape(8.dp), color = bgColor) {
            Box(Modifier.padding(horizontal = 15.dp), contentAlignment = Alignment.CenterStart) {
                Text(value, color = Color.Black)
                if (isDropdown) Icon(Icons.Default.KeyboardArrowDown, null, Modifier.align(Alignment.CenterEnd))
            }
        }
    }
}