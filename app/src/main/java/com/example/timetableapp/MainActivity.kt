package com.example.timetableapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.timetableapp.EditTimetable.EditTimetableScreen
import com.example.timetableapp.EditTimetable.EditSelectionScreen
import com.example.timetableapp.homepage.HomepageInterface
import com.example.timetableapp.SubjectView.SubjectInterface
import com.example.timetableapp.generatetable.GenerateTimetableInterface
import com.example.timetableapp.TimetableView.TimetableViewScreen

data class TimetableEntry(
    val subject: String,
    val lecturer: String,
    val duration: String,
    val dayAndTime: String, // Format: "MON 08.00"
    val iconRes: Int? = null
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val storage = TimetableStorage(this)

        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            var currentScreen by remember { mutableStateOf("front") }
            val context = LocalContext.current

            val overlayColor = if (isDarkMode) Color.Black.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.2f)

            // Auto-updating list using state management
            val userSchedule = remember {
                mutableStateListOf<TimetableEntry>().apply {
                    addAll(storage.loadSchedule())
                }
            }

            var entryToEdit by remember { mutableStateOf<TimetableEntry?>(null) }
            var editIndex by remember { mutableIntStateOf(-1) }

            // --- HELPER FUNCTION FOR VALIDATION ---
            fun isSlotTaken(newDayAndTime: String, currentIndex: Int = -1): Boolean {
                return userSchedule.filterIndexed { index, _ -> index != currentIndex }
                    .any { it.dayAndTime.uppercase().trim() == newDayAndTime.uppercase().trim() }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.background2),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(modifier = Modifier.fillMaxSize().background(overlayColor))

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    when (currentScreen) {
                        "front" -> {
                            TimetableInterface(onGenerateClick = { currentScreen = "home" })
                        }
                        "home" -> {
                            HomepageInterface(
                                isDarkMode = isDarkMode,
                                onDarkModeToggle = { isDarkMode = !isDarkMode },
                                onGenerateNavClick = { currentScreen = "generate" },
                                onEditNavClick = { currentScreen = "edit_selection" },
                                onSubjectNavClick = { currentScreen = "subject" },
                                onTimetableClick = { currentScreen = "view_timetable" },
                                onResetConfirm = {
                                    storage.clearSchedule()
                                    userSchedule.clear()
                                    currentScreen = "front"
                                    Toast.makeText(context, "Timetable Reset Successfully", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        "generate" -> {
                            // UPDATED: Pass existingSchedule to enable slot filtering
                            GenerateTimetableInterface(
                                onBackClick = { currentScreen = "home" },
                                existingSchedule = userSchedule.toList(),
                                onAddEntries = { newEntries ->
                                    // Secondary check for safety
                                    val conflicts = newEntries.filter { isSlotTaken(it.dayAndTime) }

                                    if (conflicts.isNotEmpty()) {
                                        val conflictNames = conflicts.joinToString(", ") { it.dayAndTime }
                                        Toast.makeText(context, "Error: $conflictNames already taken!", Toast.LENGTH_LONG).show()
                                    } else {
                                        userSchedule.addAll(newEntries)
                                        storage.saveSchedule(userSchedule.toList())
                                        Toast.makeText(context, "Added ${newEntries.size} slots successfully!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                        "edit_selection" -> {
                            EditSelectionScreen(
                                userSchedule = userSchedule.toList(),
                                onEntrySelected = { index, entry ->
                                    editIndex = index
                                    entryToEdit = entry
                                    currentScreen = "edit"
                                },
                                onDeleteEntry = { index ->
                                    if (index in userSchedule.indices) {
                                        userSchedule.removeAt(index)
                                        storage.saveSchedule(userSchedule.toList())
                                        Toast.makeText(context, "Entry deleted", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onBack = { currentScreen = "home" }
                            )
                        }
                        "edit" -> {
                            val currentEntry = entryToEdit
                            if (currentEntry != null) {
                                EditTimetableScreen(
                                    entryToEdit = currentEntry,
                                    onUpdateClick = { updatedEntry ->
                                        if (isSlotTaken(updatedEntry.dayAndTime, editIndex)) {
                                            Toast.makeText(context, "Conflict with another entry!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            if (editIndex in userSchedule.indices) {
                                                userSchedule[editIndex] = updatedEntry
                                                storage.saveSchedule(userSchedule.toList())
                                                entryToEdit = null
                                                editIndex = -1
                                                currentScreen = "view_timetable"
                                                Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    onCancelClick = {
                                        entryToEdit = null
                                        editIndex = -1
                                        currentScreen = "edit_selection"
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
                                scheduleData = userSchedule.toList(),
                                onBackClick = { currentScreen = "home" }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimetableInterface(onGenerateClick: () -> Unit) {
    val buttonGold = Color(0xFFB58B43)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_timetable),
            contentDescription = "App Logo",
            modifier = Modifier.size(280.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = onGenerateClick,
            modifier = Modifier.fillMaxWidth(0.8f).height(65.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonGold),
            shape = RoundedCornerShape(15.dp)
        ) {
            Text("GENERATE", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(80.dp))
    }
}