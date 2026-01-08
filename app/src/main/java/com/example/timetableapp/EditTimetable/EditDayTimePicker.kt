package com.example.timetableapp.EditTimetable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditDaySelectionScreen(
    currentTime: String, // This is the preserved time (e.g., "08.00")
    onSelectionComplete: (String) -> Unit,
    onBack: () -> Unit
) {
    // Removed chalkboardGreen variable as it's no longer used for the background
    val itemBgColor = Color(0xFFF5E6D3)
    val buttonGold = Color(0xFFB58B43)

    val daysOfWeek = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday")
    val dayMap = mapOf(
        "Sunday" to "SUN",
        "Monday" to "MON",
        "Tuesday" to "TUE",
        "Wednesday" to "WED",
        "Thursday" to "THU"
    )

    var selectedDay by remember { mutableStateOf("") }

    // CHANGED: Removed .background(chalkboardGreen)
    // The Box is now transparent to show the background from MainActivity
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text(text = "Edit Day Only", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(20.dp))

            // Information Card showing the time being preserved
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Preserving Time:", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    Text(currentTime, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            // --- DAY SELECTION ---
            Text("Select New Day", color = Color.White, modifier = Modifier.padding(vertical = 12.dp))

            // Grid-like layout for days
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                daysOfWeek.forEach { day ->
                    val isSelected = selectedDay == day
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        onClick = { selectedDay = day },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color.White else itemBgColor,
                        border = if (isSelected) BorderStroke(2.dp, buttonGold) else null
                    ) {
                        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 20.dp)) {
                            Text(
                                text = day,
                                color = Color.Black,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val shortDay = dayMap[selectedDay] ?: "MON"
                    onSelectionComplete("$shortDay $currentTime")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp)
                    .height(55.dp),
                enabled = selectedDay.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = buttonGold),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("UPDATE DAY", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}