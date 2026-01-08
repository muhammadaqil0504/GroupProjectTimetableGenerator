package com.example.timetableapp.TimetableView

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timetableapp.DailyListLayout
import com.example.timetableapp.PdfExporter
import com.example.timetableapp.R
import com.example.timetableapp.TimetableEntry

@Composable
fun TimetableViewScreen(
    scheduleData: List<TimetableEntry>,
    onBackClick: () -> Unit
) {
    var isWeeklyView by remember { mutableStateOf(true) }
    val switchButtonColor = Color(0xFFB58B43)
    val exportButtonColor = Color(0xFF4A90E2) // Distinct blue for export
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))

            // Navigation and Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = if (isWeeklyView) "Weekly" else "Daily",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // SIMPLE EXPORT BUTTON
                Button(
                    onClick = { PdfExporter.exportTimetableToPdf(context, scheduleData) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(35.dp)
                ) {
                    Text(
                        text = "EXPORT PDF",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Table Content Area
            Box(modifier = Modifier.weight(1f)) {
                if (isWeeklyView) {
                    WeeklyTableLayout(scheduleData)
                } else {
                    DailyListLayout(scheduleData)
                }
            }

            // Switch View Button (Main Action)
            Button(
                onClick = { isWeeklyView = !isWeeklyView },
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = switchButtonColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isWeeklyView) "SWITCH TO DAILY" else "SWITCH TO WEEKLY",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
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
                    val dayMatch = item.dayAndTime.uppercase().contains(day)

                    fun normalize(t: String): String {
                        return t.uppercase()
                            .replace(" AM", "")
                            .replace(" PM", "")
                            .replace(":", ".")
                            .split("-")[0]
                            .trim()
                    }

                    val savedTime = normalize(item.dayAndTime.split(" ").lastOrNull() ?: "")
                    val slotStart = normalize(time)

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