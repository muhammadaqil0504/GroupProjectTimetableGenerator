package com.example.timetableapp.TimetableView

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timetableapp.DailyListLayout
import com.example.timetableapp.R
import com.example.timetableapp.ScallopedHeader
import com.example.timetableapp.TimetableEntry

@Composable
fun TimetableViewScreen(
    scheduleData: List<TimetableEntry>,
    onBackClick: () -> Unit
) {
    var isWeeklyView by remember { mutableStateOf(true) }
    val chalkboardGreen = Color(0xFF4B6E63)
    val switchButtonColor = Color(0xFFB58B43)

    Box(modifier = Modifier.fillMaxSize().background(chalkboardGreen)) {
        ScallopedHeader()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, start = 10.dp, end = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isWeeklyView) "WEEKLY VIEW" else "DAILY VIEW",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                if (isWeeklyView) {
                    WeeklyTableLayout(scheduleData)
                } else {
                    DailyListLayout(scheduleData)
                }
            }

            Button(
                onClick = { isWeeklyView = !isWeeklyView },
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth(0.8f)
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = switchButtonColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isWeeklyView) "Switch to Daily" else "Switch to Weekly",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        IconButton(
            onClick = onBackClick,
            modifier = Modifier.padding(top = 40.dp, start = 10.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
    }
}

@Composable
fun WeeklyTableLayout(scheduleData: List<TimetableEntry>) {
    val days = listOf("", "SUN", "MON", "TUE", "WED", "THU")
    val morningSlots = listOf("7.30-8.00", "8.00-8.30", "8.30-9.00", "9.00-9.30", "9.30-10.00")
    val afternoonSlots = listOf("10.30-11.00", "11.00-11.30", "11.30-12.00", "12.00-12.30")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
            .padding(6.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
            days.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }
        }

        LazyColumn {
            items(morningSlots.size) { index ->
                TimetableRow(time = morningSlots[index], scheduleData = scheduleData)
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 2.dp)
                        .height(30.dp)
                        .background(Color(0xFFD1D1D1), RoundedCornerShape(4.dp))
                        .border(0.5.dp, Color.Black, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("REHAT", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.DarkGray)
                }
            }

            items(afternoonSlots.size) { index ->
                TimetableRow(time = afternoonSlots[index], scheduleData = scheduleData)
            }
        }
    }
}

@Composable
fun TimetableRow(time: String, scheduleData: List<TimetableEntry>) {
    val dayHeaders = listOf("SUN", "MON", "TUE", "WED", "THU")
    val cellBgColor = Color(0xFFFDF5E6)

    Row(
        modifier = Modifier.fillMaxWidth().height(65.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            modifier = Modifier.weight(1f),
            fontSize = 9.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        dayHeaders.forEach { day ->
            val isPerhimpunan = day == "MON" && time == "7.30-8.00"

            val entry = if (isPerhimpunan) {
                TimetableEntry("Perhimpunan", "Tapak", time, "MON $time", R.drawable.perhimpunan_icon)
            } else {
                scheduleData.find { item ->
                    // 1. Check if Day matches
                    val dayMatch = item.dayAndTime.uppercase().contains(day)

                    // 2. Normalization Function to match generated data with grid slots
                    fun normalize(t: String): String {
                        return t.uppercase()
                            .replace(" AM", "")
                            .replace(" PM", "")
                            .replace(":", ".")
                            .split("-")[0] // Extract start time
                            .trim()
                    }

                    val savedTime = normalize(item.dayAndTime.split(" ").lastOrNull() ?: "")
                    val slotStart = normalize(time)

                    // 3. Match if Day matches AND normalized times match (accounting for leading zeros)
                    dayMatch && (
                            savedTime == slotStart ||
                                    "0$savedTime" == slotStart ||
                                    savedTime == "0$slotStart"
                            )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(2.dp)
                    .background(
                        if (entry != null) cellBgColor else Color.White.copy(alpha = 0.3f),
                        RoundedCornerShape(4.dp)
                    )
                    .border(
                        if (entry != null) 0.5.dp else 0.1.dp,
                        if (entry != null) Color.Black else Color.Gray,
                        RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (entry != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(2.dp)
                    ) {
                        entry.iconRes?.let {
                            Image(
                                painter = painterResource(id = it),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Text(
                            text = entry.subject,
                            fontSize = 7.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            lineHeight = 8.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}