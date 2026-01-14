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
import java.util.UUID

import com.example.timetableapp.EditTimetable.EditTimetableScreen
import com.example.timetableapp.homepage.HomepageInterface
import com.example.timetableapp.SubjectView.SubjectInterface
import com.example.timetableapp.generatetable.GenerateTimetableInterface
import com.example.timetableapp.TimetableView.TimetableViewScreen

// --- SHARED DATA CLASS ---
data class TimetableEntry(
    val id: String = UUID.randomUUID().toString(),
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

            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.background2),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(modifier = Modifier.fillMaxSize().background(overlayColor))

                Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
                    when (currentScreen) {
                        "front" -> TimetableInterface(onGenerateClick = { currentScreen = "home" })

                        "home" -> HomepageInterface(
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
                                Toast.makeText(context, "Timetable Reset", Toast.LENGTH_SHORT).show()
                            }
                        )

                        "generate" -> GenerateTimetableInterface(
                            onBackClick = { currentScreen = "home" },
                            existingSchedule = userSchedule.toList(),
                            onAddEntries = { newEntries ->
                                userSchedule.addAll(newEntries)
                                storage.saveSchedule(userSchedule.toList())
                                // REMINDER: currentScreen = "home" was removed so you can add more items!
                            }
                        )

                        "edit" -> EditTimetableScreen(
                            scheduleData = userSchedule.toList(),
                            onUpdateClick = { updatedEntry ->
                                val index = userSchedule.indexOfFirst { it.id == updatedEntry.id }
                                if (index != -1) {
                                    userSchedule[index] = updatedEntry
                                    storage.saveSchedule(userSchedule.toList())
                                    Toast.makeText(context, "Changes Saved!", Toast.LENGTH_SHORT).show()
                                    currentScreen = "view_timetable"
                                }
                            },
                            onDeleteClick = { entryToDelete ->
                                userSchedule.removeIf { it.id == entryToDelete.id }
                                storage.saveSchedule(userSchedule.toList())
                                if (userSchedule.isEmpty()) currentScreen = "home"
                            },
                            onCancelClick = { currentScreen = "home" }
                        )

                        "subject" -> SubjectInterface(onBackClick = { currentScreen = "home" })

                        "view_timetable" -> TimetableViewScreen(
                            scheduleData = userSchedule.toList(),
                            onBackClick = { currentScreen = "home" }
                        )
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