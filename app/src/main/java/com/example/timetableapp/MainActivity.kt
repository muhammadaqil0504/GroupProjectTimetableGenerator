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
import com.example.timetableapp.homepage.HomepageInterface
import com.example.timetableapp.SubjectView.SubjectInterface
import com.example.timetableapp.generatetable.GenerateTimetableInterface
import com.example.timetableapp.TimetableView.TimetableViewScreen

data class TimetableEntry(
    val subject: String,
    val lecturer: String,
    val duration: String,
    val dayAndTime: String,
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

            val userSchedule = remember {
                mutableStateListOf<TimetableEntry>().apply {
                    addAll(storage.loadSchedule())
                }
            }

            var entryToEdit by remember { mutableStateOf<TimetableEntry?>(null) }
            var editIndex by remember { mutableIntStateOf(-1) }

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
                                onEditNavClick = {
                                    if (userSchedule.isNotEmpty()) {
                                        editIndex = 0
                                        entryToEdit = userSchedule[editIndex]
                                        currentScreen = "edit"
                                    } else {
                                        editIndex = -1
                                        entryToEdit = TimetableEntry("New Subject", "Lecturer", "30m", "MON 08.00")
                                        currentScreen = "edit"
                                    }
                                },
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
                                onAddEntry = { newEntry ->
                                    userSchedule.add(newEntry)
                                    storage.saveSchedule(userSchedule.toList())
                                },
                                onGenerateAutomatically = {
                                    storage.saveSchedule(userSchedule.toList())
                                    currentScreen = "view_timetable"
                                }
                            )
                        }
                        "edit" -> {
                            val currentEntry = entryToEdit
                            if (currentEntry != null) {
                                EditTimetableScreen(
                                    entryToEdit = currentEntry,
                                    onUpdateClick = { updatedEntry ->
                                        if (editIndex != -1 && editIndex < userSchedule.size) {
                                            userSchedule[editIndex] = updatedEntry
                                        } else {
                                            userSchedule.add(updatedEntry)
                                        }
                                        storage.saveSchedule(userSchedule.toList())
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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            // Centers the group vertically in the screen
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_timetable),
                contentDescription = "App Logo",
                modifier = Modifier.size(280.dp),
                contentScale = ContentScale.Fit
            )

            // Tight spacer to put button "right below"
            Spacer(Modifier.height(40.dp))

            // Generate Button - Matching the wide style in your image
            Button(
                onClick = onGenerateClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Made slightly wider to match screenshot
                    .height(65.dp),     // Increased height for that chunky look
                colors = ButtonDefaults.buttonColors(containerColor = buttonGold),
                shape = RoundedCornerShape(15.dp) // More rounded corners
            ) {
                Text(
                    text = "GENERATE",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // This pushes the whole group slightly higher than dead center
            Spacer(Modifier.height(80.dp))
        }
    }
}