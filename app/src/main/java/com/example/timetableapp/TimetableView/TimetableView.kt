package com.example.timetableapp.TimetableView

import android.widget.Toast
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

// Helper function for Shortforms
fun getSubjectAlias(subject: String): String {
    return when (subject.trim()) {
        "Bahasa Melayu" -> "BM"
        "Bahasa Inggeris" -> "BI"
        "Matematik" -> "MAT"
        "Sains" -> "SNS"
        "Pendidikan Islam" -> "PI"
        "Muzik" -> "MZ"
        "Sejarah" -> "SEJ"
        "Seni Visual" -> "PSV"
        "Perhimpunan" -> "PER"
        else -> if (subject.length > 5) subject.take(4) + ".." else subject
    }
}

@Composable
fun TimetableViewScreen(
    scheduleData: List<TimetableEntry>,
    onBackClick: () -> Unit
) {
    var isWeeklyView by remember { mutableStateOf(true) }
    val switchButtonColor = Color(0xFFB58B43)
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))

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

                // FIXED: Export button now captures the result and shows a Toast
                Button(
                    onClick = {
                        val file = PdfExporter.exportTimetableToPdf(context, scheduleData)
                        if (file != null) {
                            Toast.makeText(context, "PDF Exported to Downloads folder", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Export failed. Please check permissions.", Toast.LENGTH_SHORT).show()
                        }
                    },
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
    val days = listOf("", "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

    val morningSlots = listOf("7.30-8.00", "8.00-8.30", "8.30-9.00", "9.00-9.30", "9.30-10.00")
    val afternoonSlots = listOf(
        "10.30-11.00", "11.00-11.30", "11.30-12.00",
        "12.00-12.30", "12.30-1.00", "1.00-1.30", "1.30-2.00"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
            .padding(4.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
            days.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 10.sp,
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
                        .height(25.dp)
                        .background(Color(0xFFD1D1D1), RoundedCornerShape(4.dp))
                        .border(0.5.dp, Color.Black, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("REHAT", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.DarkGray)
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
    val dayHeaders = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    val cellBgColor = Color(0xFFFDF5E6)

    Row(
        modifier = Modifier.fillMaxWidth().height(55.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            modifier = Modifier.weight(1f),
            fontSize = 7.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            lineHeight = 8.sp,
            color = Color.Black
        )

        dayHeaders.forEach { day ->
            val entry = scheduleData.find { item ->
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

                dayMatch && (savedTime == slotStart || "0$savedTime" == slotStart || savedTime == "0$slotStart")
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(1.dp)
                    .background(
                        if (entry != null) cellBgColor else Color.White.copy(alpha = 0.3f),
                        RoundedCornerShape(2.dp)
                    )
                    .border(
                        if (entry != null) 0.5.dp else 0.1.dp,
                        if (entry != null) Color.Black else Color.Gray,
                        RoundedCornerShape(2.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (entry != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(1.dp)
                    ) {
                        val iconToDraw = when {
                            entry.subject.equals("Perhimpunan", ignoreCase = true) -> R.drawable.perhimpunan_icon
                            else -> entry.iconRes
                        }

                        if (iconToDraw != null) {
                            Image(
                                painter = painterResource(id = iconToDraw),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = getSubjectAlias(entry.subject),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            lineHeight = 9.sp,
                            maxLines = 1,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}