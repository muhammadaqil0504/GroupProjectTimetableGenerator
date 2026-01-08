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
    // The Box is now transparent, showing the background image from MainActivity
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
                Text("Select Day", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
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
                        border = if (isSelected) BorderStroke(2.dp, buttonGold) else null
                    ) {
                        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 20.dp)) {
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
                colors = ButtonDefaults.buttonColors(containerColor = buttonGold),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("CONFIRM DAY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}