package com.example.timetableapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DailyListLayout(scheduleData: List<TimetableEntry>) {
    // UPDATED: Full 7-day week starting with Monday
    val days = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    var selectedDay by remember { mutableStateOf("MON") }

    // --- 1. REHAT REMAINS FIXED (EVERY DAY) ---
    val fixedItems = listOf(
        TimetableEntry("REHAT", "Waktu Makan", "10.00-10.30", "$selectedDay 10.00", R.drawable.rehat_icon)
    )

    // --- 2. FILTER & SORT LOGIC ---
    val filteredSchedule = (scheduleData.filter { it.dayAndTime.uppercase().contains(selectedDay) } + fixedItems)
        .sortedBy { entry ->
            val timePart = entry.duration.split("-").firstOrNull() ?: ""
            val hour = timePart.split(".", ":")[0].trim().toIntOrNull() ?: 0
            val normalizedHour = if (hour < 7) hour + 12 else hour
            val minute = timePart.split(".", ":").getOrNull(1)?.trim()?.toIntOrNull() ?: 0
            normalizedHour * 100 + minute
        }

    Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        // Day Picker Row - Optimized for 7 Days
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp) // Reduced spacing to fit more
        ) {
            days.forEach { day ->
                val isSelected = selectedDay == day
                Button(
                    onClick = { selectedDay = day },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    contentPadding = PaddingValues(0.dp), // Clear padding for small buttons
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFFF5E6D3) else Color.White.copy(alpha = 0.8f)
                    ),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Text(
                        text = day,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp // Slightly smaller font for 7 columns
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredSchedule) { entry ->
                Row(modifier = Modifier.fillMaxWidth()) {

                    // --- 3. AM/PM LOGIC (11.30-12.00 PM FIX) ---
                    val timeParts = entry.duration.split("-")
                    val startTime = timeParts.getOrNull(0)?.trim() ?: ""
                    val endTime = timeParts.getOrNull(1)?.trim() ?: ""

                    val startHour = startTime.split(".", ":")[0].toIntOrNull() ?: 0
                    val endHour = endTime.split(".", ":")[0].toIntOrNull() ?: 0

                    val period = when {
                        startHour >= 12 || startHour in 1..6 -> "PM"
                        endHour >= 12 && endHour != 0 -> "PM"
                        else -> "AM"
                    }

                    Column(modifier = Modifier.width(105.dp)) {
                        Text(
                            text = "${entry.duration} $period",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 15.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 4.dp).width(80.dp),
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }

                    // --- 4. DYNAMIC CARD DISPLAY ---
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(85.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (entry.subject == "REHAT") Color(0xFFFFF9C4) else Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val iconToUse = when {
                                entry.subject.equals("Perhimpunan", ignoreCase = true) -> R.drawable.perhimpunan_icon
                                entry.subject.equals("REHAT", ignoreCase = true) -> R.drawable.rehat_icon
                                else -> entry.iconRes
                            }

                            if (iconToUse != null) {
                                Image(
                                    painter = painterResource(iconToUse),
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp)
                                )
                            }

                            Spacer(Modifier.width(15.dp))

                            Column {
                                Text(
                                    text = entry.subject,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = entry.lecturer,
                                    fontSize = 13.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}