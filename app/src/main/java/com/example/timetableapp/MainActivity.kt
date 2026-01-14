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

import com.example.timetableapp.edittimetable.EditTimetableScreen
import com.example.timetableapp.homepage.HomepageInterface
import com.example.timetableapp.SubjectView.SubjectInterface
import com.example.timetableapp.generatetable.GenerateTimetableInterface
import com.example.timetableapp.TimetableView.TimetableViewScreen

data class TimetableEntry(
    val subject: String,
    val lecturer: String,
    val duration: String,
    val dayAndTime: String, // Format: "SUN 07.30"
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

            // --- HELPER FUNCTION FOR VALIDATION ---
            fun isSlotTaken(newDayAndTime: String, currentEntry: TimetableEntry? = null): Boolean {
                return userSchedule.any {
                    it.dayAndTime.uppercase().trim() == newDayAndTime.uppercase().trim() && it != currentEntry
                }
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
                                onEditNavClick = { currentScreen = "edit" },
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
                            GenerateTimetableInterface(
                                onBackClick = { currentScreen = "home" },
                                existingSchedule = userSchedule.toList(),
                                onAddEntries = { newEntries ->
                                    val conflicts = newEntries.filter { isSlotTaken(it.dayAndTime) }
                                    if (conflicts.isNotEmpty()) {
                                        Toast.makeText(context, "Conflict: Some slots already taken!", Toast.LENGTH_LONG).show()
                                    } else {
                                        userSchedule.addAll(newEntries)
                                        storage.saveSchedule(userSchedule.toList())
                                        Toast.makeText(context, "Added successfully!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                        "edit" -> {
                            EditTimetableScreen(
                                scheduleData = userSchedule.toList(),
                                onUpdateClick = { updatedEntry ->
                                    // Logic to find and update the entry
                                    val index = userSchedule.indexOfFirst {
                                        // Match by original content if day/subject changed
                                        it.dayAndTime == updatedEntry.dayAndTime || it.subject == updatedEntry.subject
                                    }

                                    if (index != -1) {
                                        userSchedule[index] = updatedEntry
                                        storage.saveSchedule(userSchedule.toList())
                                        Toast.makeText(context, "Changes Saved!", Toast.LENGTH_SHORT).show()
                                        currentScreen = "view_timetable"
                                    }
                                },
                                // FIXED: Added missing onDeleteClick parameter
                                onDeleteClick = { entryToDelete ->
                                    userSchedule.remove(entryToDelete)
                                    storage.saveSchedule(userSchedule.toList())
                                    Toast.makeText(context, "Entry Deleted", Toast.LENGTH_SHORT).show()
                                    // If list is empty, return home, otherwise stay to edit more
                                    if (userSchedule.isEmpty()) currentScreen = "home"
                                },
                                onCancelClick = { currentScreen = "home" }
                            )
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