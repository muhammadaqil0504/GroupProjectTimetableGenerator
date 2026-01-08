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
    // These are the shortened names (3 letters)
    val days = listOf("SUN", "MON", "TUE", "WED", "THU")
    var selectedDay by remember { mutableStateOf("SUN") }

    val fixedItems = listOf(
        TimetableEntry("REHAT", "Waktu Makan", "10.00-10.30", "$selectedDay 10.00", R.drawable.rehat_icon)
    ).toMutableList()

    if (selectedDay == "MON") {
        fixedItems.add(TimetableEntry("Perhimpunan", "Tapak", "07.30-08.00", "MON 07.30", R.drawable.perhimpunan_icon))
    }

    // NORMALIZED SORTING
    val filteredSchedule = (scheduleData.filter { it.dayAndTime.uppercase().contains(selectedDay) } + fixedItems)
        .sortedBy { entry ->
            val timePart = entry.duration.split("-").firstOrNull() ?: ""
            if (timePart.length == 4 && !timePart.startsWith("0")) "0$timePart" else timePart
        }

    Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        // DAY SELECTION ROW
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            days.forEach { day ->
                val isSelected = selectedDay == day
                Button(
                    onClick = { selectedDay = day },
                    modifier = Modifier.weight(1f).height(45.dp), // Slightly taller for better visibility
                    contentPadding = PaddingValues(0.dp), // Removes internal padding to fit "WED" etc.
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFFF5E6D3) else Color.White.copy(alpha = 0.8f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Text(
                        text = day,
                        color = Color.Black,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                        fontSize = 13.sp // Slightly larger font
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
                    val displayTime = entry.duration.split("-")[0].replace(".", ":")
                    Column(modifier = Modifier.width(80.dp)) { // Slightly wider for the time label
                        Text(
                            text = "$displayTime AM",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 15.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 4.dp).width(55.dp),
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth().height(85.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (entry.subject == "REHAT") Color(0xFFFFF9C4) else Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            entry.iconRes?.let { Image(painterResource(it), null, modifier = Modifier.size(55.dp)) }
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