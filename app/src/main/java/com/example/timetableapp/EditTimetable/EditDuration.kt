package com.example.timetableapp.EditTimetable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun EditDurationScreen(
    currentDay: String, // Pass the current day (e.g., "MON") to rebuild the full string
    onDurationSelected: (String, String) -> Unit, // Returns (newDuration, newDayAndTime)
    onBack: () -> Unit
) {
    // Background color variable removed to let background.jpeg show
    val itemBgColor = Color(0xFFF5E6D3)

    val timeSlots = listOf(
        "7.30-8.00",
        "8.00-8.30",
        "8.30-9.00",
        "9.00-9.30",
        "9.30-10.00",
        "10.30-11.00",
        "11.00-11.30",
        "11.30-12.00",
        "12.00-12.30"
    )

    // CHANGED: Removed .background(chalkboardGreen)
    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text(
                    text = "Select Duration",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(timeSlots) { timeRange ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clickable {
                                val startTime = timeRange.split("-")[0].trim()
                                val newDayAndTime = "$currentDay $startTime"
                                onDurationSelected(timeRange, newDayAndTime)
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = itemBgColor,
                        shadowElevation = 2.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = timeRange,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}