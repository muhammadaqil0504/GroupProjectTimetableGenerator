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
    currentTime: String, // e.g., "08.00"
    onSelectionComplete: (String) -> Unit,
    onBack: () -> Unit
) {
    val itemBgColor = Color(0xFFF5E6D3)
    val buttonGold = Color(0xFFB58B43)

    // UPDATED: Full 7 days starting with Monday
    val daysOfWeek = listOf(
        "Monday", "Tuesday", "Wednesday", "Thursday",
        "Friday", "Saturday", "Sunday"
    )

    // UPDATED: Full mapping for 7 days
    val dayMap = mapOf(
        "Monday" to "MON",
        "Tuesday" to "TUE",
        "Wednesday" to "WED",
        "Thursday" to "THU",
        "Friday" to "FRI",
        "Saturday" to "SAT",
        "Sunday" to "SUN"
    )

    var selectedDay by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Edit Day Only",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(20.dp))

            // Information Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Current Class Time:",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = currentTime,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Text(
                text = "Select New Day",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 12.dp).align(Alignment.Start)
            )

            // Scrollable column if screen height is small
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                daysOfWeek.forEach { day ->
                    val isSelected = selectedDay == day
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        onClick = { selectedDay = day },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Color.White else itemBgColor,
                        border = if (isSelected) BorderStroke(2.dp, buttonGold) else null,
                        tonalElevation = 2.dp
                    ) {
                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = day,
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val shortDay = dayMap[selectedDay] ?: "MON"
                    // Combines the new short day with the existing time
                    onSelectionComplete("$shortDay $currentTime")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp)
                    .height(60.dp),
                enabled = selectedDay.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonGold,
                    disabledContainerColor = buttonGold.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "UPDATE DAY",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}