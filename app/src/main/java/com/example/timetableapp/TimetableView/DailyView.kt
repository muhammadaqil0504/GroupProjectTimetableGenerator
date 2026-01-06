package com.example.timetableapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    // 1. Define Fixed Items
    val fixedItems = mutableListOf<TimetableEntry>()

    // Always add REHAT at 10:00 AM for every day
    fixedItems.add(
        TimetableEntry(
            subject = "REHAT",
            lecturer = "Waktu Makan",
            duration = "10.00-10.30",
            dayAndTime = "$selectedDay 10.00-10.30",
            iconRes = R.drawable.rehat_icon // Use your boy-with-milk icon
        )
    )

    // ONLY add PERHIMPUNAN if the selected day is Monday
    if (selectedDay == "MON") {
        fixedItems.add(
            TimetableEntry(
                subject = "Perhimpunan",
                lecturer = "Tapak Perhimpunan",
                duration = "07.30-08.00",
                dayAndTime = "MON 07.30-08.00",
                iconRes = R.drawable.perhimpunan_icon // Ensure you add a speaker/assembly icon to drawable
            )
        )
    }

    // 2. Combine user data with fixed items and sort
    val filteredSchedule = (scheduleData.filter { it.dayAndTime.uppercase().contains(selectedDay) } + fixedItems)
        .sortedBy { it.duration }

    Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        // --- Day Selection Tabs ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            days.forEach { day ->
                val isSelected = selectedDay == day
                Button(
                    onClick = { selectedDay = day },
                    modifier = Modifier.weight(1f).height(40.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFFF5E6D3) else Color.White.copy(alpha = 0.8f),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Text(text = day, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        // --- Schedule List with Time Markers and Divider Lines ---
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredSchedule) { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Left Time Marker
                    val startTime = entry.duration.split("-")[0].replace(".", ":")
                    Column(modifier = Modifier.width(75.dp)) {
                        Text(
                            text = "$startTime AM",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 15.dp)
                        )
                        // This adds the white line seen in your reference image
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 4.dp).width(50.dp),
                            thickness = 1.dp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }

                    // Card Design
                    val isFixed = entry.subject == "REHAT" || entry.subject == "Perhimpunan"
                    Card(
                        modifier = Modifier.fillMaxWidth().height(85.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isFixed) Color(0xFFFFF9C4) else Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (entry.iconRes != null) {
                                Image(
                                    painter = painterResource(id = entry.iconRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(55.dp)
                                )
                                Spacer(Modifier.width(15.dp))
                            }

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