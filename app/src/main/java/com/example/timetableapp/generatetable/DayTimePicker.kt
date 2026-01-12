package com.example.timetableapp.generatetable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.foundation.BorderStroke

@Composable
fun DayTimeSelectionScreen(onSelectionComplete: (String) -> Unit, onBack: () -> Unit) {
    val itemBgColor = Color(0xFFF5E6D3)
    val buttonGold = Color(0xFFB58B43)

    // UPDATED: Full 7 days starting with Monday
    val daysOfWeek = listOf(
        "Monday", "Tuesday", "Wednesday", "Thursday",
        "Friday", "Saturday", "Sunday"
    )

    // UPDATED: Mapping for all 7 days to match your Timetable logic
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
                    text = "Select Day",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                items(daysOfWeek) { day ->
                    val isSelected = selectedDay == day
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .clickable { selectedDay = day },
                        shape = RoundedCornerShape(12.dp),
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
                                fontSize = 18.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val shortDay = dayMap[selectedDay] ?: selectedDay
                    onSelectionComplete(shortDay)
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
                    text = "CONFIRM DAY",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}