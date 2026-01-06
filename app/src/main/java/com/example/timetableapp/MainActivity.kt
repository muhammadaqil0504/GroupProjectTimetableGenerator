package com.example.timetableapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. CORRECTED IMPORTS: Ensure these match your actual folder names exactly
import com.example.timetableapp.EditTimetable.EditTimetableScreen
import com.example.timetableapp.homepage.HomepageInterface
import com.example.timetableapp.SubjectView.SubjectInterface
import com.example.timetableapp.generatetable.GenerateTimetableInterface
import com.example.timetableapp.TimetableView.TimetableViewScreen

// 2. SHARED DATA CLASS: Defined here once for the whole project
data class TimetableEntry(
    val subject: String,
    val lecturer: String,
    val duration: String,
    val dayAndTime: String,
    val iconRes: Int? = null
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // State for screen navigation
            var currentScreen by remember { mutableStateOf("front") }

            // The main list that holds all your subjects
            val userSchedule = remember { mutableStateListOf<TimetableEntry>() }

            // State for the item you want to edit
            var entryToEdit by remember { mutableStateOf<TimetableEntry?>(null) }

            val chalkboardGreen = Color(0xFF4B6E63)

            Surface(modifier = Modifier.fillMaxSize(), color = chalkboardGreen) {
                when (currentScreen) {
                    "front" -> {
                        TimetableInterface(onGenerateClick = { currentScreen = "home" })
                    }

                    "home" -> {
                        HomepageInterface(
                            onGenerateNavClick = { currentScreen = "generate" },
                            onEditNavClick = {
                                // Selects first entry or a placeholder if list is empty
                                entryToEdit = if (userSchedule.isNotEmpty()) {
                                    userSchedule[0]
                                } else {
                                    TimetableEntry("New Subject", "Lecturer Name", "1.0", "MON 08:00")
                                }
                                currentScreen = "edit"
                            },
                            onSubjectNavClick = { currentScreen = "subject" },
                            onTimetableClick = { currentScreen = "view_timetable" }
                        )
                    }

                    "generate" -> {
                        GenerateTimetableInterface(
                            onBackClick = { currentScreen = "home" },
                            onAddEntry = { newEntry -> userSchedule.add(newEntry) },
                            onGenerateAutomatically = { currentScreen = "view_timetable" }
                        )
                    }

                    "edit" -> {
                        entryToEdit?.let { entry ->
                            EditTimetableScreen(
                                entryToEdit = entry,
                                onUpdateClick = { updated ->
                                    val index = userSchedule.indexOf(entry)
                                    if (index != -1) userSchedule[index] = updated
                                    currentScreen = "home"
                                },
                                onCancelClick = { currentScreen = "home" }
                            )
                        } ?: run { currentScreen = "home" }
                    }

                    "subject" -> {
                        SubjectInterface(onBackClick = { currentScreen = "home" })
                    }

                    "view_timetable" -> {
                        // FIXED: Links to TimetableViewScreen with proper list conversion
                        TimetableViewScreen(
                            scheduleData = userSchedule.toList(),
                            onBackClick = { currentScreen = "home" }
                        )
                    }
                }
            }
        }
    }
}

// 3. GLOBAL COMPOSABLES: Accessible by all other files in your project

@Composable
fun TimetableInterface(onGenerateClick: () -> Unit) {
    val goldText = Color(0xFFC5A368)
    val buttonGold = Color(0xFFB58B43)

    Box(modifier = Modifier.fillMaxSize()) {
        ScallopedHeader()

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.DateRange, null, Modifier.size(100.dp), goldText)
            Spacer(Modifier.height(20.dp))
            Text("TIMETABLE", color = goldText, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text("GENERATOR", color = goldText, fontSize = 18.sp)
            Spacer(Modifier.height(60.dp))

            Button(
                onClick = onGenerateClick,
                modifier = Modifier.fillMaxWidth(0.75f).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonGold),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("GENERATE", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ScallopedHeader() {
    Canvas(modifier = Modifier.fillMaxWidth().height(90.dp)) {
        drawRect(Color.White, size = size.copy(height = size.height * 0.4f))
        val circleCount = 10
        val radius = size.width / circleCount
        for (i in 0..circleCount) {
            drawCircle(
                color = Color.White,
                radius = radius / 1.1f,
                center = Offset(x = (size.width / circleCount) * i, y = size.height * 0.4f)
            )
        }
    }
}