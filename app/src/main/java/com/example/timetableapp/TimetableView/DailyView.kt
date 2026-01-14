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
    // UPDATED: Sunday to Thursday Only
    val days = listOf("SUN", "MON", "TUE", "WED", "THU")
    var selectedDay by remember { mutableStateOf("SUN") }

    // --- FILTER & SORT LOGIC ---
    // Removed the "fixedItems" list so REHAT only shows if added by user
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
        // Day Picker Row - Now fits perfectly with 5 days
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            days.forEach { day ->
                val isSelected = selectedDay == day
                Button(
                    onClick = { selectedDay = day },
                    modifier = Modifier
                        .weight(1f)
                        .height(45.dp),
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

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredSchedule) { entry ->
                Row(modifier = Modifier.fillMaxWidth()) {

                    // --- TIME & PERIOD LOGIC ---
                    val timeParts = entry.duration.split("-")
                    val startTime = timeParts.getOrNull(0)?.trim() ?: ""
                    val startHour = startTime.split(".", ":")[0].toIntOrNull() ?: 0

                    val period = if (startHour in 7..11) "AM" else "PM"

                    Column(modifier = Modifier.width(90.dp)) {
                        Text(
                            text = "${entry.duration}\n$period",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 10.dp),
                            lineHeight = 14.sp
                        )
                    }

                    // --- DYNAMIC CARD DISPLAY ---
                    val isSpecial = entry.subject.uppercase() == "REHAT" || entry.subject.uppercase() == "PERHIMPUNAN"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        colors = CardDefaults.cardColors(
                            // SPECIAL COLOR LOGIC: Grey for Rehat/Perhimpunan, Beige for Subjects
                            containerColor = if (isSpecial) Color(0xFFD1D1D1) else Color(0xFFFDF5E6)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
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
                                    modifier = Modifier.size(45.dp)
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = entry.subject,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp,
                                    color = Color.Black
                                )
                                if (entry.lecturer != "-") {
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
}