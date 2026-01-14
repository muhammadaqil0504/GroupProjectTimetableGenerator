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
    val days = listOf("SUN", "MON", "TUE", "WED", "THU")
    var selectedDay by remember { mutableStateOf("SUN") }

    val filteredSchedule = scheduleData.filter { entry ->
        val dayPart = entry.dayAndTime.split(" ").firstOrNull()?.uppercase() ?: ""
        dayPart.startsWith(selectedDay)
    }.sortedBy { entry ->
        val timePart = entry.duration.split("-").firstOrNull() ?: ""
        val hour = timePart.split(".", ":")[0].trim().toIntOrNull() ?: 0
        val normalizedHour = if (hour < 7) hour + 12 else hour
        val minute = timePart.split(".", ":").getOrNull(1)?.trim()?.toIntOrNull() ?: 0
        normalizedHour * 100 + minute
    }

    Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        // --- DAY PICKER ROW ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            days.forEach { day ->
                val isSelected = selectedDay == day
                Button(
                    onClick = { selectedDay = day },
                    modifier = Modifier.weight(1f).height(45.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFFB58B43) else Color.White.copy(alpha = 0.8f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Text(
                        text = day,
                        color = if (isSelected) Color.White else Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        if (filteredSchedule.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No classes scheduled", color = Color.White.copy(alpha = 0.6f))
            }
        }

        // --- TIMETABLE LIST ---
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredSchedule) { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // --- TIME SECTION WITH HORIZONTAL DIVIDER ---
                    val startTime = entry.duration.split("-").firstOrNull()?.trim() ?: ""
                    val startHour = startTime.split(".", ":").firstOrNull()?.toIntOrNull() ?: 0
                    val period = if (startHour in 7..11) "AM" else "PM"

                    // Increased width to 135.dp for the 16.sp font and divider
                    Column(
                        modifier = Modifier.width(105.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "${entry.duration} $period",
                            color = Color.White,
                            fontSize = 14.sp, // Kept at 16.sp as requested
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        // The Horizontal Divider
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 4.dp, end = 10.dp).width( width = 80.dp),
                            thickness = 2.dp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }

                    // --- DYNAMIC CARD DISPLAY ---
                    val isSpecial = entry.subject.uppercase() == "REHAT" || entry.subject.uppercase() == "PERHIMPUNAN"

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSpecial) Color(0xFFD1D1D1) else Color(0xFFFDF5E6)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
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
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = entry.subject,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    maxLines = 1
                                )
                                if (entry.lecturer != "-") {
                                    Text(
                                        text = entry.lecturer,
                                        fontSize = 12.sp,
                                        color = Color.DarkGray,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}