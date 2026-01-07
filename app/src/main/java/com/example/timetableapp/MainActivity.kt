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

// Ensure these imports match your actual folder names.
// Note: If you renamed 'EditTimetable' to 'edittimetable' to fix the warning, update here.
import com.example.timetableapp.EditTimetable.EditTimetableScreen
import com.example.timetableapp.homepage.HomepageInterface
import com.example.timetableapp.SubjectView.SubjectInterface
import com.example.timetableapp.generatetable.GenerateTimetableInterface
import com.example.timetableapp.TimetableView.TimetableViewScreen

// SHARED DATA CLASS
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
            var currentScreen by remember { mutableStateOf("front") }

            // Using mutableStateListOf ensures the UI observes additions/replacements
            val userSchedule = remember { mutableStateListOf<TimetableEntry>() }

            // State to hold the specific item being edited
            var entryToEdit by remember { mutableStateOf<TimetableEntry?>(null) }

            // Critical for ensuring the correct index is replaced in the list
            var editIndex by remember { mutableIntStateOf(-1) }

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
                                if (userSchedule.isNotEmpty()) {
                                    // Set tracking to the first item for editing
                                    editIndex = 0
                                    entryToEdit = userSchedule[editIndex]
                                    currentScreen = "edit"
                                } else {
                                    // Provide a placeholder if the list is empty
                                    editIndex = -1
                                    entryToEdit = TimetableEntry("New Subject", "Lecturer", "30m", "MON 08.00")
                                    currentScreen = "edit"
                                }
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
                        // Using a local val to ensure thread safety during composition
                        val currentEntry = entryToEdit
                        if (currentEntry != null) {
                            EditTimetableScreen(
                                entryToEdit = currentEntry,
                                onUpdateClick = { updatedEntry: TimetableEntry ->
                                    // UPDATE LOGIC: Replace at specific index to ensure persistence
                                    if (editIndex != -1 && editIndex < userSchedule.size) {
                                        userSchedule[editIndex] = updatedEntry
                                    } else {
                                        userSchedule.add(updatedEntry)
                                    }

                                    // Reset editing state and navigate to view the results
                                    entryToEdit = null
                                    editIndex = -1
                                    currentScreen = "view_timetable"
                                },
                                onCancelClick = {
                                    entryToEdit = null
                                    editIndex = -1
                                    currentScreen = "home"
                                }
                            )
                        } else {
                            currentScreen = "home"
                        }
                    }

                    "subject" -> {
                        SubjectInterface(onBackClick = { currentScreen = "home" })
                    }

                    "view_timetable" -> {
                        TimetableViewScreen(
                            // .toList() forces a UI refresh by providing a new list instance
                            scheduleData = userSchedule.toList(),
                            onBackClick = { currentScreen = "home" }
                        )
                    }
                }
            }
        }
    }
}

// --- GLOBAL UI COMPONENTS ---

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